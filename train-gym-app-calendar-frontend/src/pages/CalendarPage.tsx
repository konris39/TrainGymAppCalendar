import React, { useState, useEffect } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    Paper
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs';

const CalendarPage: React.FC = () => {
    const navigate = useNavigate();
    const [workouts, setWorkouts] = useState<any[]>([]);
    const [currentMonth, setCurrentMonth] = useState(dayjs());

    const fetchWorkouts = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('Brak tokenu. Użytkownik nie jest zalogowany.');
            return;
        }
        axios
            .get('/api/training/my', {
                headers: { Authorization: `Bearer ${token}` }
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

    const firstDayOfMonth = currentMonth.startOf('month');
    const lastDayOfMonth = currentMonth.endOf('month');
    const startDayIndex = firstDayOfMonth.day();
    const totalCells = Math.ceil((startDayIndex + lastDayOfMonth.date()) / 7) * 7;
    const calendarDays: dayjs.Dayjs[] = [];
    for (let i = 0; i < totalCells; i++) {
        const day = firstDayOfMonth.subtract(startDayIndex, 'day').add(i, 'day');
        calendarDays.push(day);
    }

    const workoutsByDate: { [date: string]: any[] } = {};
    workouts.forEach((workout) => {
        const d = dayjs(workout.trainingDate).format('YYYY-MM-DD');
        if (!workoutsByDate[d]) workoutsByDate[d] = [];
        workoutsByDate[d].push(workout);
    });

    const handleWorkoutClick = (workoutId: number) => {
        navigate(`/training-detail/${workoutId}`);
    };

    const handlePrevMonth = () => setCurrentMonth(currentMonth.subtract(1, 'month'));
    const handleNextMonth = () => setCurrentMonth(currentMonth.add(1, 'month'));

    const handleNavClick = (path: string) => navigate(path);

    const isToday = (day: dayjs.Dayjs) => day.isSame(dayjs(), 'day');

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
                    <Typography variant="h6" sx={{ color: '#fff', cursor: 'pointer' }} onClick={() => handleNavClick('/main')}>
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

            {/* Główna sekcja kalendarza */}
            <Container sx={{ mt: 2, position: 'relative', zIndex: 1 }}>
                <Box
                    sx={{
                        backgroundColor: 'rgba(50, 50, 50, 0.45)',
                        backdropFilter: 'blur(6px)',
                        WebkitBackdropFilter: 'blur(6px)',
                        borderRadius: 2,
                        p: 3,
                        minHeight: '65vh'
                    }}
                >
                    {/* Nawigacja miesięczna */}
                    <Box sx={{ textAlign: 'center', mb: 3 }}>
                        <Button
                            onClick={handlePrevMonth}
                            variant="contained"
                            sx={{
                                mr: 2,
                                backgroundColor: '#333',
                                color: '#fff',
                                boxShadow: 'none',
                                borderRadius: '8px',
                                textTransform: 'none',
                                fontWeight: 'bold',
                                '&:hover': { backgroundColor: '#555' }
                            }}
                        >
                            {'<<'}
                        </Button>
                        <Typography variant="h5" component="span" sx={{ color: '#fff', mx: 2 }}>
                            {currentMonth.format('MMMM YYYY')}
                        </Typography>
                        <Button
                            onClick={handleNextMonth}
                            variant="contained"
                            sx={{
                                ml: 2,
                                backgroundColor: '#333',
                                color: '#fff',
                                boxShadow: 'none',
                                borderRadius: '8px',
                                textTransform: 'none',
                                fontWeight: 'bold',
                                '&:hover': { backgroundColor: '#555' }
                            }}
                        >
                            {'>>'}
                        </Button>
                    </Box>

                    {/* Grid kalendarza */}
                    <Box sx={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 1 }}>
                        {calendarDays.map((day, idx) => {
                            const isCurrentMonth = day.month() === currentMonth.month();
                            const dayString = day.format('YYYY-MM-DD');
                            const dayWorkouts = workoutsByDate[dayString] || [];
                            const border = isToday(day) ? '2px solid #000' : '1px solid #555';
                            const backgroundColor = isToday(day) ? 'rgba(255,255,255,0.99)' : (isCurrentMonth ? '#cccccc' : '#777');
                            return (
                                <Paper
                                    key={idx}
                                    sx={{
                                        height: 95,
                                        p: 1,
                                        backgroundColor,
                                        border,
                                        color: '#000',
                                        overflow: 'hidden'
                                    }}
                                >
                                    <Typography variant="caption" sx={{ fontWeight: 'bold' }}>
                                        {day.date()}
                                    </Typography>
                                    {dayWorkouts.map((workout) => (
                                        <Box
                                            key={workout.id}
                                            sx={{
                                                mt: 0.5,
                                                px: 0.5,
                                                backgroundColor: '#000',
                                                color: '#fff',
                                                borderRadius: 1,
                                                cursor: 'pointer'
                                            }}
                                            onClick={() => handleWorkoutClick(workout.id)}
                                        >
                                            <Typography variant="caption">{workout.name}</Typography>
                                        </Box>
                                    ))}
                                </Paper>
                            );
                        })}
                    </Box>
                </Box>
            </Container>
        </Box>
    );
};

export default CalendarPage;
