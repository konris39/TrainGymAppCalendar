import React, { useState } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    TextField,
    Paper
} from '@mui/material';
import { useNavigate } from 'react-router-dom';

const OneRMCalculatorPage: React.FC = () => {
    const navigate = useNavigate();

    const [weight, setWeight] = useState<number | ''>('');
    const [reps, setReps] = useState<number | ''>('');
    const [result, setResult] = useState<number | null>(null);
    const [error, setError] = useState<string>('');

    const handleCalculate = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (weight === '' || reps === '' || weight <= 0 || reps <= 0) {
            setError('Please provide valid numbers greater than zero.');
            setResult(null);
            return;
        }
        setError('');
        const oneRM = Number(weight) * (1 + Number(reps) / 30);
        setResult(oneRM);
    };

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    const textFieldFocusOverride = {
        '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: '#000 !important', // czarny outline
        },
        '& .MuiFormLabel-root.Mui-focused': {
            color: '#000 !important',       // czarna etykieta
        }
    };

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
                {/* Fallback text */}
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

            {/* Główna sekcja kalkulatora */}
            <Container sx={{ mt: 4, position: 'relative', zIndex: 1 }}>
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
                        CALCULATE YOUR 1RM
                    </Typography>

                    <Box component="form" onSubmit={handleCalculate}>
                        <TextField
                            label="Weight (kg)"
                            variant="outlined"
                            fullWidth
                            type="number"
                            value={weight}
                            onChange={(e) => setWeight(Number(e.target.value))}
                            sx={{ mb: 2, ...textFieldFocusOverride }}
                        />
                        <TextField
                            label="Reps"
                            variant="outlined"
                            fullWidth
                            type="number"
                            value={reps}
                            onChange={(e) => setReps(Number(e.target.value))}
                            sx={{ mb: 2, ...textFieldFocusOverride }}
                        />

                        {error && (
                            <Typography variant="body2" color="error" sx={{ mb: 2 }}>
                                {error}
                            </Typography>
                        )}

                        <Button
                            type="submit"
                            variant="contained"
                            fullWidth
                            sx={{ backgroundColor: '#000', color: '#fff', py: 1.2, mb: 2 }}
                        >
                            CALCULATE
                        </Button>
                    </Box>

                    {result !== null && (
                        <Typography variant="h5" sx={{ mt: 3 }}>
                            Your estimated one rep max is: {result.toFixed(2)} kg
                        </Typography>
                    )}
                </Paper>
            </Container>
        </Box>
    );
};

export default OneRMCalculatorPage;
