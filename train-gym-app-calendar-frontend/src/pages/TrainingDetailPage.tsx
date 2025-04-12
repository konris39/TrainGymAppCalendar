import React, { useState, useEffect } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    Paper,
    IconButton
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs';

import EditIcon from '@mui/icons-material/Edit';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import DeleteIcon from '@mui/icons-material/Delete';

const TrainingDetailPage: React.FC = () => {
    const navigate = useNavigate();
    const { id } = useParams<{ id: string }>();
    const [training, setTraining] = useState<any>(null);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            setErrorMessage('Brak tokenu. Proszę się zalogować.');
            return;
        }
        axios
            .get(`/api/training/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            .then((res) => {
                setTraining(res.data);
            })
            .catch((err) => {
                console.error('Error fetching training details:', err);
                setErrorMessage('Nie udało się pobrać danych treningu.');
            });
    }, [id]);

    const handleEdit = () => {
        navigate(`/edit-workout-cal/${id}`);
    };

    const handleComplete = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) return;
            await axios.patch(`/api/training/complete/${id}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            window.location.reload();
        } catch (error) {
            console.error('Error completing training:', error);
        }
    };

    const handleDelete = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) return;
            await axios.delete(`/api/training/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            navigate('/calendar');
        } catch (error) {
            console.error('Error deleting training:', error);
        }
    };

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    return (
        <Box sx={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
            {/* Tło – wideo */}
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
                Twoja przeglądarka nie obsługuje formatu wideo.
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

            {/* Centralny kontener z ciemną ramką (blur) */}
            <Container sx={{ mt: 4, position: 'relative', zIndex: 1 }}>
                <Box sx={{ display: 'flex', justifyContent: 'center' }}>
                    <Box
                        sx={{
                            backgroundColor: 'rgba(20,20,20,0.6)',
                            backdropFilter: 'blur(12px)',
                            borderRadius: 2,
                            p: 2,
                            maxWidth: 650,
                            width: '100%'
                        }}
                    >
                        {errorMessage && (
                            <Typography variant="h6" color="error" align="center" sx={{ mb: 2 }}>
                                {errorMessage}
                            </Typography>
                        )}
                        {training && (
                            <Paper
                                sx={{
                                    p: 4,
                                    borderRadius: 2,
                                    backgroundColor: 'rgba(255,255,255,0.9)',
                                    boxShadow: 3,
                                    position: 'relative'
                                }}
                            >
                                {/* Ikonki w prawym górnym rogu */}
                                <Box
                                    sx={{
                                        position: 'absolute',
                                        top: 16,
                                        right: 16,
                                        display: 'flex',
                                        gap: 1
                                    }}
                                >
                                    <IconButton onClick={handleEdit} sx={{ color: '#555' }}>
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton onClick={handleComplete} sx={{ color: '#2a2' }}>
                                        <CheckCircleIcon />
                                    </IconButton>
                                    <IconButton onClick={handleDelete} sx={{ color: '#f55' }}>
                                        <DeleteIcon />
                                    </IconButton>
                                </Box>

                                {/* Sekcja: Name */}
                                <Typography variant="h4" sx={{ mb: 3, fontWeight: 600 }}>
                                    {training.name}
                                </Typography>

                                {/* Sekcja: Date */}
                                <Typography variant="h6" sx={{ mb: 3, color: '#333' }}>
                                    {dayjs(training.trainingDate).format('DD MMMM YYYY')}
                                </Typography>

                                {/* Sekcja: Description */}
                                <Typography variant="body1" sx={{ mb: 3, lineHeight: 1.6 }}>
                                    {training.description}
                                </Typography>

                                {/* Przycisk Back – czarny */}
                                <Box sx={{ textAlign: 'center', mt: 4 }}>
                                    <Button variant="contained" sx={{ backgroundColor: '#000', color: '#fff' }} onClick={() => navigate('/calendar')}>
                                        BACK
                                    </Button>
                                </Box>
                            </Paper>
                        )}
                    </Box>
                </Box>
            </Container>
        </Box>
    );
};

export default TrainingDetailPage;
