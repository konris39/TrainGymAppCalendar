import React, { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Button, Typography, Box, Container } from '@mui/material';

const Layout: React.FC = () => {
    const navigate = useNavigate();
    const [videoLoaded, setVideoLoaded] = useState(false);

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    return (
        <Box sx={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
            {/* Placeholder until video loads (global) */}
            {!videoLoaded && (
                <Box
                    sx={{
                        position: 'absolute',
                        inset: 0,
                        backgroundColor: '#000',
                        zIndex: -2
                    }}
                />
            )}

            {/* Background video (global) */}
            <video
                autoPlay
                loop
                muted
                playsInline
                preload="auto"
                onCanPlayThrough={() => setVideoLoaded(true)}
                style={{
                    position: 'absolute',
                    inset: 0,
                    objectFit: 'cover',
                    zIndex: -1,
                    opacity: videoLoaded ? 1 : 0,
                    width: '100%',
                    height: 'auto',
                    minHeight: '100vh',
                    transition: 'opacity 0.8s ease-in-out'
                }}
            >
                <source src="/videos/background.mp4" type="video/mp4" />
                Twoja przeglądarka nie obsługuje formatu video.
            </video>

            {/* Shared AppBar */}
            <AppBar position="static" sx={{ backgroundColor: '#000' }} elevation={0}>
                <Toolbar sx={{ justifyContent: 'space-between' }}>
                    <Typography
                        variant="h6"
                        sx={{ color: '#fff', cursor: 'pointer' }}
                        onClick={() => handleNavClick('/main')}
                    >
                        Train Gym App
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 3 }}>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/add-workout')}>Add Workout</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/calendar')}>Calendar</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/your-workouts')}>Your Workouts</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/1rm-calculator')}>1RM Calculator</Button>
                        <Button sx={{ color: '#fff' }} onClick={() => handleNavClick('/profile')}>Profile</Button>
                    </Box>
                </Toolbar>
            </AppBar>

            {/* Dynamic page content injected here */}
            <Container sx={{ mt: '124px', position: 'relative', zIndex: 1 }}>
                <Outlet />
            </Container>
        </Box>
    );
};

export default Layout;