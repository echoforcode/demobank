import { configureStore } from '@reduxjs/toolkit';
import user from '../features/usersSlice.js';
import pages from '../features/pageSlice.js';

export const store = configureStore({
    reducer: { user, pages },
});