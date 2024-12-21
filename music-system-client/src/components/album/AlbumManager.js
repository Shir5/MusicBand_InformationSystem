import React, { useState, useEffect } from "react";
import { getAlbums, createAlbum, updateAlbum, deleteAlbum } from "../../services/api";
import ConfirmModal from "../molecules/confirmModal/ConfirmModal";
import styles from "./AlbumManager.module.css";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const AlbumManager = () => {
    const [albums, setAlbums] = useState([]);
    const [newAlbum, setNewAlbum] = useState({ name: "", tracks: 0 });
    const [editingId, setEditingId] = useState(null);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [albumToDelete, setAlbumToDelete] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});

    const fetchAlbums = async () => {
        setLoading(true);
        try {
            const data = await getAlbums();
            setAlbums(data);
        } catch {
            setError("Failed to load albums.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAlbums();

        const socket = new SockJS("http://localhost:8080/ws");
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            stompClient.subscribe("/topic/albums", (message) => {
                try {
                    const updatedAlbum = JSON.parse(message.body);
                    setAlbums((prevAlbums) => {
                        if (updatedAlbum.action === "delete") {
                            return prevAlbums.filter((album) => album.id !== updatedAlbum.id);
                        }

                        const existingIndex = prevAlbums.findIndex((album) => album.id === updatedAlbum.id);
                        if (existingIndex !== -1) {
                            const updatedAlbums = [...prevAlbums];
                            updatedAlbums[existingIndex] = updatedAlbum;
                            return updatedAlbums;
                        }

                        return [...prevAlbums, updatedAlbum];
                    });
                } catch (err) {
                    console.error("Failed to parse WebSocket message for albums:", err);
                }
            });
        });

        return () => {
            stompClient.disconnect();
        };
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewAlbum((prev) => ({ ...prev, [name]: value }));
    };
    const handleSave = async () => {
        const errors = {};

        if (!newAlbum.name.trim()) {
            errors.name = "Name is required.";
        }
        if (newAlbum.tracks <= 0) {
            errors.tracks = "Number of tracks must be greater than 0.";
        }

        if (Object.keys(errors).length > 0) {
            setFieldErrors(errors);
            toast.error("Please correct the errors.");
            return;
        }

        try {
            if (editingId) {
                await updateAlbum(editingId, newAlbum);
            } else {
                await createAlbum(newAlbum);
            }
            setNewAlbum({ name: "", tracks: 0 });
            setEditingId(null);
            fetchAlbums();
            toast.success("Album saved successfully!");
        } catch {
            toast.error("Failed to save album.");
        }
    };


    const handleEdit = (album) => {
        setNewAlbum(album);
        setEditingId(album.id);
    };

    const confirmDelete = (id) => {
        setAlbumToDelete(id);
        setIsModalOpen(true);
    };

    const handleDelete = async () => {
        try {
            await deleteAlbum(albumToDelete);
            setIsModalOpen(false);
            fetchAlbums();
        } catch {
            setError("Failed to delete album.");
        }
    };

    return (
        <div className={styles.container}>
            <ToastContainer />
            <h2>Manage Albums</h2>
            {error && <p className={styles.error}>{error}</p>}
            <div className={styles.form}>
                <label>
                    Name:
                    <input
                        type="text"
                        name="name"
                        value={newAlbum.name}
                        onChange={handleInputChange}
                        className={fieldErrors.name ? styles.errorInput : ""}
                        placeholder="Album name"
                    />
                    {fieldErrors.name && <p className={styles.errorMessage}>{fieldErrors.name}</p>}
                </label>
                <label>
                    Tracks:
                    <input
                        type="number"
                        name="tracks"
                        value={newAlbum.tracks}
                        onChange={handleInputChange}
                        className={fieldErrors.tracks ? styles.errorInput : ""}
                        placeholder="Number of tracks"
                    />
                    {fieldErrors.tracks && <p className={styles.errorMessage}>{fieldErrors.tracks}</p>}
                </label>

                <button onClick={handleSave}>
                    {editingId ? "Update Album" : "Create Album"}
                </button>
            </div>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Tracks</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {albums.length > 0 ? (
                        albums.map((album) => (
                            <tr key={album.id}>
                                <td>{album.id}</td>
                                <td>{album.name}</td>
                                <td>{album.tracks}</td>
                                <td>
                                    <button onClick={() => handleEdit(album)}>Edit</button>
                                    <button onClick={() => confirmDelete(album.id)}>Delete</button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="4" className={styles.noData}>
                                No albums available
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
            <ConfirmModal
                isOpen={isModalOpen}
                message="Are you sure you want to delete this album?"
                onConfirm={handleDelete}
                onCancel={() => setIsModalOpen(false)}
            />
        </div>
    );
};

export default AlbumManager;
