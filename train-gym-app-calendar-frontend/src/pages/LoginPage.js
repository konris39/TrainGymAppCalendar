import React, { useState } from 'react';
import {
    Box,
    Paper,
    Typography,
    TextField,
    Button,
    Grid
} from '@mui/material';
import { useNavigate } from 'react-router-dom';

function LoginPage() {
    const navigate = useNavigate();
    const [mail, setMail] = useState('');
    const [password, setPassword] = useState('');

    // Obsługa przycisku logowania
    const handleLogin = (e) => {
        e.preventDefault();
        // TODO: wywołaj endpoint logowania (axios/fetch)
        console.log('Login data:', { mail, password });
        navigate('/main');
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
            <Grid container justifyContent="center">
                <Grid item xs={11} sm={8} md={5} lg={4}>
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
                            WebkitBackdropFilter: 'blur(15px)'
                        }}
                    >
                        <Typography variant="h4" component="h1" mb={3}>
                            Zaloguj się
                        </Typography>

                        <Box component="form" onSubmit={handleLogin} sx={{ width: '100%' }}>
                            <TextField
                                label="Email"
                                variant="outlined"
                                fullWidth
                                margin="normal"
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
                                ZALOGUJ SIĘ
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
                            onClick={() => navigate('/register')}
                        >
                            Nie masz konta? Zarejestruj się
                        </Typography>
                    </Paper>
                </Grid>
            </Grid>
        </Box>
    );
}

export default LoginPage;
