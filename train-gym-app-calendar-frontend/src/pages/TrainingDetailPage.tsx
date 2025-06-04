import React, { useState, useEffect } from 'react';
import { Box, Container, Paper, Typography, IconButton, Button } from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../api/axios';
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
        api
            .get(`/api/training/${id}`)
            .then((res) => setTraining(res.data))
            .catch(() => setErrorMessage('Nie udało się pobrać danych treningu.'));
    }, [id]);

    const handleEdit = () => {
        navigate(`/edit-workout-cal/${id}`);
    };

    const handleComplete = async () => {
        try {
            const token = localStorage.getItem('token') ?? '';
            if (!token) return;
            await api.patch(`/api/training/complete/${id}`);
            window.location.reload();
        } catch {
            setErrorMessage('Nie udało się oznaczyć treningu jako ukończonego.');
        }
    };

    const handleDelete = async () => {
        try {
            const token = localStorage.getItem('token') ?? '';
            if (!token) return;
            await api.delete(`/api/training/${id}`);
            navigate('/calendar');
        } catch {
            setErrorMessage('Nie udało się usunąć treningu.');
        }
    };

    return (
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
                            <Box sx={{ position: 'absolute', top: 16, right: 16, display: 'flex', gap: 1 }}>
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

                            <Typography variant="h4" sx={{ mb: 3, fontWeight: 600 }}>
                                {training.name}
                            </Typography>

                            <Typography variant="h6" sx={{ mb: 3, color: '#333' }}>
                                {dayjs(training.trainingDate).format('DD MMMM YYYY')}
                            </Typography>

                            <Typography variant="body1" sx={{ mb: 3, lineHeight: 1.6 }}>
                                {training.description}
                            </Typography>

                            <Box sx={{ textAlign: 'center', mt: 4 }}>
                                <Button
                                    variant="contained"
                                    sx={{
                                        backgroundColor: '#000',
                                        color: '#fff',
                                        '&:hover': { backgroundColor: '#333' }
                                    }}
                                    onClick={() => navigate('/calendar')}
                                >
                                    BACK
                                </Button>
                            </Box>
                        </Paper>
                    )}
                </Box>
            </Box>
        </Container>
    );
};

export default TrainingDetailPage;
