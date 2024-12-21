import React from "react";
import GradientButton from "../../atoms/gradientButton/GradientButton"; // Импорт кнопки
import styles from "./ProfileMenu.module.css";

const ProfileMenu = ({ username, isAdmin, onLogout, onViewAdminRequests }) => {
    return (
        <div className={styles.profileMenu}>
            <p>Username: {username}</p>
            {isAdmin && (
                <GradientButton text="Admin Requests" onClick={onViewAdminRequests} />
            )}
            <GradientButton text="Logout" onClick={onLogout} />
        </div>
    );
};

export default ProfileMenu;
