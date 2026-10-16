import axios from 'axios';
export const api = axios.create({
    baseURL: '/api/v1',
    timeout: 5000,
    headers: { 'Content-Type': 'application/json' },
});
export function errorMessage(error) {
    if (!error.response) {
        return 'Cannot reach the server. Check Spring Boot and retry.';
    }
    const message = error.response.data?.message;
    return typeof message === 'string'
        ? message
        : `Request failed (${error.response.status}).`;
}