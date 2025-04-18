import React, { useState } from 'react';
import { Container, Paper, Typography, Box, TextField, Button } from '@mui/material';

const OneRMCalculatorPage: React.FC = () => {
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

    const textFieldFocusOverride = {
        '& .MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline': {
            borderColor: '#000 !important',
        },
        '& .MuiFormLabel-root.Mui-focused': {
            color: '#000 !important',
        },
    };

    return (
        <Container sx={{ mt: 4, position: 'relative', zIndex: 1 }}>
            <Paper
                sx={{
                    backgroundColor: 'rgba(240,240,240,0.9)',
                    borderRadius: 2,
                    p: 5,
                    maxWidth: 650,
                    mx: 'auto',
                    textAlign: 'center',
                    boxShadow: 3,
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
    );
};

export default OneRMCalculatorPage;