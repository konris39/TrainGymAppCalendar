import React, { useEffect, useState } from 'react';
import { Container, Typography, Paper, Box, IconButton, CircularProgress } from '@mui/material';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import CancelOutlinedIcon from '@mui/icons-material/CancelOutlined';
import api from '../api/axios';

interface Training {
    id: number;
    name: string;
    description: string;
    trainingDate: string;
    completed: boolean;
}

const TrainerPanelPage: React.FC = () => {
    const [trainings, setTrainings] = useState<Training[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchTrainings = async () => {
            try {
                const token = localStorage.getItem('token') ?? '';
                const res = await api.get('/api/training/to-accept');
                    setTrainings(res.data);
                } catch {
                    setTrainings([]);
                } finally {
                    setLoading(false);
                }
            };
        fetchTrainings();
    }, []);

    const acceptTraining = async (id: number) => {
        try {
            const token = localStorage.getItem('token');
            await api.patch(`/api/training/accept/${id}`);
            setTrainings(t => t.filter(tr => tr.id !== id));
        } catch (err) {
            alert('Nie udało się zaakceptować treningu.');
        }
    };

    const declineTraining = async (id: number) => {
        try {
            const token = localStorage.getItem('token') ?? '';
            await api.patch(`/api/training/decline/${id}`, {});
            setTrainings(t => t.filter(tr => tr.id !== id));
        } catch (err) {
            alert('Nie udało się odrzucić treningu.');
        }
    };

    if (loading) {
        return (
            <Box sx={{ width: '100%', minHeight: '40vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container sx={{ mt: 4 }}>
            <Paper sx={{ p: 4, borderRadius: 2 }}>
                <Typography variant="h4" gutterBottom>Trainer Panel – Trainings to Accept</Typography>
                {trainings.length === 0
                    ? <Typography>No trainings to accept!</Typography>
                    : trainings.map(tr => (
                        <Box
                            key={tr.id}
                            sx={{
                                borderBottom: '1px solid #eee',
                                py: 2,
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center'
                            }}
                        >
                            <Box>
                                <Typography><b>Name:</b> {tr.name}</Typography>
                                <Typography><b>Date:</b> {tr.trainingDate}</Typography>
                                <Typography><b>Description:</b> {tr.description}</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                <IconButton
                                    color="success"
                                    onClick={() => acceptTraining(tr.id)}
                                    aria-label="Zaakceptuj"
                                >
                                    <CheckCircleOutlineIcon sx={{ fontSize: 32 }} />
                                </IconButton>
                                <IconButton
                                    color="error"
                                    onClick={() => declineTraining(tr.id)}
                                    aria-label="Odrzuć"
                                >
                                    <CancelOutlinedIcon sx={{ fontSize: 32 }} />
                                </IconButton>
                            </Box>
                        </Box>
                    ))
                }
            </Paper>
        </Container>
    );
};

export default TrainerPanelPage;
