import { createSlice } from '@reduxjs/toolkit';
const slice = createSlice({
    name: 'pages',
    initialState: { showSpinner: false },
    reducers: {
        openSpinner(state) { state.showSpinner = true; },
        closeSpinner(state) { state.showSpinner = false; },
},
});
export const { openSpinner, closeSpinner } = slice.actions;
export const selectSpinner = state => state.pages.showSpinner;
export default slice.reducer;