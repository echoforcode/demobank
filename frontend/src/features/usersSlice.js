import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { api, errorMessage } from '../api/api.js';

const initialState = {
    user: null, token: null, status: 'idle', error: null,
};
export const registerUser = createAsyncThunk(
    'user/register',
    async (details, thunk) => {
        try {
            const { data } = await api.post('/user/register', details);
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
    },
    extraReducers(builder) {
        builder
        .addCase(registerUser.pending, state => {
        state.status = 'pending';
        state.error = null;
        })
        .addCase(registerUser.fulfilled, state => {
        state.status = 'success';
    })
        .addCase(registerUser.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload || 'Registration failed.';
    });
},
});

export const { resetStatus } = slice.actions;
export default slice.reducer;