import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUserCircle } from "@fortawesome/free-solid-svg-icons";
import { motion, AnimatePresence } from "framer-motion";
import BandManager from "../bandManager/BandManager";
import AlbumManager from "../album/AlbumManager";
import LabelManager from "../label/LabelManager";
import AdminRequestsManager from "../molecules/adminRequestManager/AdminRequestsManager";
import styles from "./Dashboard.module.css";
import ProfileMenu from "../molecules/profileMenu/ProfileMenu";
import SpecialRequests from "../specialRequests/SpecialRequests";
const Dashboard = () => {
    const [isMenuVisible, setIsMenuVisible] = useState(false);
    const [isAdmin, setIsAdmin] = useState(localStorage.getItem("role") === "ADMIN");
    const [currentPath, setCurrentPath] = useState("albums");
    const [direction, setDirection] = useState(0); // 1 - вправо, -1 - влево

    const [username, setUsername] = useState("");

    const pagesOrder = ["bands", "albums", "labels", "special-requests"]; // Добавили "special-requests"

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            const payload = JSON.parse(atob(token.split(".")[1]));
            setUsername(payload.sub || "Unknown User");
        }
    }, []);

    const handleClick = () => {
        setIsMenuVisible(!isMenuVisible);
    };

    const handleNavigation = (path) => {
        if (path !== currentPath) {
            const currentIndex = pagesOrder.indexOf(currentPath);
            const nextIndex = pagesOrder.indexOf(path);

            setDirection(nextIndex > currentIndex ? 1 : -1); // Направление вправо или влево
            setCurrentPath(path);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        window.location.href = "/auth";
    };

    const renderContent = () => {
        switch (currentPath) {
            case "bands":
                return <BandManager />;
            case "albums":
                return <AlbumManager />;
            case "labels":
                return <LabelManager />;
            case "special-requests":
                return <SpecialRequests />;
            case "admin-requests":
                return <AdminRequestsManager />;
            default:
                return <h2>Select a section from the menu above</h2>;
        }
    };
    

    const pageVariants = {
        initial: (direction) => ({
            opacity: 0,
            x: direction > 0 ? 100 : -100, // Движение вправо или влево
        }),
        animate: {
            opacity: 1,
            x: 0,
        },
        exit: (direction) => ({
            opacity: 0,
            x: direction > 0 ? -100 : 100, // Движение вправо или влево
        }),
    };

    return (
        <div className={styles.dashboard}>
            <nav className={styles.navbar}>
                <ul className={styles.navLinks}>
                    <li>
                        <Link
                            to="bands"
                            className={currentPath === "bands" ? styles.active : ""}
                            onClick={() => handleNavigation("bands")}
                        >
                            Bands
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="albums"
                            className={currentPath === "albums" ? styles.active : ""}
                            onClick={() => handleNavigation("albums")}
                        >
                            Albums
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="labels"
                            className={currentPath === "labels" ? styles.active : ""}
                            onClick={() => handleNavigation("labels")}
                        >
                            Labels
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="special-requests"
                            className={currentPath === "special-requests" ? styles.active : ""}
                            onClick={() => handleNavigation("special-requests")}
                        >
                            Special Requests
                        </Link>
                    </li>

                </ul>
                <div
                    className={styles.profileIconContainer}
                    onClick={handleClick}
                    style={{ cursor: "pointer" }}
                >
                    <FontAwesomeIcon icon={faUserCircle} size="2x" />
                    {isMenuVisible && (
                        <ProfileMenu
                            username={username}
                            isAdmin={isAdmin}
                            onLogout={handleLogout}
                            onViewAdminRequests={() => handleNavigation("admin-requests")}
                        />
                    )}
                </div>
            </nav>

            <div className={styles.content}>
                <AnimatePresence mode="wait" custom={direction}>
                    <motion.div
                        key={currentPath}
                        variants={pageVariants}
                        initial="initial"
                        animate="animate"
                        exit="exit"
                        custom={direction}
                        transition={{ duration: 0.5 }}
                    >
                        {renderContent()}
                    </motion.div>
                </AnimatePresence>
            </div>
        </div>
    );
};

export default Dashboard;
