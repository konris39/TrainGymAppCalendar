import React, { useState } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    TextField
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/pl';

import CalendarTodayIcon from '@mui/icons-material/CalendarToday';

const AddEventPage: React.FC = () => {
    const navigate = useNavigate();

    const [eventName, setEventName] = useState('');
    const [eventDate, setEventDate] = useState<any>(null);
    const [description, setDescription] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        try {
            const finalDate = eventDate ? eventDate.format('YYYY-MM-DD') : '';
            const response = await axios.post('/api/training/addPublic', {
                name: eventName,
                trainingDate: finalDate,
                description
            });
            console.log('Add Event response:', response.data);
            navigate('/your-workouts');
        } catch (error) {
            console.error('Add Event error:', error);
            setErrorMessage('Nie udało się dodać wydarzenia.');
        }
    };

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    const textFieldStyle = {
        backgroundColor: 'rgba(250, 250, 250, 0.85)',
        backdropFilter: 'blur(6px)',
        WebkitBackdropFilter: 'blur(6px)',
        '& .MuiOutlinedInput-input': {
            color: 'black !important'
        }
    };

    const inputLabelStyle = {
        fontSize: '1.1rem',
        color: 'rgba(80,80,80,1)',
        '&.Mui-focused': {
            color: 'black'
        }
    };

    const inputOutlineStyle = {
        '& .MuiOutlinedInput-root': {
            '& fieldset': {
                borderColor: 'rgba(200,200,200,0.4)'
            },
            '&:hover fieldset': {
                borderColor: 'rgba(150,150,150,0.7)'
            },
            '&.Mui-focused fieldset': {
                borderColor: 'black'
            }
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
        '& .MuiPickersDay-dayOutsideMonth': {
            opacity: 0.3
        }
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

            {/* Kontener z formularzem */}
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
                        ADD EVENT
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
                                onChange={(e) => setEventName(e.target.value)}
                                InputProps={{
                                    notched: false,
                                    sx: {
                                        ...textFieldStyle,
                                    }
                                }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={{ ...inputOutlineStyle, ...floatLabelStyle, mb: 3 }}
                            />

                            {/* POLE DATA (DatePicker) */}
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
                                            sx: {
                                                ...textFieldStyle,
                                                '& .MuiOutlinedInput-input': {
                                                    color: 'black !important'
                                                }
                                            }
                                        },
                                        InputLabelProps: {
                                            sx: inputLabelStyle,
                                            shrink: true
                                        }
                                    },
                                    popper: {
                                        sx: popperStyle
                                    }
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
                                onChange={(e) => setDescription(e.target.value)}
                                InputProps={{
                                    sx: textFieldStyle,
                                    notched: false
                                }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={{ ...inputOutlineStyle, ...floatLabelStyle, mb: 3 }}
                            />

                            {errorMessage && (
                                <Typography variant="body2" color="error" mt={1}>
                                    {errorMessage}
                                </Typography>
                            )}

                            <Button
                                type="submit"
                                variant="contained"
                                fullWidth
                                sx={{
                                    backgroundColor: '#000',
                                    color: '#fff',
                                    '&:hover': { backgroundColor: '#333' },
                                    fontSize: '1rem',
                                    py: 1.2
                                }}
                            >
                                SUBMIT
                            </Button>
                        </Box>
                    </LocalizationProvider>
                </Box>
            </Container>
        </Box>
    );
};

export default AddEventPage;
