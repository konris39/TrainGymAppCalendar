// src/pages/MainPage.js
import React from 'react';
import { Container, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

function MainPage() {
    const navigate = useNavigate();

    const handleLogout = () => {
        // TODO: Dodaj logikę wylogowania (np. czyszczenie tokenów, jeśli zajdzie taka potrzeba)
        navigate('/login');
    };

    return (
        <Container maxWidth="sm" sx={{ textAlign: 'center', mt: 4 }}>
            <Typography variant="h4" gutterBottom>
                Zalogowano!
            </Typography>
            <Button variant="contained" onClick={handleLogout}>
                Wyloguj
            </Button>
        </Container>
    );
}

export default MainPage;
