import React, { useState, useEffect } from 'react';
import {
    Box,
    Container,
    TextField,
    Typography,
    Button
} from '@mui/material';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import axios from 'axios';
import dayjs, { Dayjs } from 'dayjs';

import { LocalizationProvider } from '@mui/x-date-pickers';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/pl';

const EditWorkoutPage: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { id } = useParams<{ id: string }>();
    const from = (location.state as any)?.from || `/training-detail/${id}`;

    const [eventName, setEventName] = useState('');
    const [eventDate, setEventDate] = useState<Dayjs | null>(null);
    const [description, setDescription] = useState('');
    const [errorMessage, setErrorMessage] = useState('');


    useEffect(() => {
        if (!id) return;
        const token = localStorage.getItem('token');
        if (!token) {
            setErrorMessage('Brak tokenu. Zaloguj się.');
            return;
        }
        axios
            .get(`/api/training/${id}`, { headers: { Authorization: `Bearer ${token}` } })
            .then(res => {
                setEventName(res.data.name);
                setEventDate(dayjs(res.data.trainingDate));
                setDescription(res.data.description);
            })
            .catch(() => setErrorMessage('Nie udało się pobrać danych treningu.'));
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const token = localStorage.getItem('token');
        if (!token) {
            setErrorMessage('Brak tokenu. Zaloguj się.');
            return;
        }
        try {
            await axios.patch(
                `/api/training/update/${id}`,
                {
                    name: eventName,
                    trainingDate: eventDate?.format('YYYY-MM-DD'),
                    description
                },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            navigate(from);
        } catch {
            setErrorMessage('Nie udało się zaktualizować treningu.');
        }
    };

    const handleCancel = () => {
        navigate(from);
    };

    const textFieldStyle = {
        backdropFilter: 'blur(6px)',
        WebkitBackdropFilter: 'blur(6px)',
        '& .MuiOutlinedInput-input': { color: 'black !important' }
    };
    const inputLabelStyle = {
        fontSize: '1.1rem',
        color: 'rgba(80,80,80,1)',
        '&.Mui-focused': { color: 'black !important' }
    };
    const inputOutlineStyle = {
        '& .MuiOutlinedInput-root': {
            '& fieldset': { borderColor: 'rgba(100,100,100,0.6)' },
            '&:hover fieldset': { borderColor: 'rgba(80,80,80,0.8)' },
            '&.Mui-focused fieldset': { borderColor: '#000' }
        }
    };
    const textFieldFocusOverride = {
        '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: '#000 !important'
        },
        '& .MuiFormLabel-root.Mui-focused': {
            color: '#000 !important'
        }
    };
    const floatLabelStyle = {
        '& .MuiInputLabel-root': { transformOrigin: 'left top', transition: 'all 0.15s ease' },
        '& label.MuiInputLabel-shrink': { transform: 'translate(0, -1.2rem) scale(0.85)' }
    };
    const popperStyle = {
        '& .MuiPickersCalendarHeader-switchHeader': { backgroundColor: '#222', color: '#fff' },
        '& .MuiPickersDay-dayOutsideMonth': { opacity: 0.3 }
    };

    return (
        <Container sx={{ mt: '84px', mb: 4, position: 'relative', zIndex: 1 }}>
            <Box
                component="form"
                onSubmit={handleSubmit}
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    px: 4,
                    py: 4,
                    textAlign: 'center',
                    backgroundColor: 'rgba(240,240,240,0.9)',
                    borderRadius: 2,
                    mx: 'auto',
                    maxWidth: 500
                }}
            >
                <Typography
                    sx={{
                        color: '#000',
                        mb: 3,
                        fontWeight: 600,
                        fontFamily: 'Yu Gothic Light',
                        fontSize: 64
                    }}
                >
                    EDIT EVENT
                </Typography>

                <TextField
                    label="Event Name"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    value={eventName}
                    onChange={e => setEventName(e.target.value)}
                    InputProps={{ sx: textFieldStyle }}
                    InputLabelProps={{ sx: inputLabelStyle }}
                    sx={{ ...inputOutlineStyle, ...floatLabelStyle, ...textFieldFocusOverride, mb: 3 }}
                />

                <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pl">
                    <DatePicker
                        label="Date"
                        value={eventDate}
                        onChange={newValue => setEventDate(newValue)}
                        slotProps={{
                            textField: {
                                fullWidth: true,
                                margin: 'normal',
                                placeholder: 'dd.mm.yyyy',
                                InputProps: { notched: false, sx: textFieldStyle },
                                InputLabelProps: { sx: inputLabelStyle, shrink: true },
                                sx: { ...inputOutlineStyle, ...floatLabelStyle, ...textFieldFocusOverride, mb: 3 }
                            },
                            popper: { sx: popperStyle }
                        }}
                    />
                </LocalizationProvider>

                <TextField
                    label="Description"
                    variant="outlined"
                    fullWidth
                    margin="normal"
                    multiline
                    rows={4}
                    value={description}
                    onChange={e => setDescription(e.target.value)}
                    InputProps={{ sx: textFieldStyle, notched: false }}
                    InputLabelProps={{ sx: inputLabelStyle }}
                    sx={{ ...inputOutlineStyle, ...floatLabelStyle, ...textFieldFocusOverride, mb: 3 }}
                />

                {errorMessage && (
                    <Typography variant="body2" color="error" sx={{ mb: 2 }}>
                        {errorMessage}
                    </Typography>
                )}

                <Box sx={{ width: '100%', display: 'flex', gap: 2, mt: 2 }}>
                    <Button
                        onClick={handleCancel}
                        variant="contained"
                        fullWidth
                        sx={{
                            backgroundColor: '#000',
                            color: '#fff',
                            '&:hover': { backgroundColor: '#333' }
                        }}
                    >
                        CANCEL
                    </Button>
                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        sx={{
                            backgroundColor: '#000',
                            color: '#fff',
                            '&:hover': { backgroundColor: '#333' }
                        }}
                    >
                        CONFIRM
                    </Button>
                </Box>
            </Box>
        </Container>
    );
};

export default EditWorkoutPage;
