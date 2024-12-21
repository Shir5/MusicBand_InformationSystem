import React from 'react';
import { motion } from 'framer-motion';
import styles from './ConfirmModal.module.css';

const ConfirmModal = ({ isOpen, onConfirm, onCancel, message }) => {
    if (!isOpen) return null;

    return (
        <motion.div
            className={styles.modalBackdrop}
            initial={{ opacity: 0 }} // Начальное состояние фона
            animate={{ opacity: 1 }} // Конечное состояние фона
            exit={{ opacity: 0 }} // Состояние при выходе
            transition={{ duration: 0.1 }} // Длительность анимации
            onClick={onCancel} // Закрытие при клике на фон
        >
            <motion.div
                className={styles.modalContent}
                initial={{ scale: 0.8, opacity: 0 }} // Начальное состояние модального окна
                animate={{ scale: 1, opacity: 1 }} // Конечное состояние
                exit={{ scale: 0.8, opacity: 0 }} // Состояние при выходе
                transition={{ duration: 0.1 }} // Длительность анимации
                onClick={(e) => e.stopPropagation()} // Предотвращение закрытия при клике на содержимое
            >
                <p>{message}</p>
                <div className={styles.modalActions}>
                    <button className={styles.confirmButton} onClick={onConfirm}>
                        Yes
                    </button>
                    <button className={styles.cancelButton} onClick={onCancel}>
                        No
                    </button>
                </div>
            </motion.div>
        </motion.div>
    );
};

export default ConfirmModal;
