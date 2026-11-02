import { createAsyncThunk } from '@reduxjs/toolkit';
import { bankRequest } from '../api/bankRequest.js';

export const getExchangeRate = createAsyncThunk(
    'accounts/rates',
    (_, thunk) => bankRequest(thunk, { url: '/accounts/rate' }),
    {
        condition: (_, { getState }) =>
        getState().accounts.rates.status !== 'pending',
    },
);
export const convertCurrency = createAsyncThunk(
    'accounts/convert',
    (details, thunk) => bankRequest(thunk, {
    method: 'post', url: '/accounts/convert', data: details,
}),
);
export const findRecipient = createAsyncThunk(
    'accounts/findRecipient',
    (details, thunk) => bankRequest(thunk, {
    method: 'post', url: '/accounts/find', data: details,
    }),
);
export const transferFunds = createAsyncThunk(
    'accounts/transfer',
    (details, thunk) => bankRequest(thunk, {
    method: 'post', url: '/accounts/transfer', data: details,
}),
);

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