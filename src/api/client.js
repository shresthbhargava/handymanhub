import axios from 'axios';

let currentToken = null;

export const setAuthToken = (token) => {
  currentToken = token;
};

export const clearAuthToken = () => {
  currentToken = null;
};

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'https://handymanhub.onrender.com',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use(
  (config) => {
    if (currentToken) {
      config.headers.Authorization = `Bearer ${currentToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default apiClient;