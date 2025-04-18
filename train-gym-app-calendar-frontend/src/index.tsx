import React from 'react';
import ReactDOM from 'react-dom/client';
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import App from './App';
import reportWebVitals from './reportWebVitals';

const container = document.getElementById('root');
if (!container) {
    throw new Error("Brak elementu 'root' w index.html");
}
const root = ReactDOM.createRoot(container);

const theme = createTheme({
    palette: {
        background: {
            default: '#000'
        }
    }
});

root.render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <App />
        </ThemeProvider>
    </React.StrictMode>
);

reportWebVitals();