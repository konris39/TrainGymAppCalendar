import React, { useEffect, useState } from 'react';
import {
    Box,
    Card,
    CardContent,
    TextField,
    IconButton,
    Divider,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '@mui/material/styles';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import axios from 'axios';

type UserData = {
    id: number;
    name: string;
    mail: string;
};

type DataUser = {
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
};

const ProfilePage: React.FC = () => {
    const navigate = useNavigate();
    const theme = useTheme();

    const [dataUser, setDataUser] = useState<DataUser | null>(null);
    const [initialDataUser, setInitialDataUser] = useState<DataUser | null>(null);
    const [editMode, setEditMode] = useState(false);

    const [editName, setEditName] = useState('');
    const [editMail, setEditMail] = useState('');
    const [editWeight, setEditWeight] = useState(0);
    const [editHeight, setEditHeight] = useState(0);
    const [editAge, setEditAge] = useState(0);
    const [editBP, setEditBP] = useState(0);
    const [editSQ, setEditSQ] = useState(0);
    const [editDL, setEditDL] = useState(0);

    const [confirmOpen, setConfirmOpen] = useState(false);
    const [deleteId, setDeleteId] = useState<number | null>(null);

    const token = localStorage.getItem('token');

    const fetchProfile = async () => {
        if (!token) return;
        try {
            // Pobierz POJEDYNCZY obiekt, nie tablicę!
            const res = await axios.get<DataUser>('/api/data/my', { headers: { Authorization: `Bearer ${token}` } });
            const fetched = res.data;
            setDataUser(fetched);
            setInitialDataUser(fetched);
            setEditName(fetched.user.name);
            setEditMail(fetched.user.mail);
            setEditWeight(fetched.weight);
            setEditHeight(fetched.height);
            setEditAge(fetched.age);
            setEditBP(fetched.bp);
            setEditSQ(fetched.sq);
            setEditDL(fetched.dl);
        } catch (err) {
            console.error('Error fetching profile:', err);
        }
    };

    useEffect(() => {
        fetchProfile();
    }, []);

    const handleEditToggle = () => setEditMode(true);
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
        const promises: Promise<any>[] = [];
        if (editName !== dataUser.user.name) {
            promises.push(
                axios.patch(`/api/user/updateName/${userId}`, { name: editName }, { headers: { Authorization: `Bearer ${token}` } })
            );
        }
        if (editMail !== dataUser.user.mail) {
            promises.push(
                axios.patch(`/api/user/updateMail/${userId}`, { mail: editMail }, { headers: { Authorization: `Bearer ${token}` } })
            );
        }
        const patchData: Partial<DataUser> = {};
        if (editWeight !== dataUser.weight) patchData.weight = editWeight;
        if (editHeight !== dataUser.height) patchData.height = editHeight;
        if (editAge !== dataUser.age) patchData.age = editAge;
        if (editBP !== dataUser.bp) patchData.bp = editBP;
        if (editSQ !== dataUser.sq) patchData.sq = editSQ;
        if (editDL !== dataUser.dl) patchData.dl = editDL;
        if (Object.keys(patchData).length) {
            promises.push(
                axios.patch('/api/data/update', patchData, { headers: { Authorization: `Bearer ${token}` } })
            );
        }
        try {
            await Promise.all(promises);
            await fetchProfile();
            setEditMode(false);
        } catch (err) {
            console.error('Error saving profile:', err);
        }
    };

    const requestDelete = (id: number) => {
        setDeleteId(id);
        setConfirmOpen(true);
    };
    const cancelDelete = () => setConfirmOpen(false);
    const confirmDelete = async () => {
        if (!token || deleteId === null) return;
        try {
            await axios.delete(`/api/data/${deleteId}`, { headers: { Authorization: `Bearer ${token}` } });
            setConfirmOpen(false);
            await fetchProfile();
        } catch (err) {
            console.error('Error deleting record:', err);
        }
    };

    return (
        <Box sx={{ mt: -12, display: 'flex', justifyContent: 'center', mb: 4 }}>
            <Card sx={{ width: '100%', maxWidth: 800, bgcolor: 'rgba(230,230,230,0.85)', p: 4, boxShadow: 3, borderRadius: 2 }}>
                <CardContent sx={{ position: 'relative' }}>
                    <Typography variant="h4" sx={{ mb: 2, fontWeight: 700, textAlign: 'center' }}>
                        Profil
                    </Typography>

                    {!editMode ? (
                        <IconButton sx={{ position: 'absolute', top: 16, right: 16 }} onClick={handleEditToggle}>
                            <EditIcon />
                        </IconButton>
                    ) : (
                        <>
                            <IconButton sx={{ position: 'absolute', top: 16, right: 56 }} onClick={handleSave}>
                                <SaveIcon />
                            </IconButton>
                            <IconButton sx={{ position: 'absolute', top: 16, right: 16 }} onClick={handleCancel}>
                                <CancelIcon />
                            </IconButton>
                        </>
                    )}

                    {/* Basic Info */}
                    <Box sx={{ mb: 3 }}>
                        <Divider sx={{ mb: 1 }} />
                        <Typography variant="h6" sx={{ mb: 1 }}>Podstawowe informacje</Typography>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            {editMode ? (
                                <>
                                    <TextField label="Name" size="small" value={editName} onChange={e => setEditName(e.target.value)} />
                                    <TextField label="Mail" size="small" value={editMail} onChange={e => setEditMail(e.target.value)} />
                                </>
                            ) : (
                                <>
                                    <Typography>{`Name: ${dataUser?.user.name}`}</Typography>
                                    <Typography>{`Mail: ${dataUser?.user.mail}`}</Typography>
                                </>
                            )}
                        </Box>
                    </Box>

                    {/* Additional Data */}
                    <Box sx={{ mb: 3 }}>
                        <Divider sx={{ mb: 1 }} />
                        <Typography variant="h6" sx={{ mb: 1 }}>Dodatkowe dane</Typography>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            {(['Weight','Height','Age','Bench Press','Squat','Deadlift'] as const).map((label, idx) => {
                                const field = ['weight','height','age','bp','sq','dl'][idx] as keyof DataUser;
                                const editVal = [editWeight,editHeight,editAge,editBP,editSQ,editDL][idx] as number;
                                const setters = [setEditWeight,setEditHeight,setEditAge,setEditBP,setEditSQ,setEditDL] as const;
                                const setFn = setters[idx];
                                return editMode ? (
                                    <TextField
                                        key={label}
                                        label={`${label}${field==='height'?' (m)':''}`}
                                        type="number"
                                        size="small"
                                        value={editVal}
                                        onChange={e => setFn(Number(e.target.value))}
                                    />
                                ) : (
                                    <Typography key={label}>{`${label}: ${dataUser?.[field] ?? 0}${field==='height'?' m':''}`}</Typography>
                                );
                            })}
                        </Box>
                    </Box>

                    {/* Calculations */}
                    <Box>
                        <Divider sx={{ mb: 1 }} />
                        <Typography variant="h6" sx={{ mb: 1 }}>Wyniki obliczeń</Typography>
                        <Typography>{`BMI: ${dataUser?.bmi !== undefined && dataUser?.bmi !== null ? dataUser.bmi.toFixed(2) : '–'}`}</Typography>
                        <Typography>{`SUM: ${dataUser?.sum !== undefined && dataUser?.sum !== null ? dataUser.sum.toFixed(2) : '–'}`}</Typography>
                    </Box>

                    <Dialog open={confirmOpen} onClose={cancelDelete}>
                        <DialogTitle>Potwierdź usunięcie</DialogTitle>
                        <DialogContent>
                            <Typography>Czy na pewno chcesz usunąć ten rekord?</Typography>
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={cancelDelete}>Anuluj</Button>
                            <Button onClick={confirmDelete} color="error">Usuń</Button>
                        </DialogActions>
                    </Dialog>
                </CardContent>
            </Card>
        </Box>
    );
};

export default ProfilePage;
