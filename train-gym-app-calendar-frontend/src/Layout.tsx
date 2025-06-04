import React, { useEffect, useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import {
    AppBar, Toolbar, Button, Typography, Box, Container, CircularProgress,
    Snackbar, Alert
} from '@mui/material';
import axios from 'axios';

const POLL_INTERVAL = 60_000;

type User = {
    id: number;
    name: string;
    mail: string;
    admin: boolean;
    trainer: boolean;
};

const Layout: React.FC = () => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);
    const [sessionExpired, setSessionExpired] = useState(false);
    const [videoLoaded, setVideoLoaded] = useState(false);
    const navigate = useNavigate();

    const fetchUser = async () => {
        setLoading(true);
        try {
            const res = await axios.get('/api/user/me', { withCredentials: true });
            setUser(res.data);
            setSessionExpired(false);
        } catch (err: any) {
            setUser(null);
            setSessionExpired(true);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUser();
        const interval = setInterval(fetchUser, POLL_INTERVAL);
        return () => clearInterval(interval);
    }, []);

    const handleLogout = () => {
        axios.post('/api/auth/logout', {}, { withCredentials: true })
            .finally(() => {
                setSessionExpired(false);
                setUser(null);
                navigate('/login');
            });
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
                    {}
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <Typography
                            variant="h6"
                            sx={{ color: '#fff', cursor: 'pointer' }}
                            onClick={() => navigate('/main')}
                        >
                            Train Gym App
                        </Typography>
                    </Box>
                    {}
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

            {}
            {sessionExpired && (
                <Box
                    sx={{
                        position: 'fixed',
                        inset: 0,
                        zIndex: 1999,
                        backgroundColor: 'rgba(0,0,0,0.6)',
                        pointerEvents: 'all',
                        cursor: 'not-allowed',
                    }}
                />
            )}

            {}
            <Snackbar
                open={sessionExpired}
                anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
                sx={{
                    zIndex: 2000,
                    pointerEvents: 'auto',
                }}
            >
                <Alert
                    severity="warning"
                    sx={{ width: '100%', fontSize: 18 }}
                    action={
                        <Button color="inherit" size="small" onClick={handleLogout}>
                            Zaloguj się ponownie
                        </Button>
                    }
                >
                    Twoja sesja wygasła lub zostałeś wylogowany.<br />
                    {user ? 'Zostaniesz wylogowany.' : 'Zaloguj się ponownie, aby kontynuować.'}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Layout;
