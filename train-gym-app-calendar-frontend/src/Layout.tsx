import React, { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Button, Typography, Box, Container, CircularProgress } from '@mui/material';
import { useAuth } from './useAuth';

const Layout: React.FC = () => {
    const [videoLoaded, setVideoLoaded] = useState(false);
    const navigate = useNavigate();
    const { user, loading } = useAuth(); // <--- bez logout

    // Prosta funkcja logout
    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    if (loading) {
        return (
            <Box sx={{ width: '100%', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
            {!videoLoaded && (
                <Box sx={{ position: 'absolute', inset: 0, backgroundColor: '#000', zIndex: -2 }} />
            )}
            <video
                autoPlay loop muted playsInline preload="auto"
                onCanPlayThrough={() => setVideoLoaded(true)}
                style={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    width: '100vw',
                    height: '100vh',
                    objectFit: 'cover',
                    zIndex: -1,
                    opacity: videoLoaded ? 1 : 0,
                    transition: 'opacity 0.8s ease-in-out'
                }}
            >
                <source src="/videos/background.mp4" type="video/mp4" />
                Twoja przeglądarka nie obsługuje formatu video.
            </video>


            <AppBar position="static" sx={{ backgroundColor: '#000' }} elevation={0}>
                <Toolbar sx={{ justifyContent: 'space-between' }}>
                    {/* LEWA STRONA */}
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <Typography
                            variant="h6"
                            sx={{ color: '#fff', cursor: 'pointer' }}
                            onClick={() => navigate('/main')}
                        >
                            Train Gym App
                        </Typography>
                    </Box>
                    {/* PRAWA STRONA */}
                    <Box sx={{ display: 'flex', gap: 3 }}>
                        {user?.trainer && (
                            <Button sx={{ color: '#fff' }} onClick={() => navigate('/trainer-panel')}>Trainer Panel</Button>
                        )}
                        <Button sx={{ color: '#fff' }} onClick={() => navigate('/add-workout')}>Add Workout</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => navigate('/calendar')}>Calendar</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => navigate('/your-workouts')}>Your Workouts</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => navigate('/1rm-calculator')}>1RM Calculator</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => navigate('/profile')}>Profile</Button>
                        {user?.admin && (
                            <Button sx={{ color: '#fff' }} onClick={() => navigate('/admin')}>Admin</Button>
                        )}
                        <Button sx={{ color: '#fff' }} onClick={handleLogout}>LOG OUT</Button>
                    </Box>
                </Toolbar>
            </AppBar>

            <Container sx={{ mt: '124px', position: 'relative', zIndex: 1 }}>
                <Outlet />
            </Container>
        </Box>
    );
};

export default Layout;
