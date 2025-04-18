import React, { useEffect, useState } from 'react';
import {
    AppBar,
    Toolbar,
    Button,
    Typography,
    Box,
    Container,
    Card,
    CardContent,
    TextField,
    IconButton,
    Divider
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';

interface UserData {
    id: number;
    name: string;
    mail: string;
}

interface DataUser {
    id: number;
    weight: number;
    height: number;
    age: number;
    bmi?: number;
    bp: number;
    sq: number;
    dl: number;
    sum?: number;
    user: UserData;
}

const ProfilePage: React.FC = () => {
    const navigate = useNavigate();

    const [dataUser, setDataUser] = useState<DataUser | null>(null);
    const [initialDataUser, setInitialDataUser] = useState<DataUser | null>(null);

    const [editMode, setEditMode] = useState<boolean>(false);

    const [editName, setEditName] = useState<string>('');
    const [editMail, setEditMail] = useState<string>('');

    const [editWeight, setEditWeight] = useState<number>(0);
    const [editHeight, setEditHeight] = useState<number>(0);
    const [editAge, setEditAge] = useState<number>(0);
    const [editBP, setEditBP] = useState<number>(0);
    const [editSQ, setEditSQ] = useState<number>(0);
    const [editDL, setEditDL] = useState<number>(0);

    const token = localStorage.getItem('token');

    const fetchProfile = () => {
        if (!token) {
            console.error('Brak tokenu. Użytkownik nie jest zalogowany.');
            return;
        }
        axios
            .get<DataUser[]>('/api/data/my', {
                headers: { Authorization: `Bearer ${token}` }
            })
            .then((res) => {
                if (res.data && res.data.length > 0) {
                    const fetched = res.data[0];
                    setDataUser(fetched);
                    setInitialDataUser(JSON.parse(JSON.stringify(fetched)));
                    setEditName(fetched.user.name);
                    setEditMail(fetched.user.mail);
                    setEditWeight(fetched.weight);
                    setEditHeight(fetched.height);
                    setEditAge(fetched.age);
                    setEditBP(fetched.bp);
                    setEditSQ(fetched.sq);
                    setEditDL(fetched.dl);
                }
            })
            .catch((err) => {
                console.error('Błąd podczas pobierania danych profilu:', err);
            });
    };

    useEffect(() => {
        fetchProfile();
    }, []);

    const handleNavClick = (path: string) => {
        navigate(path);
    };

    const handleEditToggle = () => {
        setEditMode(true);
    };

    const handleCancel = () => {
        if (!initialDataUser) return;
        setEditName(initialDataUser.user.name);
        setEditMail(initialDataUser.user.mail);
        setEditWeight(initialDataUser.weight);
        setEditHeight(initialDataUser.height);
        setEditAge(initialDataUser.age);
        setEditBP(initialDataUser.bp);
        setEditSQ(initialDataUser.sq);
        setEditDL(initialDataUser.dl);
        setEditMode(false);
    };

    const handleSave = async () => {
        if (!token || !dataUser) return;
        const userId = dataUser.user.id;
        try {
            const promises: Promise<any>[] = [];
            if (editName !== dataUser.user.name) {
                promises.push(
                    axios.patch(`/api/user/updateName/${userId}`, { name: editName }, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                );
            }
            if (editMail !== dataUser.user.mail) {
                promises.push(
                    axios.patch(`/api/user/updateMail/${userId}`, { mail: editMail }, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                );
            }
            const patchData: Partial<DataUser> = {};
            if (editWeight !== dataUser.weight) patchData.weight = editWeight;
            if (editHeight !== dataUser.height) patchData.height = editHeight;
            if (editAge !== dataUser.age) patchData.age = editAge;
            if (editBP !== dataUser.bp) patchData.bp = editBP;
            if (editSQ !== dataUser.sq) patchData.sq = editSQ;
            if (editDL !== dataUser.dl) patchData.dl = editDL;

            if (Object.keys(patchData).length > 0) {
                console.log("PATCHING DATA: ", patchData);
                promises.push(
                    axios.patch('/api/data/update', patchData, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                );
            }

            await Promise.all(promises);
            await fetchProfile();
            setEditMode(false);
        } catch (err) {
            console.error('Błąd przy zapisie zmian profilu:', err);
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
                Twoja przeglądarka nie obsługuje wideo.
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

            <Container
                sx={{
                    mt: '84px',
                    position: 'relative',
                    zIndex: 1,
                    animation: 'fadeIn 1s',
                    '@keyframes fadeIn': {
                        from: { opacity: 0 },
                        to: { opacity: 1 }
                    },
                    maxWidth: 'md'
                }}
            >
                <Card
                    sx={{
                        backgroundColor: 'rgba(230,230,230,0.85)',
                        borderRadius: 2,
                        boxShadow: 3,
                        mx: 'auto',
                        p: 4,
                        mb: 4
                    }}
                >
                    <CardContent sx={{ position: 'relative' }}>
                        <Typography variant="h4" sx={{ fontWeight: 700, mb: 2 }}>
                            Profil
                        </Typography>

                        {/* Przyciski edycji / zatwierdzania / anulowania */}
                        {!editMode ? (
                            <IconButton
                                onClick={handleEditToggle}
                                sx={{ position: 'absolute', top: 16, right: 16, color: '#333' }}
                            >
                                <EditIcon />
                            </IconButton>
                        ) : (
                            <>
                                <IconButton
                                    onClick={handleSave}
                                    sx={{ position: 'absolute', top: 16, right: 56, color: '#333', mr: 1 }}
                                >
                                    <SaveIcon />
                                </IconButton>
                                <IconButton
                                    onClick={handleCancel}
                                    sx={{ position: 'absolute', top: 16, right: 16, color: '#333' }}
                                >
                                    <CancelIcon />
                                </IconButton>
                            </>
                        )}

                        {/* Podstawowe informacje */}
                        <Box sx={{ mb: 2 }}>
                            <Divider sx={{ mb: 1 }} />
                            <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>
                                Podstawowe informacje
                            </Typography>
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                                {!editMode ? (
                                    <>
                                        <Typography variant="body1">Name: {dataUser?.user.name}</Typography>
                                        <Typography variant="body1">Mail: {dataUser?.user.mail}</Typography>
                                    </>
                                ) : (
                                    <>
                                        <TextField
                                            label="Name"
                                            value={editName}
                                            onChange={(e) => setEditName(e.target.value)}
                                            size="small"
                                        />
                                        <TextField
                                            label="Mail"
                                            value={editMail}
                                            onChange={(e) => setEditMail(e.target.value)}
                                            size="small"
                                        />
                                    </>
                                )}
                            </Box>
                        </Box>

                        {/* Dodatkowe dane */}
                        <Box sx={{ mb: 2 }}>
                            <Divider sx={{ mb: 1 }} />
                            <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>
                                Dodatkowe dane
                            </Typography>
                            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                                {!editMode ? (
                                    <Typography variant="body1">Waga: {dataUser?.weight ?? 0} kg</Typography>
                                ) : (
                                    <TextField
                                        label="Waga (kg)"
                                        type="number"
                                        value={editWeight}
                                        onChange={(e) => setEditWeight(Number(e.target.value))}
                                        size="small"
                                    />
                                )}
                                {!editMode ? (
                                    <Typography variant="body1">Wzrost: {dataUser?.height ?? 0} m</Typography>
                                ) : (
                                    <TextField
                                        label="Wzrost (m)"
                                        type="number"
                                        value={editHeight}
                                        onChange={(e) => setEditHeight(Number(e.target.value))}
                                        size="small"
                                    />
                                )}
                                {!editMode ? (
                                    <Typography variant="body1">Wiek: {dataUser?.age ?? 0}</Typography>
                                ) : (
                                    <TextField
                                        label="Wiek"
                                        type="number"
                                        value={editAge}
                                        onChange={(e) => setEditAge(Number(e.target.value))}
                                        size="small"
                                    />
                                )}
                                {!editMode ? (
                                    <Typography variant="body1">Bench Press: {dataUser?.bp ?? 0}</Typography>
                                ) : (
                                    <TextField
                                        label="Bench Press"
                                        type="number"
                                        value={editBP}
                                        onChange={(e) => setEditBP(Number(e.target.value))}
                                        size="small"
                                    />
                                )}
                                {!editMode ? (
                                    <Typography variant="body1">Squat: {dataUser?.sq ?? 0}</Typography>
                                ) : (
                                    <TextField
                                        label="Squat"
                                        type="number"
                                        value={editSQ}
                                        onChange={(e) => setEditSQ(Number(e.target.value))}
                                        size="small"
                                    />
                                )}
                                {!editMode ? (
                                    <Typography variant="body1">Deadlift: {dataUser?.dl ?? 0}</Typography>
                                ) : (
                                    <TextField
                                        label="Deadlift"
                                        type="number"
                                        value={editDL}
                                        onChange={(e) => setEditDL(Number(e.target.value))}
                                        size="small"
                                    />
                                )}
                            </Box>
                        </Box>

                        {/* Wyniki obliczeń (BMI i SUM) */}
                        <Box sx={{ mb: 2 }}>
                            <Divider sx={{ mb: 1 }} />
                            <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>
                                Wyniki obliczeń
                            </Typography>
                            <Typography variant="body1">
                                BMI: {dataUser?.bmi !== undefined ? dataUser.bmi.toFixed(2) : '–'}
                            </Typography>
                            <Typography variant="body1">
                                SUM: {dataUser?.sum !== undefined ? dataUser.sum.toFixed(2) : '–'}
                            </Typography>
                        </Box>

                    </CardContent>
                </Card>
            </Container>
        </Box>
    );
};

export default ProfilePage;
