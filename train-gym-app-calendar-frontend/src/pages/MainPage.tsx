import React from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Container,
    Typography,
    Box,
    useTheme
} from '@mui/material';
import { useNavigate } from 'react-router-dom';

const MainPage: React.FC = () => {
    const navigate = useNavigate();
    const theme = useTheme();

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    return (
        <Box sx={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
            {/* Video tło */}
            <video
                autoPlay
                loop
                muted
                playsInline
                style={{
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                    zIndex: -1
                }}
            >
                <source src="/videos/background.mp4" type="video/mp4" />
                Twoja przeglądarka nie obsługuje formatu video.
            </video>

            {/* Nawigacja (AppBar) */}
            <AppBar position="static" sx={{ backgroundColor: '#000' }} elevation={0}>
                <Toolbar sx={{ justifyContent: 'space-between' }}>
                    <Typography variant="h6" sx={{ color: '#fff' }}>
                        Train Gym App
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 3 }}>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/add-workout')}>
                            Add Workout
                        </Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/calendar')}>
                            Calendar
                        </Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/your-workouts')}>
                            Your Workouts
                        </Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/1rm-calculator')}>
                            1RM Calculator
                        </Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/profile')}>
                            Profile
                        </Button>
                    </Box>
                </Toolbar>
            </AppBar>

            {/* Główna sekcja (hero) */}
            <Container
                sx={{
                    // Zamiast mt: 8 (64px), dajemy dokładnie 84px, czyli o 20px więcej
                    mt: '124px',
                    position: 'relative',
                    zIndex: 1
                }}
            >
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
                        onClick={() => handleNavClick('/add-workout')}
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
            </Container>
        </Box>
    );
};

export default MainPage;
