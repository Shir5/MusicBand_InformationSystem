import { jwtDecode } from "jwt-decode";

export const isTokenValid = () => {
    const token = localStorage.getItem('token');
    if (!token) return false;

    try {
        const { exp } = jwtDecode(token); // Извлекаем время истечения токена
        return Date.now() < exp * 1000; // Проверяем, истёк ли токен
    } catch (error) {
        console.error("Ошибка при декодировании токена:", error);
        return false;
    }

};
