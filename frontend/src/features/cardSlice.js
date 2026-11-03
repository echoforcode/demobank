import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { bankRequest } from '../api/bankRequest.js';
import { logout } from './usersSlice.js';

export const fetchCard = createAsyncThunk('cards/fetch',
    (_, thunk) => bankRequest(thunk, { url: '/card' }, true));
export const createCard = createAsyncThunk('cards/create',
    (amount, thunk) => bankRequest(thunk, {
        method: 'post', url: '/card/create', data: { amount },
    }));
export const creditCard = createAsyncThunk('cards/credit',
    (amount, thunk) => bankRequest(thunk, {
        method: 'post', url: '/card/credit', data: { amount },
    }));
export const debitCard = createAsyncThunk('cards/debit',
    (amount, thunk) => bankRequest(thunk, {
        method: 'post', url: '/card/debit', data: { amount },
}));
const initialState = () => ({
    card: null, status: 'idle', error: null, requestId: null,
    receipt: null, revision: 0,
});
const slice = createSlice({
    name: 'cards',
    initialState: initialState(),
    reducers: {},
    extraReducers(builder) {
    for (const task of [fetchCard, createCard, creditCard, debitCard]) {
        builder.addCase(task.pending, (state, action) => {
        state.status = 'pending';
        state.error = null;
        state.requestId = action.meta.requestId;
    });
        builder.addCase(task.rejected, (state, action) => {
            if (state.requestId !== action.meta.requestId) return;
            state.status = action.meta.aborted ? 'idle' : 'failed';
            state.error = action.meta.aborted ? null
            : action.payload || 'Card request failed.';
    });
    }
    builder.addCase(fetchCard.fulfilled, (state, action) => {
        if (state.requestId !== action.meta.requestId) return;
        state.status = 'success';
        state.card = action.payload;
    });
    for (const task of [createCard, creditCard, debitCard]) {
        builder.addCase(task.fulfilled, (state, action) => {
        if (state.requestId !== action.meta.requestId) return;
        state.status = 'success';
        state.card = action.payload.card;
        state.receipt = action.payload;
        state.revision += 1;
});
}
    builder.addCase(logout, () => initialState());
},
});
export const selectCard = state => state.cards.card;
export default slice.reducer;