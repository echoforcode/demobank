import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { bankRequest } from '../api/bankRequest.js';
import { logout } from './usersSlice.js';

export function historyKey({ scope = 'all', id = '' }) {
    return `${scope}:${id}`;
}
export const emptyHistory = {
    items: [], page: 0, totalPages: 0, totalItems: 0,
    status: 'idle', error: null, requestId: null,
};
export const fetchTransactions = createAsyncThunk('transactions/fetch',
    ({ scope = 'all', id, page = 0 }, thunk) => {
        let url = '/transactions';
        if (scope === 'account') url += `/a/${encodeURIComponent(id)}`;
        if (scope === 'card') url += `/c/${encodeURIComponent(id)}`;
        return bankRequest(thunk, { url, params: { page } });
});

const slice = createSlice({
    name: 'transactions',
    initialState: { views: {} },
    reducers: {},
    extraReducers(builder) {
        builder.addCase(fetchTransactions.pending, (state, action) => {
        const key = historyKey(action.meta.arg);
        state.views[key] = {
            ...emptyHistory, page: action.meta.arg.page || 0,
            status: 'pending', requestId: action.meta.requestId,
    };
    });
        builder.addCase(fetchTransactions.fulfilled, (state, action) => {
        const view = state.views[historyKey(action.meta.arg)];
        if (view?.requestId !== action.meta.requestId) return;
        Object.assign(view, action.payload, { status: 'success', error: null });
        });
        builder.addCase(fetchTransactions.rejected, (state, action) => {
        const view = state.views[historyKey(action.meta.arg)];
        if (view?.requestId !== action.meta.requestId) return;
        view.status = action.meta.aborted ? 'idle' : 'failed';
        view.error = action.meta.aborted ? null
            : action.payload || 'Could not load history.';
        });
        builder.addCase(logout, () => ({ views: {} }));
},
});
export default slice.reducer;