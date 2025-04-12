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
    IconButton,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

import EditIcon from '@mui/icons-material/Edit';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import DeleteIcon from '@mui/icons-material/Delete';

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

    const [confirmDialogOpen, setConfirmDialogOpen] = useState(false);
    const [deleteConfirmId, setDeleteConfirmId] = useState<number | null>(null);

    const fetchWorkouts = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('Brak tokenu. Użytkownik nie jest zalogowany.');
            return;
        }
        axios
            .get('/api/training/my', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
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

    const handleEdit = (id: number) => {
        navigate(`/edit-workout/${id}`);
    };

    const handleComplete = (id: number) => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('Brak tokenu. Użytkownik nie jest zalogowany.');
            return;
        }
        axios
            .patch(`/api/training/complete/${id}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            })
            .then(() => {
                fetchWorkouts();
            })
            .catch((err) => {
                console.error('Error marking training as complete:', err);
            });
    };

    const handleDeleteClick = (id: number) => {
        setDeleteConfirmId(id);
        setConfirmDialogOpen(true);
    };

    const handleDialogClose = () => {
        setConfirmDialogOpen(false);
        setDeleteConfirmId(null);
    };

    const confirmDelete = () => {
        if (deleteConfirmId === null) return;
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('Brak tokenu. Użytkownik nie jest zalogowany.');
            return;
        }

        axios
            .delete(`/api/training/${deleteConfirmId}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            .then(() => {
                setConfirmDialogOpen(false);
                setDeleteConfirmId(null);
                fetchWorkouts();
            })
            .catch((err) => {
                console.error('Error deleting workout:', err);
            });
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

            {/* Dialog potwierdzenia usunięcia */}
            <Dialog
                open={confirmDialogOpen}
                onClose={handleDialogClose}
                PaperProps={{
                    sx: {
                        backgroundColor: 'rgba(60, 60, 60, 0.85)',
                        backdropFilter: 'blur(6px)',
                        color: '#fff',
                        borderRadius: 2,
                        minWidth: 300
                    }
                }}
            >
                <DialogTitle sx={{ fontWeight: 600, fontSize: 24 }}>Potwierdzenie</DialogTitle>
                <DialogContent>
                    <Typography sx={{ fontSize: 18, color: '#fff' }}>
                        Czy na pewno chcesz usunąć ten trening?
                    </Typography>
                </DialogContent>
                <DialogActions sx={{ p: 2 }}>
                    <Button
                        onClick={handleDialogClose}
                        sx={{
                            color: '#fff',
                            backgroundColor: 'rgba(255,255,255,0.1)',
                            mr: 1,
                            fontWeight: 600,
                            textTransform: 'none',
                            '&:hover': {
                                backgroundColor: 'rgba(255,255,255,0.2)'
                            }
                        }}
                    >
                        Anuluj
                    </Button>
                    <Button
                        onClick={confirmDelete}
                        sx={{
                            backgroundColor: '#C00',
                            color: '#fff',
                            fontWeight: 600,
                            textTransform: 'none',
                            '&:hover': {
                                backgroundColor: '#F00'
                            }
                        }}
                    >
                        Usuń
                    </Button>
                </DialogActions>
            </Dialog>

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
                    </Box>

                    {workouts.map((workout) => {
                        const isExpanded = expandedIds.includes(workout.id);
                        return (
                            <Card
                                key={workout.id}
                                sx={{
                                    mb: 2,
                                    backgroundColor: 'rgba(230,230,230,0.75)',
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

                                        {/* Prawa część – przyciski */}
                                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                            {/* Expand (v) */}
                                            <IconButton
                                                onClick={() => toggleExpanded(workout.id)}
                                                sx={{
                                                    color: '#333',
                                                    backgroundColor: 'rgba(255, 255, 255, 0.6)'
                                                }}
                                            >
                                                <ExpandMoreIcon
                                                    sx={{
                                                        transform: isExpanded ? 'rotate(180deg)' : 'rotate(0deg)',
                                                        transition: 'transform 0.3s'
                                                    }}
                                                />
                                            </IconButton>
                                            {/* Edycja */}
                                            <IconButton
                                                onClick={() => handleEdit(workout.id)}
                                                sx={{
                                                    color: '#333',
                                                    backgroundColor: 'rgba(255, 255, 255, 0.6)'
                                                }}
                                            >
                                                <EditIcon />
                                            </IconButton>
                                            {/* Zatwierdź (ukończony) */}
                                            <IconButton
                                                onClick={() => handleComplete(workout.id)}
                                                sx={{
                                                    color: '#2a2',
                                                    backgroundColor: 'rgba(255, 255, 255, 0.6)'
                                                }}
                                            >
                                                <CheckCircleIcon />
                                            </IconButton>
                                            {/* Usuń (X) */}
                                            <IconButton
                                                onClick={() => handleDeleteClick(workout.id)}
                                                sx={{
                                                    color: '#f55',
                                                    backgroundColor: 'rgba(255, 255, 255, 0.6)'
                                                }}
                                            >
                                                <DeleteIcon />
                                            </IconButton>
                                        </Box>
                                    </Box>

                                    {/* Rozwijane szczegóły (Collapse) */}
                                    <Collapse
                                        in={isExpanded}
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
                        );
                    })}
                </Box>
            </Container>
        </Box>
    );
};


export default YourWorkoutsPage;
