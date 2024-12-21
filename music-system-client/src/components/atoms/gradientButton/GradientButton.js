import React, { useState } from "react";
import styles from "./ButtonEffect.module.css";

const GradientButton = ({ text, onClick }) => {
    const [gradientPosition, setGradientPosition] = useState({ x: 50, y: 50 });

    const handleMouseMove = (event) => {
        const rect = event.target.getBoundingClientRect();
        const x = ((event.clientX - rect.left) / rect.width) * 100;
        const y = ((event.clientY - rect.top) / rect.height) * 100;

        setGradientPosition({ x, y });
    };

    return (
        <button
            className={styles.button}
            style={{
                background: `radial-gradient(circle at ${gradientPosition.x}% ${gradientPosition.y}%, #740938, #feb47b)`,
            }}
            onMouseMove={handleMouseMove}
            onClick={onClick} // Поддержка события клика
        >
            {text}
        </button>
    );
};

export default GradientButton;
