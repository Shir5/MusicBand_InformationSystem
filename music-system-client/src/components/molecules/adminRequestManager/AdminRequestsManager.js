import React, { useEffect, useState } from "react";
import { getAdminRequests, approveAdminRequest, rejectAdminRequest } from "../../../services/api";
import styles from "./AdminRequestsManager.module.css";

const AdminRequestsManager = () => {
    const [requests, setRequests] = useState([]);
    const [error, setError] = useState("");

    const fetchRequests = async () => {
        try {
            const data = await getAdminRequests();
            setRequests(data);
            setError(""); // Сбрасываем ошибку, если запрос выполнен успешно
        } catch (err) {
            setError(err.message || "Failed to load admin requests.");
            console.error(err);
        }
    };

    useEffect(() => {
        // Начальный запрос
        fetchRequests();

        // Периодическое обновление каждые 5 секунд
        const intervalId = setInterval(fetchRequests, 5000);

        // Очищаем интервал при размонтировании компонента
        return () => clearInterval(intervalId);
    }, []);

    const handleApprove = async (id) => {
        try {
            await approveAdminRequest(id);
            setRequests((prev) => prev.filter((req) => req.id !== id));
        } catch (err) {
            setError("Failed to approve request.");
            console.error(err);
        }
    };

    const handleReject = async (id) => {
        try {
            await rejectAdminRequest(id);
            setRequests((prev) => prev.filter((req) => req.id !== id));
        } catch (err) {
            setError("Failed to reject request.");
            console.error(err);
        }
    };

    return (
        <div className={styles.container}>
            <h2>Admin Requests</h2>
            {error && <p className={styles.error}>{error}</p>}
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {requests.map((req) => (
                        <tr key={req.id}>
                            <td>{req.id}</td>
                            <td>{req.user.username}</td>
                            <td>{req.status}</td>
                            <td>
                                <button onClick={() => handleApprove(req.id)}>Approve</button>
                                <button onClick={() => handleReject(req.id)}>Reject</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminRequestsManager;
