import { api, errorMessage } from './api.js';
import { logout } from '../features/usersSlice.js';
export async function bankRequest(thunk, config,notFoundIsNull = false) {
    const token = thunk.getState().user.token;
    if (!token) return thunk.rejectWithValue('Please log in again.');
    try {
        const { data } = await api.request({
        ...config,
        signal: thunk.signal,
        headers: { Authorization: `Bearer ${token}` },
    });
    return data;
    } catch (error) {
        if (notFoundIsNull && error.response?.status === 404) return null;
        if (error.response?.status === 401
            && thunk.getState().user.token === token) {
            thunk.dispatch(logout());
    }
    return thunk.rejectWithValue(errorMessage(error));
}
}