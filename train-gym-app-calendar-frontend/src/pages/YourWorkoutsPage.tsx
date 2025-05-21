import React, { useState, useEffect } from 'react';
import {
    Container,
    Box,
    Card,
    CardContent,
    Collapse,
    IconButton,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    useTheme
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs';

interface Workout {
    id: number;
    name: string;
    description: string;
    trainingDate: string;
    completed: boolean;
    accepted: boolean; // <-- dodane pole
}

const YourWorkoutsPage: React.FC = () => {
    const navigate = useNavigate();
    const theme = useTheme();
    const [workouts, setWorkouts] = useState<Workout[]>([]);
    const [expandedIds, setExpandedIds] = useState<number[]>([]);
    const [confirmOpen, setConfirmOpen] = useState(false);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) return;
        axios
            .get<Workout[]>('/api/training/my', { headers: { Authorization: `Bearer ${token}` } })
            .then(res => setWorkouts(res.data))
            .catch(console.error);
    }, []);

    // completed pierwsze sortowanie, potem oczekujące na akceptację
    const sortedWorkouts = [...workouts].sort((a, b) => {
        if (a.completed !== b.completed) return a.completed ? 1 : -1;
        if (a.accepted !== b.accepted) return a.accepted ? -1 : 1;
        return 0;
    });

    const toggleExpand = (id: number) => {
        setExpandedIds(prev =>
            prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
        );
    };

    const markComplete = (id: number) => {
        const token = localStorage.getItem('token');
        if (!token) return;
        axios
            .patch(`/api/training/complete/${id}`, {}, { headers: { Authorization: `Bearer ${token}` } })
            .then(() => window.location.reload())
            .catch(console.error);
    };

    const requestDelete = (id: number) => {
        setDeleteId(id);
        setConfirmOpen(true);
    };
    const cancelDelete = () => {
        setConfirmOpen(false);
        setDeleteId(null);
    };
    const confirmDelete = () => {
        if (!deleteId) return;
        const token = localStorage.getItem('token');
        if (!token) return;
        axios
            .delete(`/api/training/${deleteId}`, { headers: { Authorization: `Bearer ${token}` } })
            .then(() => window.location.reload())
            .catch(console.error);
    };

    const editWorkout = (id: number) => navigate(`/edit-workout/${id}`);

    return (
        <Container sx={{ mt: '124px', position: 'relative', zIndex: 1 }}>
            <Box
                sx={{
                    backgroundColor: 'rgba(240,240,240,0.9)',
                    borderRadius: 2,
                    p: 4,
                    boxShadow: 3,
                    maxWidth: 800,
                    mx: 'auto'
                }}
            >
                <Typography
                    variant="h4"
                    sx={{ mb: 2, fontWeight: 700, textAlign: 'center' }}
                >
                    YOUR WORKOUTS
                </Typography>

                {sortedWorkouts.map(w => {
                    const expanded = expandedIds.includes(w.id);
                    const awaitingAcceptance = !w.completed && !w.accepted;
                    return (
                        <Card
                            key={w.id}
                            sx={{
                                mb: 2,
                                boxShadow: 2,
                                position: 'relative',
                                bgcolor: w.completed
                                    ? '#f0f0f0'
                                    : awaitingAcceptance
                                    ? '#f2f2f2'
                                    : '#fff',
                                opacity: w.completed
                                    ? 0.6
                                    : awaitingAcceptance
                                    ? 0.4
                                    : 1,
                                pointerEvents: w.completed || awaitingAcceptance ? 'none' : 'auto' // blokuje przyciski jeśli completed lub oczekuje
                            }}
                        >
                            <CardContent sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <Box>
                                    <Typography variant="h6" sx={{ fontWeight: 600 }}>
                                        {w.name}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        {dayjs(w.trainingDate).format('DD MMM YYYY')}
                                    </Typography>
                                </Box>
                                <Box sx={{ display: 'flex', gap: 1 }}>
                                    <IconButton onClick={() => toggleExpand(w.id)} disabled={w.completed || awaitingAcceptance}>
                                        <ExpandMoreIcon
                                            sx={{ transform: expanded ? 'rotate(180deg)' : 'rotate(0)', transition: '0.3s' }}
                                        />
                                    </IconButton>
                                    <IconButton onClick={() => editWorkout(w.id)} disabled={w.completed || awaitingAcceptance}>
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton onClick={() => markComplete(w.id)} sx={{ color: theme.palette.success.main }} disabled={w.completed || awaitingAcceptance}>
                                        <CheckCircleIcon />
                                    </IconButton>
                                    <IconButton onClick={() => requestDelete(w.id)} sx={{ color: theme.palette.error.main }} disabled={w.completed || awaitingAcceptance}>
                                        <DeleteIcon />
                                    </IconButton>
                                </Box>
                            </CardContent>
                            <Collapse in={expanded} timeout="auto" unmountOnExit>
                                <Box sx={{ p: 2, bgcolor: '#f9f9f9' }}>
                                    <Typography variant="body1">
                                        {w.description || 'No description provided.'}
                                    </Typography>
                                </Box>
                            </Collapse>

                            {awaitingAcceptance && (
                                <Box
                                    sx={{
                                        position: 'absolute',
                                        top: 0,
                                        left: 0,
                                        width: '100%',
                                        height: '100%',
                                        display: 'flex',
                                        justifyContent: 'center',
                                        alignItems: 'center',
                                        bgcolor: 'rgba(255,255,255,0.70)',
                                        zIndex: 2,
                                        pointerEvents: 'auto'
                                    }}
                                >
                                    <Typography
                                        variant="h5"
                                        sx={{
                                            color: '#888',
                                            fontWeight: 700,
                                            textTransform: 'uppercase',
                                            textAlign: 'center',
                                            letterSpacing: 1,
                                            userSelect: 'none'
                                        }}
                                    >
                                        Oczekiwanie na potwierdzenie<br />
                                        <span style={{ fontSize: 18, fontWeight: 400 }}>
                                            Skontaktuj się z trenerem
                                        </span>
                                    </Typography>
                                </Box>
                            )}

                            {w.completed && (
                                <Box
                                    sx={{
                                        position: 'absolute',
                                        top: 0,
                                        left: 0,
                                        width: '100%',
                                        height: '100%',
                                        display: 'flex',
                                        justifyContent: 'center',
                                        alignItems: 'center',
                                        bgcolor: 'rgba(255,255,255,0.6)',
                                        zIndex: 2,
                                        pointerEvents: 'auto'
                                    }}
                                >
                                    <Typography variant="h4" color="text.secondary" sx={{ textTransform: 'uppercase' }}>
                                        Completed
                                    </Typography>
                                </Box>
                            )}
                        </Card>
                    );
                })}

                {/* Delete Confirmation */}
                <Dialog open={confirmOpen} onClose={cancelDelete}>
                    <DialogTitle>Confirm Delete</DialogTitle>
                    <DialogContent>
                        <Typography>Are you sure you want to delete this workout?</Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={cancelDelete}>Cancel</Button>
                        <Button onClick={confirmDelete} color="error">
                            Delete
                        </Button>
                    </DialogActions>
                </Dialog>
            </Box>
        </Container>
    );
};

export default YourWorkoutsPage;
