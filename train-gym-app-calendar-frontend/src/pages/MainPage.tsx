import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    IconButton,
    Menu,
    MenuItem,
    Button,
    Paper
} from '@mui/material';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import FilterListIcon from '@mui/icons-material/FilterList';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// Typ treningu:
type RecommendedTraining = {
    id: number;
    name: string;
    description: string;
    type: string;
};

const MainPage: React.FC = () => {
    const navigate = useNavigate();
    const [trainings, setTrainings] = useState<RecommendedTraining[]>([]);
    const [filterAnchor, setFilterAnchor] = useState<null | HTMLElement>(null);
    const [allTypes, setAllTypes] = useState<string[]>([]);
    const [activeTypes, setActiveTypes] = useState<string[]>([]);
    const [loading, setLoading] = useState(false);

    const fetchTrainings = () => {
        setLoading(true);
        const token = localStorage.getItem('token');
        axios.get('/api/recommended-trainings', {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => {
                setTrainings(res.data);
                const types: string[] = Array.from(new Set(res.data.map((t: RecommendedTraining) => t.type)));
                setAllTypes(types);
                setActiveTypes(types);
            })
            .catch(() => {
                setTrainings([]);
                setAllTypes([]);
                setActiveTypes([]);
            })
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchTrainings();
    }, []);

    const handleOpenFilter = (event: React.MouseEvent<HTMLButtonElement>) => {
        setFilterAnchor(event.currentTarget);
    };

    const handleToggleType = (type: string) => {
        setActiveTypes(prev =>
            prev.includes(type) ? prev.filter(t => t !== type) : [...prev, type]
        );
    };

    const filteredTrainings = trainings.filter(t => activeTypes.includes(t.type));

    const handleAdd = (training: RecommendedTraining) => {
        navigate('/add-workout', { state: { name: training.name, description: training.description } });
    };

    const handleGetNewRecommended = async () => {
        try {
            setLoading(true);
            const token = localStorage.getItem('token');
            await axios.post('/api/recommended-trainings/assign-to-user', {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchTrainings();
        } catch (e) {
            alert('Błąd podczas przypisywania nowych treningów!');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ px: 3, py: 5 }}>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    minHeight: '40vh',
                    textAlign: 'center',
                    backgroundColor: 'rgba(0, 0, 0, 0.72)',
                    backdropFilter: 'blur(6px)',
                    borderRadius: 4,
                    mb: 4,
                    py: 4
                }}
            >
                <Typography
                    sx={{ color: '#fff', mb: 2, fontWeight: 100, fontFamily: 'Yu Gothic Light', fontSize: 60 }}
                >
                    GET STRONGER WITH US!
                </Typography>
                <Typography sx={{ color: '#fff', fontSize: 24, mb: 2 }}>
                    Your journey to a healthier, stronger life begins now.
                </Typography>
                <Button
                    variant="contained"
                    onClick={() => navigate('/add-workout')}
                    sx={{
                        backgroundColor: '#000',
                        color: '#fff',
                        '&:hover': { backgroundColor: '#222' },
                        px: 6,
                        py: 2,
                        fontSize: '1rem',
                        mt: 2,
                        boxShadow: 2,
                        borderRadius: 2,
                        letterSpacing: 1,
                        fontWeight: 600
                    }}
                >
                    ADD WORKOUT
                </Button>
            </Box>

            <Box sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                mb: 2
            }}>
                <Button
                    variant="contained"
                    startIcon={<RefreshIcon />}
                    onClick={handleGetNewRecommended}
                    sx={{
                        fontWeight: 600,
                        px: 3,
                        color: '#fff',
                        backgroundColor: '#000',
                        borderRadius: 2,
                        letterSpacing: 1,
                        boxShadow: 2,
                        '&:hover': { backgroundColor: '#222' }
                    }}
                    disabled={loading}
                >
                    GET NEW RECOMMENDED TRAININGS
                </Button>
                <Button
                    variant="contained"
                    startIcon={<FilterListIcon />}
                    onClick={handleOpenFilter}
                    sx={{
                        px: 3,
                        py: 1,
                        boxShadow: 2,
                        color: '#fff',
                        backgroundColor: '#000',
                        borderRadius: 2,
                        letterSpacing: 1,
                        fontWeight: 600,
                        '&:hover': { backgroundColor: '#222' }
                    }}
                >
                    FILTRUJ
                </Button>
                <Menu
                    anchorEl={filterAnchor}
                    open={Boolean(filterAnchor)}
                    onClose={() => setFilterAnchor(null)}
                >
                    {allTypes.map(type => (
                        <MenuItem key={type} onClick={() => handleToggleType(type)}>
                            <input
                                type="checkbox"
                                checked={activeTypes.includes(type)}
                                readOnly
                                style={{ marginRight: 8 }}
                            />
                            {type.charAt(0).toUpperCase() + type.slice(1).toLowerCase()}
                        </MenuItem>
                    ))}
                </Menu>
            </Box>

            <Box
                sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr' },
                    gap: 4,
                }}
            >
                {filteredTrainings.map(training => (
                    <Paper
                        key={training.id}
                        sx={{
                            minHeight: 240,
                            position: 'relative',
                            display: 'flex',
                            flexDirection: 'column',
                            borderRadius: 4,
                            boxShadow: 6,
                            background: '#fff',
                            transition: 'transform 0.15s, box-shadow 0.15s',
                            '&:hover': {
                                transform: 'scale(1.02)',
                                boxShadow: 12,
                            },
                            overflow: 'hidden'
                        }}
                    >
                        {/* Górny pasek z nazwą i typem */}
                        <Box
                            sx={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                p: 2,
                                pb: 0,
                                background: 'transparent'
                            }}
                        >
                            <Typography sx={{ fontWeight: 900, fontSize: 22, color: '#161616' }}>
                                {training.name}
                            </Typography>
                            <Box sx={{
                                background: '#000',
                                color: '#fff',
                                fontWeight: 700,
                                fontSize: 15,
                                borderRadius: '20px',
                                px: 2,
                                py: 0.5,
                                ml: 2,
                                minWidth: 64,
                                textAlign: 'center',
                                letterSpacing: 1
                            }}>
                                {training.type.charAt(0).toUpperCase() + training.type.slice(1).toLowerCase()}
                            </Box>
                        </Box>

                        {/* Opis */}
                        <Typography
                            sx={{
                                px: 2,
                                pt: 2,
                                pb: 5,
                                flexGrow: 1,
                                color: '#222',
                                fontSize: 16,
                                whiteSpace: 'pre-line'
                            }}
                        >
                            {training.description}
                        </Typography>

                        {/* Zielony plusik */}
                        <IconButton
                            onClick={() => handleAdd(training)}
                            sx={{
                                position: 'absolute',
                                bottom: 18,
                                right: 18,
                                bgcolor: '#fff',
                                border: '2px solid #31a356',
                                boxShadow: 2,
                                '&:hover': { bgcolor: '#caffcd' }
                            }}
                        >
                            <AddCircleIcon sx={{ fontSize: 44, color: '#31a356' }} />
                        </IconButton>
                    </Paper>
                ))}
            </Box>

            {filteredTrainings.length === 0 && (
                <Typography align="center" sx={{ mt: 5, color: '#777' }}>
                    Brak treningów spełniających wybrane filtry.
                </Typography>
            )}
        </Box>
    );
};

export default MainPage;
