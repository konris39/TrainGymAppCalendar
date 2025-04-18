import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

const MainPage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                minHeight: '50vh',
                px: 3,
                py: 3,
                textAlign: 'center',
                backgroundColor: 'rgba(0, 0, 0, 0.45)',
                backdropFilter: 'blur(6px)',
                WebkitBackdropFilter: 'blur(6px)',
                borderRadius: 2
            }}
        >
            <Typography
                sx={{
                    color: '#fff',
                    mb: 3,
                    fontWeight: 100,
                    fontFamily: 'Yu Gothic Light',
                    fontSize: 76
                }}
            >
                GET STRONGER WITH US!
            </Typography>
            <Typography sx={{ color: '#ddd', mb: 4, fontSize: 28 }}>
                Your journey to a healthier, stronger life begins now.
            </Typography>
            <Button
                variant="contained"
                onClick={() => navigate('/add-workout')}
                sx={{
                    backgroundColor: '#000',
                    color: '#fff',
                    '&:hover': { backgroundColor: '#333' },
                    px: 6,
                    py: 2.5,
                    fontSize: '1.3rem',
                    mt: 2
                }}
            >
                ADD WORKOUT
            </Button>
        </Box>
    );
};

export default MainPage;