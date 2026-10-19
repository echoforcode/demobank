import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { api, errorMessage } from '../api/api.js';
//Add authentication slice
const initialState = {
    user: null, token: null, status: 'idle', error: null,
    };
export const registerUser = createAsyncThunk(
    'user/register', async (details, thunk) => {
        try {
        return (await api.post('/user/register', details)).data;
        } catch (error) {
        return thunk.rejectWithValue(errorMessage(error));
        }
    },
    );

export const authenticateUser = createAsyncThunk(
    'user/authenticate', async (details, thunk) => {
    try {
    const { data } = await api.post('/user/auth', details);
    if (typeof data?.token !== 'string' || !data.token || !data?.user) {
        return thunk.rejectWithValue('Expected token and user in response.');
    }
    return data;
    } catch (error) {
    return thunk.rejectWithValue(errorMessage(error));
}
},
);

const slice = createSlice({
    name: 'user',
    initialState,
    reducers: {
    resetStatus(state) {
        state.status = 'idle';
        state.error = null;
    },
        logout() { return { ...initialState }; },
    },
    extraReducers(builder) {
    for (const task of [registerUser, authenticateUser]) {
        builder.addCase(task.pending, state => {
        state.status = 'pending';
        state.error = null;
    });
    builder.addCase(task.rejected, (state, action) => {
    state.status = 'failed';
    state.error = action.payload || 'Request failed.';
    });
    }
    builder.addCase(registerUser.fulfilled, state => {
    state.status = 'success';
    });
    builder.addCase(authenticateUser.fulfilled, (state, action) => {
    state.status = 'success';
    state.user = action.payload.user;
    state.token = action.payload.token;
});
},
});

export const { resetStatus, logout } = slice.actions;
export default slice.reducer;