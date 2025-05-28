import React, { useState } from 'react';
import {
    Container,
    Paper,
    Typography,
    Box,
    TextField,
    Button,
    Checkbox,
    FormControlLabel
} from '@mui/material';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/pl';
import dayjs, { Dayjs } from 'dayjs';
import api from '../api/axios';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';

const AddEventPage: React.FC = () => {
    const [eventName, setEventName] = useState('');
    const [eventDate, setEventDate] = useState<Dayjs | null>(null);
    const [description, setDescription] = useState('');
    const [askTrainer, setAskTrainer] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const textFieldStyle = {
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
            '& fieldset': { borderColor: 'rgba(100,100,100,0.6)' },
            '&:hover fieldset': { borderColor: 'rgba(80,80,80,0.8)' },
            '&.Mui-focused fieldset': { borderColor: 'black' }
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

    const textFieldFocusOverride = {
        '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: '#000 !important'
        },
        '& .MuiFormLabel-root.Mui-focused': {
            color: '#000 !important'
        }
    };

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setErrorMessage('');
        if (!eventName || !eventDate) {
            setErrorMessage('Wypełnij nazwę i datę.');
            return;
        }
        try {
            await api.post('/api/training/add', {
                name: eventName,
                trainingDate: eventDate.format('YYYY-MM-DD'),
                description,
                askTrainer
                });
            window.location.href = '/your-workouts';
        } catch {
            setErrorMessage('Nie udało się dodać wydarzenia.');
        }
    };

    return (
        <Container sx={{ mt: '84px', mb: 4, position: 'relative', zIndex: 1 }}>
            <Paper
                sx={{
                    backgroundColor: 'rgba(240,240,240,0.9)',
                    borderRadius: 2,
                    p: 5,
                    maxWidth: 650,
                    mx: 'auto',
                    textAlign: 'center',
                    boxShadow: 3
                }}
            >
                <Typography variant="h4" sx={{ mb: 3 }}>
                    DODAJ WYDARZENIE
                </Typography>

                <Box component="form" onSubmit={handleSubmit} sx={{ width: '100%' }}>
                    <TextField
                        label="Nazwa wydarzenia"
                        variant="outlined"
                        fullWidth
                        margin="normal"
                        value={eventName}
                        onChange={e => setEventName(e.target.value)}
                        InputProps={{ sx: textFieldStyle, notched: false }}
                        InputLabelProps={{ sx: inputLabelStyle }}
                        sx={{ ...inputOutlineStyle, ...floatLabelStyle, ...textFieldFocusOverride }}
                    />

                    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pl">
                        <DatePicker
                            label="Data"
                            value={eventDate}
                            onChange={newValue => setEventDate(newValue)}
                            slotProps={{
                                textField: {
                                    fullWidth: true,
                                    margin: 'normal',
                                    placeholder: 'DD/MM/YYYY',
                                    InputProps: { notched: false, sx: textFieldStyle },
                                    InputLabelProps: { sx: inputLabelStyle, shrink: true },
                                    sx: { ...inputOutlineStyle, ...floatLabelStyle, ...textFieldFocusOverride }
                                },
                                popper: { sx: popperStyle }
                            }}
                            slots={{ openPickerIcon: CalendarTodayIcon }}
                        />
                    </LocalizationProvider>

                    <TextField
                        label="Opis"
                        variant="outlined"
                        fullWidth
                        margin="normal"
                        multiline
                        rows={4}
                        value={description}
                        onChange={e => setDescription(e.target.value)}
                        InputProps={{ sx: textFieldStyle, notched: false }}
                        InputLabelProps={{ sx: inputLabelStyle }}
                        sx={{ ...inputOutlineStyle, ...floatLabelStyle, ...textFieldFocusOverride }}
                    />

                    {/* CHECKBOX dla askTrainer */}
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={askTrainer}
                                onChange={e => setAskTrainer(e.target.checked)}
                                color="primary"
                            />
                        }
                        label="Wyślij do akceptacji trenera"
                        sx={{ mt: 1, mb: 1, userSelect: 'none' }}
                    />

                    {errorMessage && (
                        <Typography variant="body2" color="error" sx={{ mb: 2 }}>
                            {errorMessage}
                        </Typography>
                    )}

                    <Button
                        type="submit"
                        variant="contained"
                        fullWidth
                        sx={{
                            mt: 3,
                            backgroundColor: '#000',
                            color: '#fff',
                            '&:hover': { backgroundColor: '#333' },
                            py: 1.2
                        }}
                    >
                        DODAJ
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default AddEventPage;
