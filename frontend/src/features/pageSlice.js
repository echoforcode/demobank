import { createSlice } from '@reduxjs/toolkit';
import { logout } from './usersSlice.js';

const initialState = { showSpinner: false, showNav: false };
const slice = createSlice({
    name: 'pages',
    initialState,
    reducers: {
        openSpinner(state) { state.showSpinner = true; },
        closeSpinner(state) { state.showSpinner = false; },
        toggleNav(state) { state.showNav = !state.showNav; },
        closeNav(state) { state.showNav = false; },
},
    extraReducers(builder) {
        builder.addCase(logout, () => ({ ...initialState }));
},
});
export const { openSpinner, closeSpinner } = slice.actions;
export const selectSpinner = state => state.pages.showSpinner;
export default slice.reducer;