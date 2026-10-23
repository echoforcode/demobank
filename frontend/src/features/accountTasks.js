import { createAsyncThunk } from '@reduxjs/toolkit';
import { bankRequest } from '../api/bankRequest.js';

export const fetchAccounts = createAsyncThunk(
    'accounts/fetch',
    (_, thunk) => bankRequest(thunk, { url: '/accounts' }),
    {
        condition: (_, { getState }) =>
        getState().accounts.load.status !== 'pending',
    },
);

export const createAccount = createAsyncThunk(
    'accounts/create',
    (code, thunk) => bankRequest(thunk, {
        method: 'post',
        url: '/accounts',
        data: { code },
    }),
);