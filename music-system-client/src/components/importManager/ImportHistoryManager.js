// ImportHistoryManager.jsx

import React, { useEffect, useState } from "react";
import styles from "./ImportHistoryManager.module.css";
import { getImportHistory, downloadImportFile } from "../../services/api";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const ImportHistoryManager = () => {
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [websocketMessage, setWebsocketMessage] = useState("");

    // Получение токена и роли пользователя
    const token = localStorage.getItem("token"); // Замените на ваш ключ, если отличается
    const role = localStorage.getItem("role"); // Предполагается, что роль хранится здесь
    const isAdmin = role === "ADMIN";

    // Функция для скачивания файла импорта
    const handleDownload = async (historyId, fileName) => {
        try {
            const fileBlob = await downloadImportFile(historyId, token);
            // Создаём URL из Blob
            const url = window.URL.createObjectURL(new Blob([fileBlob]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", fileName || "imported_file.json"); // Имя файла
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
            window.URL.revokeObjectURL(url); // Освобождаем память
            toast.success("File downloaded successfully!");
        } catch (err) {
            toast.error("Failed to download file.");
            console.error("Error downloading file:", err);
        }
    };

    // Функция для получения истории импорта
    const fetchHistory = async () => {
        try {
            const data = await getImportHistory(token, isAdmin);
            setHistory(data);
        } catch (err) {
            setError("Failed to fetch import history");
            console.error(err);
            toast.error("Failed to fetch import history");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchHistory();

        // Настройка WebSocket
        const socket = new SockJS("http://localhost:8080/ws");
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            stompClient.subscribe("/topic/history", (message) => {
                const data = JSON.parse(message.body);
                console.log("WebSocket message received:", data);

                if (data.action === "user-history" || data.action === "all-history") {
                    setWebsocketMessage(data.message);

                    // Обновляем историю на основе новых данных
                    setHistory(data.data);
                    toast.info(data.message);
                }
            });
        });

        return () => {
            stompClient.disconnect(() => {
                console.log("WebSocket disconnected");
            });
        };
    }, [token, isAdmin]); // Добавляем зависимости

    if (loading) {
        return (
            <div className={styles.container}>
                <ToastContainer />
                <p>Loading...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className={styles.container}>
                <ToastContainer />
                <p className={styles.error}>{error}</p>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <ToastContainer />
            <h2>Import History</h2>
            {websocketMessage && <p className={styles.websocketMessage}>{websocketMessage}</p>}
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Status</th>
                        <th>Username</th>
                        <th>Added Objects</th>
                        <th>Timestamp</th>
                        <th>File Name</th>
                        <th>Actions</th> {/* Новая колонка */}
                    </tr>
                </thead>
                <tbody>
                    {history.length > 0 ? (
                        history.map((item) => (
                            <tr key={item.id}>
                                <td>{item.id}</td>
                                <td>{item.status}</td>
                                <td>{item.username}</td>
                                <td>{item.status === "SUCCESS" ? item.addedObjects : "N/A"}</td>
                                <td>{new Date(item.timestamp).toLocaleString()}</td>
                                <td>{item.fileName || "N/A"}</td>
                                <td>
                                    {item.fileObjectName ? (
                                        <button
                                            onClick={() => handleDownload(item.id, item.fileName)}
                                            className={styles.downloadButton}
                                        >
                                            Download File
                                        </button>
                                    ) : (
                                        "No File"
                                    )}
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="7" className={styles.noData}>
                                No import history available.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default ImportHistoryManager;
