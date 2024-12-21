import React, { useState, useEffect } from "react";
import { getLabels, createLabel, updateLabel, deleteLabel } from "../../services/api";
import styles from "./LabelManager.module.css";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import ConfirmModal from "../../components/molecules/confirmModal/ConfirmModal";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const LabelManager = () => {
    const [labels, setLabels] = useState([]);
    const [newLabel, setNewLabel] = useState({ name: "", bands: 0 });
    const [editingId, setEditingId] = useState(null);
    const [error, setError] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [labelToDelete, setLabelToDelete] = useState(null);
    const [fieldErrors, setFieldErrors] = useState({});

    const fetchLabels = async () => {
        try {
            const data = await getLabels();
            setLabels(data);
        } catch (error) {
            toast.error("Failed to load labels.");
        }
    };


    useEffect(() => {
        fetchLabels();
    }, []);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewLabel((prev) => ({ ...prev, [name]: value }));
    };
    const handleSave = async () => {
        const errors = {};

        if (!newLabel.name.trim()) {
            errors.name = "Name is required.";
        }
        if (newLabel.bands <= 0) {
            errors.bands = "Number of bands must be greater than 0.";
        }

        if (Object.keys(errors).length > 0) {
            setFieldErrors(errors);
            toast.error("Please correct the errors.");
            return;
        }

        try {
            if (editingId) {
                await updateLabel(editingId, newLabel);
            } else {
                await createLabel(newLabel);
            }
            setNewLabel({ name: "", bands: 0 });
            setEditingId(null);
            fetchLabels();
            toast.success("Label saved successfully!");
        } catch (error) {
            toast.error("Failed to save label.");
        }
    };

    const handleEdit = (label) => {
        setNewLabel(label);
        setEditingId(label.id);
    };

    const handleDelete = async () => {
        try {
            await deleteLabel(labelToDelete);
            setIsModalOpen(false);
            fetchLabels();
        } catch (error) {
            setError("Failed to delete label.");
        }
    };
    const closeModal = () => {
        setIsModalOpen(false);
        setLabelToDelete(null);
    };

    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws");
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            stompClient.subscribe("/topic/labels", (message) => {
                try {
                    const updatedLabel = JSON.parse(message.body);
                    setLabels((prevLabels) => {
                        if (updatedLabel.action === "delete") {
                            return prevLabels.filter((label) => label.id !== updatedLabel.id);
                        }

                        const existingIndex = prevLabels.findIndex((label) => label.id === updatedLabel.id);
                        if (existingIndex !== -1) {
                            const updatedLabels = [...prevLabels];
                            updatedLabels[existingIndex] = updatedLabel;
                            return updatedLabels;
                        }

                        return [...prevLabels, updatedLabel];
                    });
                } catch (err) {
                    console.error("Failed to parse WebSocket message for labels:", err);
                }
            });
        });

        return () => {
            stompClient.disconnect();
        };
    }, []);

    const confirmDelete = (id) => {
        setLabelToDelete(id);
        setIsModalOpen(true);
    };

    return (
        <div className={styles.container}>
            <ToastContainer />
            <h2>Manage Labels</h2>
            {error && <p className={styles.error}>{error}</p>}
            <div className={styles.form}>
                <label>
                    Name:
                    <input
                        type="text"
                        name="name"
                        value={newLabel.name}
                        onChange={handleInputChange}
                        className={fieldErrors.name ? styles.errorInput : ""}
                        placeholder="Label name"
                    />
                    {fieldErrors.name && <p className={styles.errorMessage}>{fieldErrors.name}</p>}
                </label>
                <label>
                    Bands:
                    <input
                        type="number"
                        name="bands"
                        value={newLabel.bands || ""}
                        onChange={handleInputChange}
                        className={fieldErrors.bands ? styles.errorInput : ""}
                        placeholder="Number of bands"
                    />
                    {fieldErrors.bands && <p className={styles.errorMessage}>{fieldErrors.bands}</p>}
                </label>

                <button onClick={handleSave}>
                    {editingId ? "Update Label" : "Create Label"}
                </button>
            </div>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Bands</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {labels.length > 0 ? (
                        labels.map((label) => (
                            <tr key={label.id}>
                                <td>{label.id}</td>
                                <td>{label.name}</td>
                                <td>{label.bands}</td>
                                <td>
                                    <button onClick={() => handleEdit(label)}>Edit</button>
                                    <button onClick={() => confirmDelete(label.id)}>Delete</button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="4">No labels available</td>
                        </tr>
                    )}
                </tbody>
            </table>
            <ConfirmModal
                isOpen={isModalOpen}
                message="Are you sure you want to delete this label?"
                onConfirm={handleDelete}
                onCancel={closeModal}
            />

        </div>
    );
};

export default LabelManager;
