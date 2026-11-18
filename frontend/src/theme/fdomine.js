import { createTheme } from '@mui/material/styles';
import '@fontsource/domine/400.css';
import '@fontsource/domine/500.css';
import '@fontsource/domine/600.css';
import '@fontsource/domine/700.css';

const fdomine = createTheme({
  palette: {
    primary: { main: '#12518A' },
  },

  typography: {
    fontFamily: '"Domine", Arial, sans-serif',
  },

  shape: {
    borderRadius: 12,
  },
});

export default fdomine;