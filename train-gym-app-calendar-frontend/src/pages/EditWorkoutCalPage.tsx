import React, { useState, useEffect } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    TextField
} from '@mui/material';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import axios from 'axios';
import dayjs from 'dayjs';

import { LocalizationProvider } from '@mui/x-date-pickers';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/pl';

const EditWorkoutCalPage: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { id } = useParams<{ id: string }>();
    const from = (location.state as any)?.from || `/training-detail/${id}`;

    const [eventName, setEventName] = useState('');
    const [eventDate, setEventDate] = useState<any>(null);
    const [description, setDescription] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    // Funkcja pobierająca szczegóły treningu
    const fetchWorkoutDetails = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            setErrorMessage('Brak tokenu. Zaloguj się.');
            return;
        }
        axios
            .get(`/api/training/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            })
            .then((res) => {
                const data = res.data;
                setEventName(data.name);
                setEventDate(dayjs(data.trainingDate));
                setDescription(data.description);
            })
            .catch((err) => {
                console.error('Error fetching workout details:', err);
                setErrorMessage('Nie udało się pobrać danych treningu.');
            });
    };

    useEffect(() => {
        if (id) {
            fetchWorkoutDetails();
        }
    }, [id]);

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const finalDate = eventDate ? eventDate.format('YYYY-MM-DD') : '';
            const token = localStorage.getItem('token');
            if (!token) {
                setErrorMessage('Brak tokenu. Zaloguj się.');
                return;
            }
            const response = await axios.patch(
                `/api/training/update/${id}`,
                {
                    name: eventName,
                    trainingDate: finalDate,
                    description
                },
                {
                    headers: { Authorization: `Bearer ${token}` }
                }
            );
            console.log('Update response:', response.data);
            navigate(from);
        } catch (error) {
            console.error('Update error:', error);
            setErrorMessage('Nie udało się zaktualizować treningu.');
        }
    };

    const handleCancel = () => {
        navigate(from);
    };

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    const textFieldStyle = {
        backgroundColor: 'rgba(250, 250, 250, 0.85)',
        backdropFilter: 'blur(6px)',
        WebkitBackdropFilter: 'blur(6px)',
        '& .MuiOutlinedInput-input': { color: 'black !important' }
    };

    const inputLabelStyle = {
        fontSize: '1.1rem',
        color: 'rgba(80,80,80,1)',
        '&.Mui-focused': { color: 'black' }
    };

    const inputOutlineStyle = {
        '& .MuiOutlinedInput-root': {
            '& fieldset': { borderColor: 'rgba(200,200,200,0.4)' },
            '&:hover fieldset': { borderColor: 'rgba(150,150,150,0.7)' },
            '&.Mui-focused fieldset': { borderColor: 'black' }
        }
    };

    const floatLabelStyle = {
        '& .MuiInputLabel-root': {
            transformOrigin: 'left top',
            transition: 'all 0.15s ease'
        },
        '& label.MuiInputLabel-shrink': {
            transform: 'translate(0, -1.2rem) scale(0.85)'
        }
    };

    const popperStyle = {
        '& .MuiPickersCalendarHeader-switchHeader': {
            backgroundColor: '#222',
            color: '#fff'
        },
        '& .MuiPickersDay-dayOutsideMonth': { opacity: 0.3 }
    };

    return (
        <Box sx={{ position: 'relative', minHeight: '100vh', overflow: 'hidden' }}>
            {/* Tło wideo */}
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

            {/* Główna sekcja – formularz edycji */}
            <Container sx={{ mt: '84px', position: 'relative', zIndex: 1 }}>
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        justifyContent: 'center',
                        minHeight: '60vh',
                        px: 4,
                        py: 4,
                        textAlign: 'center',
                        backgroundColor: 'rgba(50, 50, 50, 0.45)',
                        backdropFilter: 'blur(6px)',
                        WebkitBackdropFilter: 'blur(6px)',
                        borderRadius: 2,
                        mx: 'auto',
                        maxWidth: 500
                    }}
                >
                    <Typography
                        sx={{
                            color: '#fff',
                            mb: 3,
                            fontWeight: 600,
                            fontFamily: 'Yu Gothic Light',
                            fontSize: 84
                        }}
                    >
                        EDIT EVENT
                    </Typography>

                    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pl">
                        <Box component="form" onSubmit={handleSubmit} sx={{ width: '100%' }}>
                            {/* EVENT NAME */}
                            <TextField
                                label="Event Name"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                value={eventName}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEventName(e.target.value)}
                                InputProps={{ notched: false, sx: { ...textFieldStyle } }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={{ ...inputOutlineStyle, ...floatLabelStyle, mb: 3 }}
                            />

                            {/* DATA (DatePicker) */}
                            <DatePicker
                                label="Data"
                                value={eventDate}
                                onChange={(newValue) => setEventDate(newValue)}
                                slotProps={{
                                    textField: {
                                        fullWidth: true,
                                        margin: 'normal',
                                        placeholder: 'dd.mm.yyyy',
                                        sx: { ...inputOutlineStyle, ...floatLabelStyle, mb: 3 },
                                        InputProps: {
                                            notched: false,
                                            sx: { ...textFieldStyle, '& .MuiOutlinedInput-input': { color: 'black !important' } }
                                        },
                                        InputLabelProps: { sx: inputLabelStyle, shrink: true }
                                    },
                                    popper: { sx: popperStyle }
                                }}
                            />

                            {/* DESCRIPTION */}
                            <TextField
                                label="Description"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                multiline
                                rows={4}
                                value={description}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setDescription(e.target.value)}
                                InputProps={{ sx: textFieldStyle, notched: false }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={{ ...inputOutlineStyle, ...floatLabelStyle, mb: 3 }}
                            />

                            {errorMessage && (
                                <Typography variant="body2" color="error" mt={1}>
                                    {errorMessage}
                                </Typography>
                            )}

                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
                                <Button
                                    variant="contained"
                                    sx={{ backgroundColor: '#000', color: '#fff', width: '48%' }}
                                    onClick={handleCancel}
                                >
                                    CANCEL
                                </Button>
                                <Button
                                    type="submit"
                                    variant="contained"
                                    sx={{ backgroundColor: '#000', color: '#fff', width: '48%' }}
                                >
                                    CONFIRM
                                </Button>
                            </Box>
                        </Box>
                    </LocalizationProvider>
                </Box>
            </Container>
        </Box>
    );
};

export default EditWorkoutCalPage;
