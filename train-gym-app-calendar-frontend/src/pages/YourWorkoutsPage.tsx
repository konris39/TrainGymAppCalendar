import React, { useEffect, useState } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    Card,
    CardContent,
    Collapse,
    IconButton
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

interface Workout {
    id: number;
    name: string;
    description: string;
    trainingDate: string;
    completed?: boolean;
}

const YourWorkoutsPage: React.FC = () => {
    const navigate = useNavigate();
    const [workouts, setWorkouts] = useState<Workout[]>([]);
    const [expandedIds, setExpandedIds] = useState<number[]>([]);

    const fetchWorkouts = () => {
        axios
            .get(`/api/training/all`)
            .then((res) => {
                setWorkouts(res.data);
            })
            .catch((err) => {
                console.error('Error fetching workouts:', err);
            });
    };

    useEffect(() => {
        fetchWorkouts();
    }, []);

    const toggleExpanded = (id: number) => {
        setExpandedIds((prev) =>
            prev.includes(id)
                ? prev.filter((expandedId) => expandedId !== id)
                : [...prev, id]
        );
    };

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    return (
        <Box sx={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
            {/* Wideo tło */}
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
                Twoja przeglądarka nie obsługuje wideo.
            </video>

            {/* Navbar */}
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

            {/* Główna zawartość */}
            <Container
                sx={{
                    mt: '84px',
                    position: 'relative',
                    zIndex: 1,
                    animation: 'fadeIn 1s',
                    '@keyframes fadeIn': {
                        from: { opacity: 0 },
                        to: { opacity: 1 }
                    }
                }}
            >
                <Box
                    sx={{
                        backgroundColor: 'rgba(50, 50, 50, 0.45)',
                        backdropFilter: 'blur(6px)',
                        WebkitBackdropFilter: 'blur(6px)',
                        borderRadius: 2,
                        px: 4,
                        py: 4,
                        maxWidth: 700,
                        mx: 'auto',
                        textAlign: 'center'
                    }}
                >
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', mb: 2 }}>
                        <Typography
                            sx={{
                                color: '#fff',
                                fontWeight: 600,
                                fontFamily: 'Yu Gothic Light',
                                fontSize: { xs: 48, md: 72 },
                                mr: 2
                            }}
                        >
                            YOUR WORKOUTS
                        </Typography>
                        {/* Przycisk Refresh */}
                        <IconButton onClick={fetchWorkouts} sx={{ color: '#fff' }}>
                            <RefreshIcon fontSize="large" />
                        </IconButton>
                    </Box>

                    {workouts.map((workout) => (
                        <Card
                            key={workout.id}
                            sx={{
                                mb: 2,
                                backgroundColor: 'rgba(255,255,255,0.75)',
                                borderRadius: 2,
                                boxShadow: 3,
                                transition: 'all 0.3s ease'
                            }}
                        >
                            <CardContent sx={{ textAlign: 'left' }}>
                                <Box
                                    sx={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        alignItems: 'center'
                                    }}
                                >
                                    {/* Lewa część – nazwa i data */}
                                    <Box>
                                        <Typography variant="h6" sx={{ color: '#000', fontWeight: 700 }}>
                                            {workout.name}
                                        </Typography>
                                        <Typography variant="body2" sx={{ color: '#333' }}>
                                            {workout.trainingDate}
                                        </Typography>
                                    </Box>

                                    {/* Prawa część – przycisk Szczegóły */}
                                    <Box>
                                        <Button
                                            variant="contained"
                                            size="small"
                                            onClick={() => toggleExpanded(workout.id)}
                                            sx={{
                                                transition: 'background-color 0.2s ease',
                                                textTransform: 'none',
                                                fontWeight: 600,
                                                '&:hover': {
                                                    backgroundColor: '#444'
                                                }
                                            }}
                                        >
                                            Szczegóły
                                        </Button>
                                    </Box>
                                </Box>

                                {/* Rozwijane szczegóły (z animacją Collapsa) */}
                                <Collapse
                                    in={expandedIds.includes(workout.id)}
                                    timeout="auto"
                                    unmountOnExit
                                    sx={{ mt: 1 }}
                                >
                                    <Box sx={{ pl: 1 }}>
                                        <Typography variant="body1" sx={{ color: '#000' }}>
                                            <strong>Name: </strong> {workout.name}
                                        </Typography>
                                        <Typography variant="body1" sx={{ color: '#000' }}>
                                            <strong>Date: </strong> {workout.trainingDate}
                                        </Typography>
                                        <Typography variant="body1" sx={{ color: '#000' }}>
                                            <strong>Description: </strong> {workout.description}
                                        </Typography>
                                    </Box>
                                </Collapse>
                            </CardContent>
                        </Card>
                    ))}
                </Box>
            </Container>
        </Box>
    );
};

export default YourWorkoutsPage;
