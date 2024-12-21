import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true, // Если используется cookie для CORS
});

// Добавление JWT-токена в заголовок Authorization
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            console.log('Token sent in Authorization header:', token);
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        console.error('Error in request interceptor:', error);
        return Promise.reject(error);
    }
);

// Глобальная обработка ошибок ответа
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            console.error(`API error [${error.response.status}]: ${error.response.data?.message || error.response.statusText}`);
        } else if (error.request) {
            console.error('No response received from the server:', error.request);
        } else {
            console.error('Error setting up request:', error.message);
        }
        return Promise.reject(error);
    }
);

// Методы API
// Авторизация пользователя
export const login = async (username, password) => {
    try {
        const response = await api.post('/auth/login', { username, password });

        console.log('Login response:', response.data);

        // Сохраняем токен и роль в localStorage
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('role', response.data.role);

        console.log('Token and role saved in localStorage:', {
            token: response.data.token,
            role: response.data.role,
        });

        return response.data; // Возвращаем данные для дальнейшего использования
    } catch (error) {
        console.error('Login failed:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Login failed');
    }
};


// Регистрация пользователя
export const register = async (username, password, isAdminRequest = false) => {
    if (!username || !password) {
        throw new Error('Username and password are required');
    }

    if (!/^[a-zA-Z0-9_]+$/.test(username)) {
        throw new Error('Username can only contain Latin letters, digits, and underscores');
    }

    try {
        const response = await api.post('/auth/register', {
            username,
            password,
            isAdminRequest, 
        });
        return response.data;
    } catch (error) {
        throw new Error(error.response?.data?.message || 'Registration failed');
    }
};


// Получение списка групп
export const getBands = async (page, size, filter = '', sortField = 'id', sortOrder = 'ASC') => {
    try {
        const response = await api.get('/bands', {
            params: { page, size, filter, sortField, sortOrder },
        });
        return response.data;
    } catch (error) {
        console.error('Error fetching bands:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to fetch bands');
    }
};

// Получение списка лейблов
export const getLabels = async () => {
    try {
        const response = await api.get('/labels');
        return response.data;
    } catch (error) {
        console.error('Error fetching labels:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to fetch labels');
    }
};

// Получение списка альбомов
export const getAlbums = async () => {
    try {
        const response = await api.get('/albums');
        return response.data;
    } catch (error) {
        console.error('Error fetching albums:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to fetch albums');
    }
};

// Создание новой группы
export const createBand = async (data) => {
    try {
        console.log('Sending request to create band:', data);
        const response = await api.post('/bands', data);
        console.log('Response from server:', response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to create band:', error.response?.data || error.message);
        throw error;
    }
};
export const updateBand = async (id, band) => {
    try {
        console.log('Updating band with ID:', id, 'and data:', band);
        const response = await api.put(`/bands/${id}`, band);
        console.log('Response from update:', response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to update band:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Failed to update band');
    }
};
// Удаление группы
export const deleteBand = async (id) => {
    try {
        const response = await api.delete(`/bands/${id}`);
        return response.data;
    } catch (error) {
        console.error('Failed to delete band:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Failed to delete band');
    }
};



export const createLabel = async (data) => {
    try {
        const response = await api.post("/labels", data);
        return response.data;
    } catch (error) {
        throw new Error("Failed to create label.");
    }
};

export const updateLabel = async (id, data) => {
    try {
        const response = await api.put(`/labels/${id}`, data);
        return response.data;
    } catch (error) {
        throw new Error("Failed to update label.");
    }
};

export const deleteLabel = async (id) => {
    try {
        await api.delete(`/labels/${id}`);
    } catch (error) {
        throw new Error("Failed to delete label.");
    }
};

export const createAlbum = async (data) => {
    try {
        console.log('Sending request to create album:', data);
        const response = await api.post('/albums', data);
        console.log('Response from server:', response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to create album:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to create album');
    }
};
export const updateAlbum = async (id, album) => {
    try {
        console.log('Updating album with ID:', id, 'and data:', album);
        const response = await api.put(`/albums/${id}`, album);
        console.log('Response from update:', response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to update album:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Failed to update album');
    }
};

export const deleteAlbum = async (id) => {
    try {
        const response = await api.delete(`/albums/${id}`);
        return response.data;
    } catch (error) {
        console.error('Failed to delete album:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Failed to delete album');
    }
};

// Получение списка заявок администратора
export const getAdminRequests = async () => {
    try {
        console.log('Fetching admin requests...');
        const response = await api.get('/admin/requests');
        console.log('Admin requests fetched successfully:', response.data);
        return response.data; // Ожидаем, что сервер вернёт массив заявок
    } catch (error) {
        console.error('Failed to fetch admin requests:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to fetch admin requests');
    }
};

// Одобрение заявки администратора
export const approveAdminRequest = async (id) => {
    try {
        console.log(`Approving admin request with ID: ${id}`);
        const response = await api.post(`/admin/requests/${id}/approve`);
        console.log(`Admin request approved successfully for ID: ${id}`);
        return response.data;
    } catch (error) {
        console.error(`Failed to approve admin request with ID: ${id}`, error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to approve admin request');
    }
};

// Отклонение заявки администратора
export const rejectAdminRequest = async (id) => {
    try {
        console.log(`Rejecting admin request with ID: ${id}`);
        const response = await api.post(`/admin/requests/${id}/reject`);
        console.log(`Admin request rejected successfully for ID: ${id}`);
        return response.data;
    } catch (error) {
        console.error(`Failed to reject admin request with ID: ${id}`, error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to reject admin request');
    }
};
// Добавление сингла группе
export const addSingleToBand = async (bandId, singlesToAdd) => {
    try {
        const response = await api.post(`/database/add-single`, null, {
            params: { bandId, singlesToAdd },
        });
        console.log('Single added successfully:', response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to add single:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Failed to add single');
    }
};

// Добавление участника в группу
export const addParticipantToBand = async (bandId, participantsToAdd) => {
    try {
        const response = await api.post(`/database/add-participant`, null, {
            params: { bandId, participantsToAdd },
        });
        console.log('Participant added successfully:', response.data);
        return response.data;
    } catch (error) {
        console.error('Failed to add participant:', error.response?.data?.message || error.message);
        throw new Error(error.response?.data?.message || 'Failed to add participant');
    }
};
// Подсчёт лейблов с количеством групп больше заданного
export const countLabelsAboveThreshold = async (threshold) => {
    try {
        const response = await api.get(`/database/count-labels`, {
            params: { threshold },
        });
        return response.data;
    } catch (error) {
        console.error('Failed to count labels:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to count labels');
    }
};

// Поиск описаний по префиксу
export const findDescriptionsByPrefix = async (prefix) => {
    try {
        const response = await api.get(`/database/find-descriptions`, {
            params: { prefix },
        });
        return response.data;
    } catch (error) {
        console.error('Failed to find descriptions:', error.response?.data || error.message);
        throw new Error(error.response?.data?.message || 'Failed to find descriptions');
    }
};

export default api;
