import { createSlice } from '@reduxjs/toolkit';
import { logout } from './usersSlice.js';
import { fetchAccounts, createAccount,findRecipient,transferFunds } from './accountTasks.js';

const requestState = () => ({
    status: 'idle', error: null, requestId: null,
});
const initialState = () => ({
    items: [],
    load: requestState(),
    create: requestState(),
    recipient: { ...requestState(), value: null },
    transfer: { ...requestState(), receipt: null },
    rates: { ...requestState(), snapshot: null },
    conversion: { ...requestState(), receipt: null },
});

const slice = createSlice({
    name: 'accounts',
    initialState: initialState(),
    reducers: {
resetRecipient(state) {
    state.recipient = { ...requestState(), value: null };
},
},
extraReducers(builder) {
    const tasks = [
        [fetchAccounts, 'load'], [createAccount, 'create'],[findRecipient, 'recipient'], [transferFunds, 'transfer']
    ];
    for (const [task, key] of tasks) {
        builder.addCase(task.pending, (state, action) => {
        state[key].status = 'pending';
        state[key].error = null;
        state[key].requestId = action.meta.requestId;
});
builder.addCase(task.rejected, (state, action) => {
    if (state[key].requestId !== action.meta.requestId) return;
        state[key].status = action.meta.aborted ? 'idle' : 'failed';
        state[key].error = action.meta.aborted ? null
        : action.payload || 'Request failed.';
});
}
builder.addCase(fetchAccounts.fulfilled, (state, action) => {
if (state.load.requestId !== action.meta.requestId) return;
    state.load.status = 'success';
    state.items = action.payload;
});
builder.addCase(createAccount.fulfilled, (state, action) => {
if (state.create.requestId !== action.meta.requestId) return;
    state.create.status = 'success';
    state.items = state.items.filter(item =>
    item.accountId !== action.payload.accountId);
    state.items.push(action.payload);
});
builder.addCase(findRecipient.fulfilled, (state, action) => {
    if (state.recipient.requestId !== action.meta.requestId) return;
    state.recipient.status = 'success';
    state.recipient.value = action.payload;
});
builder.addCase(transferFunds.fulfilled, (state, action) => {
    if (state.transfer.requestId !== action.meta.requestId) return;
    state.transfer.status = 'success';
    state.transfer.receipt = action.payload;
});
builder.addCase(logout, () => initialState());
},
});
export const { resetRecipient }= slice.actions;
export default slice.reducer;