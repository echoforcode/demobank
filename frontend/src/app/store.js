import { configureStore } from '@reduxjs/toolkit';
import user from '../features/usersSlice.js';
import pages from '../features/pageSlice.js';
import accounts from '../features/accountsSlice.js';
import cards from '../features/cardSlice.js';

//add accountSlice to the store
export const store = configureStore({
    reducer: { user, pages, accounts, cards },
});