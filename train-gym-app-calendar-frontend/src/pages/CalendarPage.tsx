import React, { useState, useEffect } from 'react';
import { Container, Box, Paper, Typography, IconButton, useTheme } from '@mui/material';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import dayjs from 'dayjs';

const CalendarPage: React.FC = () => {
    const navigate = useNavigate();
    const theme = useTheme();
    const [workouts, setWorkouts] = useState<any[]>([]);
    const [currentMonth, setCurrentMonth] = useState(dayjs());

    useEffect(() => {
        api.get<any[]>('/api/training/my')
            .then(res => setWorkouts(res.data))
            .catch(err => console.error('Error fetching workouts:', err));
    }, []);

    const firstDay = currentMonth.startOf('month');
    const lastDay = currentMonth.endOf('month');
    const offset = firstDay.day();
    const cells = Math.ceil((offset + lastDay.date()) / 7) * 7;
    const days: dayjs.Dayjs[] = [];
    for (let i = 0; i < cells; i++) {
        days.push(firstDay.subtract(offset, 'day').add(i, 'day'));
    }

    const byDate: Record<string, any[]> = {};
    workouts.forEach(w => {
        const key = dayjs(w.trainingDate).format('YYYY-MM-DD');
        if (!byDate[key]) byDate[key] = [];
        byDate[key].push(w);
    });

    const isToday = (d: dayjs.Dayjs) => d.isSame(dayjs(), 'day');

    return (
        <Container sx={{ mt: -5, mb: 4, position: 'relative', zIndex: 1 }}>
            <Paper
                sx={{
                    backgroundColor: 'rgba(240,240,240,0.9)',
                    borderRadius: 2,
                    p: 3,
                    boxShadow: 3
                }}
            >
                {/* Month navigation with subtle buttons */}
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', mb: 2, gap: 1 }}>
                    <IconButton
                        onClick={() => setCurrentMonth(m => m.subtract(1, 'month'))}
                        sx={{ bgcolor: 'rgba(0,0,0,0.3)', color: '#fff', '&:hover': { bgcolor: 'rgba(0,0,0,0.5)' }, p: 1 }}
                    >
                        <ChevronLeftIcon />
                    </IconButton>
                    <Typography variant="h4" sx={{ mx: 1, fontWeight: 'bold' }}>
                        {currentMonth.format('MMMM YYYY')}
                    </Typography>
                    <IconButton
                        onClick={() => setCurrentMonth(m => m.add(1, 'month'))}
                        sx={{ bgcolor: 'rgba(0,0,0,0.3)', color: '#fff', '&:hover': { bgcolor: 'rgba(0,0,0,0.5)' }, p: 1 }}
                    >
                        <ChevronRightIcon />
                    </IconButton>
                </Box>

                {/* Calendar grid */}
                <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 1 }}>
                    {days.map((day, idx) => {
                        const key = day.format('YYYY-MM-DD');
                        const entries = byDate[key] || [];
                        const inMonth = day.month() === currentMonth.month();
                        return (
                            <Paper
                                key={idx}
                                sx={{
                                    p: 2,
                                    minHeight: 100,
                                    backgroundColor: isToday(day)
                                        ? 'rgba(255,255,255,1)'
                                        : inMonth
                                            ? '#fff'
                                            : '#ddd',
                                    border: isToday(day)
                                        ? `2px solid ${theme.palette.text.primary}`
                                        : '1px solid rgba(150,150,150,1)',
                                    overflow: 'hidden'
                                }}
                            >
                                <Typography variant="body2" sx={{ fontWeight: 'bold', fontSize: '0.9rem' }}>
                                    {day.date()}
                                </Typography>
                                {entries.map(w => (
                                    <Box
                                        key={w.id}
                                        onClick={() => navigate(`/training-detail/${w.id}`)}
                                        sx={{
                                            mt: 0.5,
                                            px: 0.5,
                                            backgroundColor: '#000',
                                            color: '#fff',
                                            borderRadius: 1,
                                            cursor: 'pointer',
                                            fontSize: '0.75rem'
                                        }}
                                    >
                                        {w.name}
                                    </Box>
                                ))}
                            </Paper>
                        );
                    })}
                </Box>
            </Paper>
        </Container>
    );
};

export default CalendarPage;
