import React, { useState } from 'react';
import { login, register } from '../../services/api';
import { useNavigate } from 'react-router-dom';
import GradientButton from '../atoms/gradientButton/GradientButton';
import styles from './Auth.module.css';
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const AuthForm = () => {
    const navigate = useNavigate();
    const [isRegistering, setIsRegistering] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [isAdminRegistering, setIsAdminRegistering] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!username || !password) {
            setError('Username and password are required');
            return;
        }

        if (!/^[a-zA-Z0-9_]+$/.test(username)) {
            setError('Username can only contain Latin letters, digits, and underscores');
            return;
        }

        setIsLoading(true);
        try {
            let response;
        
            if (isRegistering) {
                response = await register(username, password, isAdminRegistering);
            } else {
                response = await login(username, password);
            }
        
            localStorage.setItem("token", response.token);
            localStorage.setItem("role", response.role);
        
            toast.success(isRegistering ? "Registration successful!" : "Login successful!");
            setUsername("");
            setPassword("");
            setError("");
            navigate("/dashboard");
        } catch (err) {
            toast.error(err.message || "An error occurred. Please try again.");
            setIsLoading(false);
        }
        
    };

    return (
        <div className={styles.container}>
            <ToastContainer />

            <form onSubmit={handleSubmit} className={styles.form}>
                <h2 className={styles.header}>
                    {isRegistering ? (isAdminRegistering ? 'Admin Register' : 'Register') : 'Login'}
                </h2>

                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => {
                        setUsername(e.target.value);
                        setError(''); // Очистка ошибки
                    }}
                    className={styles.input}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => {
                        setPassword(e.target.value);
                        setError(''); // Очистка ошибки
                    }}
                    className={styles.input}
                />
                {error && (
                    <div className={styles.error}>
                        <h3>Error</h3>
                        <p>
                            {error === 'Username and password are required'
                                ? 'Please fill in both username and password fields.'
                                : error === 'Username can only contain Latin letters, digits, and underscores'
                                    ? 'Ensure your username uses only allowed characters (letters, digits, underscores).'
                                    : error === 'Invalid credentials'
                                        ? 'The username or password you entered is incorrect. Please try again.'
                                        : error.includes('Network Error')
                                            ? 'There seems to be a problem with the network. Please check your connection.'
                                            : 'An unexpected error occurred. Please try again later.'}
                        </p>
                    </div>
                )}

                {isRegistering && (
                    <div className={styles.adminOption}>
                        <label>
                            <input
                                type="checkbox"
                                checked={isAdminRegistering}
                                onChange={(e) => setIsAdminRegistering(e.target.checked)}
                            />
                            Register as Admin
                        </label>
                    </div>
                )}

                <p>
                    {isRegistering ? (
                        <>
                            Already have an account?{' '}
                            <span
                                className={styles.link}
                                onClick={() => {
                                    setIsRegistering(false);
                                    setIsAdminRegistering(false);
                                    setError('');
                                }}
                            >
                                Login here
                            </span>
                        </>
                    ) : (
                        <>
                            Don't have an account?{' '}
                            <span
                                className={styles.link}
                                onClick={() => {
                                    setIsRegistering(true);
                                    setError('');
                                }}
                            >
                                Register here
                            </span>
                        </>
                    )}
                </p>

                <GradientButton
                    text={isLoading ? 'Loading...' : isRegistering ? 'Register' : 'Login'}
                    disabled={isLoading} // Блокировка кнопки во время загрузки
                />
            </form>
        </div>
    );
};

export default AuthForm;
