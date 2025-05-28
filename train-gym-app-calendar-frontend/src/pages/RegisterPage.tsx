import React, { useState } from 'react';
import { Box, Paper, Typography, TextField, Button, Grid } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const RegisterPage: React.FC = () => {
    const navigate = useNavigate();
    const [name, setName] = useState<string>('');
    const [mail, setMail] = useState<string>('');
    const [password, setPassword] = useState<string>('');
    const [errorMessage, setErrorMessage] = useState<string>('');

    const handleRegister = async (e: React.FormEvent<HTMLFormElement>): Promise<void> => {
        e.preventDefault();
        try {
            const response = await api.post('/api/auth/register', { name, mail, password });
            console.log('Register response:', response.data);
            navigate('/login');
        } catch (error: any) {
            console.error('Register error:', error);
            setErrorMessage('Rejestracja nie powiodła się. Sprawdź dane.');
        }
    };

    const textFieldStyle = {
        backgroundColor: 'rgba(235,235,235,0.65)',
        backdropFilter: 'blur(6px)',
        WebkitBackdropFilter: 'blur(6px)'
    };

    const inputLabelStyle = {
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
                borderColor: '#222222'
            }
        }
    };

    return (
        <Box
            sx={{
                width: '100vw',
                height: '100vh',
                backgroundImage: 'url("/images/gym_pic.jpg")',
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
            }}
        >
            <Grid container>
                <Grid size={{xs: 11, sm:8, md: 5, lg: 4}}>
                    <Paper
                        elevation={6}
                        sx={{
                            width: 500,
                            p: 4,
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            borderRadius: 4,
                            backgroundColor: 'rgba(255, 255, 255, 0.2)',
                            backdropFilter: 'blur(15px)',
                            WebkitBackdropFilter: 'blur(15px)',
                            mx: 'auto'
                        }}
                    >
                        <Typography variant="h4" component="h1" mb={3}>
                            Zarejestruj się
                        </Typography>

                        <Box component="form" onSubmit={handleRegister} sx={{ width: '100%' }}>
                            <TextField
                                label="Imię"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                InputProps={{ sx: textFieldStyle }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={inputOutlineStyle}
                            />
                            <TextField
                                label="Email"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                type="email"
                                value={mail}
                                onChange={(e) => setMail(e.target.value)}
                                InputProps={{ sx: textFieldStyle }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={inputOutlineStyle}
                            />
                            <TextField
                                label="Hasło"
                                variant="outlined"
                                fullWidth
                                margin="normal"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                InputProps={{ sx: textFieldStyle }}
                                InputLabelProps={{ sx: inputLabelStyle }}
                                sx={inputOutlineStyle}
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
                                    mt: 3,
                                    backgroundColor: '#000',
                                    color: '#fff',
                                    '&:hover': {
                                        backgroundColor: '#333'
                                    }
                                }}
                            >
                                ZAREJESTRUJ SIĘ
                            </Button>
                        </Box>

                        <Typography
                            variant="body2"
                            mt={2}
                            sx={{
                                cursor: 'pointer',
                                textDecoration: 'underline',
                                color: 'black'
                            }}
                            onClick={() => navigate('/login')}
                        >
                            Masz już konto? Zaloguj się
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
};

export default RegisterPage;
