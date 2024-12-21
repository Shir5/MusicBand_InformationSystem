import React, { useState, useEffect, useCallback } from "react";
import { getBands, createBand, deleteBand, updateBand, getAlbums, getLabels } from "../../services/api";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEdit, faTrash, faSortUp, faSortDown } from "@fortawesome/free-solid-svg-icons";
import styles from "./BandManager.module.css";
import FilterInput from "../../components/atoms/filterInput/FilterInput";
import { Stomp } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import ConfirmModal from "../../components/molecules/confirmModal/ConfirmModal";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const BandManager = () => {
    const [bands, setBands] = useState([]);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [fieldErrors, setFieldErrors] = useState({});
    const [albums, setAlbums] = useState([]);
    const [labels, setLabels] = useState([]);
    const [filterQuery, setFilterQuery] = useState("");
    const [filterColumn, setFilterColumn] = useState("name");
    const [sortColumn, setSortColumn] = useState("id");
    const [sortDirection, setSortDirection] = useState("asc");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [bandToDelete, setBandToDelete] = useState(null);

    const handleSort = (column) => {
        if (sortColumn === column) {
            setSortDirection(sortDirection === "asc" ? "desc" : "asc");
        } else {
            setSortColumn(column);
            setSortDirection("asc");
        }
    };

    const filteredBands = bands.filter((band) =>
        band[filterColumn]?.toString().toLowerCase().includes(filterQuery.toLowerCase())
    );

    const sortedBands = [...filteredBands].sort((a, b) => {
        let valA = a[sortColumn];
        let valB = b[sortColumn];

        if (sortColumn === "coordinates") {
            valA = `${a.coordinates?.x || 0},${a.coordinates?.y || 0}`;
            valB = `${b.coordinates?.x || 0},${b.coordinates?.y || 0}`;
        }

        if (typeof valA === "string" && typeof valB === "string") {
            return sortDirection === "asc" ? valA.localeCompare(valB) : valB.localeCompare(valA);
        }

        return sortDirection === "asc" ? valA - valB : valB - valA;
    });

    const [newBand, setNewBand] = useState({
        name: "",
        x: 0,
        y: 0,
        genre: "",
        numberOfParticipants: 0,
        albumsCount: 0,
        singlesCount: 0,
        description: "",
        establishmentDate: "",
    });

    const fetchAlbums = async () => {
        try {
            const data = await getAlbums();
            setAlbums(data);
        } catch (err) {
            setError("Failed to load albums.");
            console.error("Error fetching albums:", err);
        }
    };

    const fetchLabels = useCallback(async () => {
        try {
            const data = await getLabels();
            setLabels(data);
        } catch (err) {
            setError("Failed to load labels.");
            console.error("Error fetching labels:", err);
        }
    }, []);

    const fetchBands = useCallback(async () => {
        setLoading(true);
        setError("");
        try {
            const data = await getBands(page, size);
            setBands(data.content || []);
            setTotalPages(data.totalPages || 0);
        } catch (err) {
            setError("Failed to load data.");
            console.error("Error fetching bands:", err);
        } finally {
            setLoading(false);
        }
    }, [page, size]);

    const resetForm = () => {
        setNewBand({
            name: "",
            x: 0,
            y: 0,
            genre: "",
            numberOfParticipants: 0,
            albumsCount: 0,
            singlesCount: 0,
            description: "",
            establishmentDate: "",
        });
        setShowForm(false);
    };

    useEffect(() => {
        fetchBands();
        fetchLabels();
        fetchAlbums();
    }, [fetchBands, fetchLabels]);

    const handleEditClick = (band) => {
        setNewBand({
            ...band,
            x: band.coordinates.x,
            y: band.coordinates.y,
        });
        setShowForm(true);
    };

    const openModal = (id) => {
        setBandToDelete(id);
        setIsModalOpen(true);
    };

    const handleDeleteConfirm = async () => {
        if (bandToDelete) {
            try {
                await deleteBand(bandToDelete);
                setBands((prev) => prev.filter((band) => band.id !== bandToDelete));
            } catch (err) {
                setError("Failed to delete band.");
                console.error("Error deleting band:", err);
            } finally {
                setIsModalOpen(false);
                setBandToDelete(null);
            }
        }
    };

    const closeModal = () => {
        setIsModalOpen(false);
        setBandToDelete(null);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewBand((prev) => ({
            ...prev,
            [name]: ["x", "y", "numberOfParticipants", "albumsCount", "singlesCount"].includes(name)
                ? parseFloat(value) || 0
                : value || "",
        }));
    };
    const handleSubmit = async () => {
        const newFieldErrors = {};
    
        if (!newBand.name.trim()) {
            newFieldErrors.name = "Name is required.";
        }
        if (newBand.x < -495) {
            newFieldErrors.x = "X coordinate must be greater than -495.";
        }
        if (!newBand.genre) {
            newFieldErrors.genre = "Genre is required.";
        }
        if (newBand.numberOfParticipants <= 0) {
            newFieldErrors.numberOfParticipants = "Number of participants must be greater than 0.";
        }
    
        if (Object.keys(newFieldErrors).length > 0) {
            setFieldErrors(newFieldErrors);
            toast.error("Please correct the highlighted errors.");
            return;
        }
    
        try {
            const formattedBand = {
                ...newBand,
                coordinates: { x: newBand.x, y: newBand.y },
                establishmentDate: new Date(newBand.establishmentDate).toISOString(),
                bestAlbumId: newBand.bestAlbum ? parseInt(newBand.bestAlbum) : null,
                labelId: newBand.labelId ? parseInt(newBand.labelId) : null,
            };
    
            let updatedBand;
            if (newBand.id) {
                updatedBand = await updateBand(newBand.id, formattedBand);
            } else {
                updatedBand = await createBand(formattedBand);
            }
    
            setBands((prev) => [...prev.filter((band) => band.id !== updatedBand.id), updatedBand]);
            resetForm();
            toast.success("Band saved successfully!");
        } catch (err) {
            toast.error("Failed to save band.");
            console.error("Error saving band:", err);
        }
    };
    
    useEffect(() => {
        const socket = new SockJS("http://localhost:8080/ws");
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, () => {
            stompClient.subscribe("/topic/bands", (message) => {
                try {
                    const data = JSON.parse(message.body);
                    if (data.action === "delete") {
                        const idToDelete = data.id;
                        setBands((prevBands) => prevBands.filter((band) => band.id !== idToDelete));
                    } else if (data.id) {
                        setBands((prevBands) => {
                            const existingIndex = prevBands.findIndex((band) => band.id === data.id);
                            if (existingIndex !== -1) {
                                const updatedBands = [...prevBands];
                                updatedBands[existingIndex] = data;
                                return updatedBands;
                            }
                            return [...prevBands, data];
                        });
                    }
                } catch (err) {
                    console.error("Failed to parse WebSocket message:", err);
                }
            });
        });

        return () => stompClient.disconnect();
    }, []);

    // Обработчики переключения страниц
    const handlePreviousPage = () => {
        if (page > 0) {
            setPage(page - 1);
        }
    };

    const handleNextPage = () => {
        if (page < totalPages - 1) {
            setPage(page + 1);
        }
    };

    return (
        
        <div className={styles.container}>
            <ToastContainer />
            <h1 style={{ color: "#f4f4f4" }}>Dashboard</h1>
            {error && <p className={styles.error}>{error}</p>}
            <ConfirmModal
                isOpen={isModalOpen}
                onConfirm={handleDeleteConfirm}
                onCancel={closeModal}
                message="Are you sure you want to delete this band?"
            />
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <button
                        className={showForm ? styles.cancelButton : styles.addButton}
                        onClick={() => {
                            if (showForm) {
                                resetForm(); // Сброс формы при отмене
                            } else {
                                setShowForm(true); // Показ формы при добавлении
                            }
                        }}
                    >
                        {showForm ? "Cancel" : "Add Music Band"}
                    </button>

                    {showForm && (
                        <div className={styles.form}>
                            <h2>{newBand.id ? "Update Music Band" : "Create Music Band"}</h2>

                            <label>
                                Name:
                                <input
                                    type="text"
                                    name="name"
                                    value={newBand.name}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Enter band name"
                                />
                                {fieldErrors.name && <p className={styles.error}>{fieldErrors.name}</p>}
                            </label>

                            <label>
                                Coordinates (X):
                                <input
                                    type="number"
                                    name="x"
                                    value={newBand.x}
                                    onChange={handleInputChange}
                                    required
                                />
                                {fieldErrors.x && <p className={styles.error}>{fieldErrors.x}</p>}
                            </label>

                            <label>
                                Coordinates (Y):
                                <input
                                    type="number"
                                    name="y"
                                    value={newBand.y}
                                    onChange={handleInputChange}
                                    required
                                    min={-495}
                                />
                                {fieldErrors.y && <p className={styles.error}>{fieldErrors.y}</p>}
                            </label>

                            <label>
                                Genre:
                                <select
                                    name="genre"
                                    value={newBand.genre}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Select Genre</option>
                                    <option value="PSYCHEDELIC_ROCK">PSYCHEDELIC_ROCK</option>
                                    <option value="SOUL">SOUL</option>
                                    <option value="MATH_ROCK">MATH_ROCK</option>
                                    <option value="POST_PUNK">POST_PUNK</option>
                                </select>
                                {fieldErrors.genre && <p className={styles.error}>{fieldErrors.genre}</p>}
                            </label>

                            <label>
                                Number of Participants:
                                <input
                                    type="number"
                                    name="numberOfParticipants"
                                    value={newBand.numberOfParticipants}
                                    onChange={handleInputChange}
                                    required
                                    min={1}
                                />
                                {fieldErrors.numberOfParticipants && <p className={styles.error}>{fieldErrors.numberOfParticipants}</p>}
                            </label>

                            <label>
                                Albums Count:
                                <input
                                    type="number"
                                    name="albumsCount"
                                    value={newBand.albumsCount || ""}
                                    onChange={handleInputChange}
                                    min={1}
                                />
                                {fieldErrors.albumsCount && <p className={styles.error}>{fieldErrors.albumsCount}</p>}
                            </label>

                            <label>
                                Singles Count:
                                <input
                                    type="number"
                                    name="singlesCount"
                                    value={newBand.singlesCount || ""}
                                    onChange={handleInputChange}
                                    min={1}
                                />
                                {fieldErrors.singlesCount && <p className={styles.error}>{fieldErrors.singlesCount}</p>}
                            </label>
                            <label>
                                Description:
                                <textarea
                                    name="description"
                                    value={newBand.description}
                                    onChange={handleInputChange}
                                    required
                                    style={{
                                        resize: "none", // Запрещает изменение размера
                                        width: "100%", // Полная ширина
                                        height: "80px", // Фиксированная высота для единообразия
                                    }}
                                />
                                {fieldErrors.description && <p className={styles.error}>{fieldErrors.description}</p>}
                            </label>

                            <label>
                                Best Album:
                                <select
                                    name="bestAlbum"
                                    value={newBand.bestAlbum || ""}
                                    onChange={(e) => {
                                        setNewBand((prev) => ({
                                            ...prev,
                                            bestAlbum: e.target.value || null, // Установите null, если ничего не выбрано
                                        }));
                                    }}
                                >
                                    <option value="">Select Album</option>
                                    {albums.map((album) => (
                                        <option key={album.id} value={album.id}>
                                            {album.name}
                                        </option>
                                    ))}
                                </select>
                            </label>
                            {fieldErrors.bestAlbum && <p className={styles.error}>{fieldErrors.bestAlbum}</p>}


                            <label>
                                Establishment Date:
                                <input
                                    type="date"
                                    name="establishmentDate"
                                    value={newBand.establishmentDate}
                                    onChange={handleInputChange}
                                    required
                                />
                                {fieldErrors.establishmentDate && <p className={styles.error}>{fieldErrors.establishmentDate}</p>}
                            </label>
                            <label>
                                Label:
                                <select
                                    name="labelId"
                                    value={newBand.labelId}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Select Label</option>
                                    {labels.map((label) => (
                                        <option key={label.id} value={label.id}>
                                            {label.name}
                                        </option>
                                    ))}
                                </select>
                                {fieldErrors.labelId && (
                                    <p className={styles.error}>{fieldErrors.labelId}</p>
                                )}
                            </label>
                            <button onClick={handleSubmit}>
                                {newBand.id ? "Update Band" : "Create Band"}
                            </button>
                        </div>

                    )}

                    <div className={styles.selectWrapper}>
                        <label htmlFor="pageSize">Items per page:</label>
                        <select
                            id="pageSize"
                            value={size}
                            onChange={(e) => setSize(Number(e.target.value))}
                        >
                            <option value={5}>5</option>
                            <option value={10}>10</option>
                            <option value={20}>20</option>
                        </select>
                    </div>
                    <FilterInput
                        filterQuery={filterQuery}
                        setFilterQuery={setFilterQuery}
                        filterColumn={filterColumn}
                        setFilterColumn={setFilterColumn}
                    />
                    <table className={styles.table}>
                        <thead>
                            <tr>
                                <th onClick={() => handleSort("id")}>
                                    ID{" "}
                                    {sortColumn === "id" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("name")}>
                                    Name{" "}
                                    {sortColumn === "name" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("coordinates")}>
                                    Coordinates{" "}
                                    {sortColumn === "coordinates" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("creationDate")}>
                                    Creation Date{" "}
                                    {sortColumn === "creationDate" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("genre")}>
                                    Genre{" "}
                                    {sortColumn === "genre" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("numberOfParticipants")}>
                                    Participants{" "}
                                    {sortColumn === "numberOfParticipants" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("singlesCount")}>
                                    Singles Count{" "}
                                    {sortColumn === "singlesCount" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("description")}>
                                    Description{" "}
                                    {sortColumn === "description" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("bestAlbumId")}>
                                    Best Album{" "}
                                    {sortColumn === "bestAlbumId" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("albumsCount")}>
                                    Albums Count{" "}
                                    {sortColumn === "albumsCount" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("establishmentDate")}>
                                    Establishment Date{" "}
                                    {sortColumn === "establishmentDate" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                                <th onClick={() => handleSort("labelId")}>
                                    Label{" "}
                                    {sortColumn === "labelId" && (
                                        <FontAwesomeIcon icon={sortDirection === "asc" ? faSortUp : faSortDown} />
                                    )}
                                </th>
                            </tr>
                        </thead>

                        <tbody>
                            {sortedBands.length > 0 ? (
                                sortedBands.map((band) => (
                                    <tr key={band.id}>
                                        <td>{band.id}</td>
                                        <td>{band.name}</td>
                                        <td>
                                            {band.coordinates
                                                ? `(${band.coordinates.x}, ${band.coordinates.y})`
                                                : "N/A"}
                                        </td>
                                        <td>{band.creationDate ? new Date(band.creationDate).toLocaleDateString() : "N/A"}</td>
                                        <td>{band.genre}</td>
                                        <td>{band.numberOfParticipants}</td>
                                        <td>{band.singlesCount || "N/A"}</td>
                                        <td>{band.description}</td>
                                        <td>{band.bestAlbumId ? band.bestAlbumId : "N/A"}</td>
                                        <td>{band.albumsCount || "N/A"}</td>
                                        <td>
                                            {band.establishmentDate
                                                ? new Date(band.establishmentDate).toLocaleDateString()
                                                : "N/A"}
                                        </td>
                                        <td>{band.labelId || "N/A"}</td>
                                        <td>
                                            <button
                                                className={styles.iconButton}
                                                onClick={() => handleEditClick(band)}
                                                title="Edit"
                                            >
                                                <FontAwesomeIcon icon={faEdit} />
                                            </button>
                                            <button
                                                className={styles.iconButton}
                                                onClick={() => openModal(band.id)}
                                                title="Delete"
                                            >
                                                <FontAwesomeIcon icon={faTrash} />
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="12" className={styles.noData}>
                                        No data available
                                    </td>
                                </tr>
                            )}
                        </tbody>


                    </table>

                    <div className={styles.pagination}>
                        <button
                            onClick={handlePreviousPage}
                            disabled={page === 0}
                            className={styles.pageButton}
                        >
                            Previous
                        </button>
                        <span>
                            Page {page + 1} of {totalPages}
                        </span>
                        <button
                            onClick={handleNextPage}
                            disabled={page >= totalPages - 1}
                            className={styles.pageButton}
                        >
                            Next
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default BandManager;


