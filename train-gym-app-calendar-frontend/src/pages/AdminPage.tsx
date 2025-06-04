import React, { useState, useEffect, useCallback } from 'react';
import {
    Container, Box, Typography, Table, TableHead,
    TableRow, TableCell, TableBody, IconButton,
    TextField, CircularProgress
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';
import { useAuth } from '../useAuth';

interface User {
    id: number;
    name: string;
    mail: string;
    trainer: boolean;
    admin: boolean;
}

const AdminPage: React.FC = () => {
    const { user, loading: authLoading } = useAuth();
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(false);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editedName, setEditedName] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        if (!authLoading) {
            if (!user) navigate('/login', { replace: true });
            else if (!user.admin) navigate('/main', { replace: true });
        }
    }, [user, authLoading, navigate]);

    const fetchUsers = useCallback(async () => {
        setLoading(true);
        try {
            const token = localStorage.getItem('token') ?? '';
            const res = await api.get<User[]>('/api/user');
            setUsers(res.data);
        } catch (err) {
            console.error('Could not fetch users', err);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        if (!authLoading && user?.admin) {
            fetchUsers();
        }
    }, [user, authLoading, fetchUsers]);

    const startEditing = (u: User) => {
        setEditingId(u.id);
        setEditedName(u.name);
    };
    const cancelEditing = () => {
        setEditingId(null);
        setEditedName('');
    };
    const saveName = async (id: number) => {
        try {
            const token = localStorage.getItem('token') ?? '';
            await api.patch(`/api/user/updateAdm/${id}`, { name: editedName });
            setEditingId(null);
            fetchUsers();
        } catch (err) {
            console.error('Failed to update name', err);
        }
    };
    const deleteUser = async (id: number) => {
        if (!window.confirm('Are you sure you want to delete this user?')) return;
        try {
            const token = localStorage.getItem('token') ?? '';
            await api.delete(`/api/user/deleteAdm/${id}`);
            fetchUsers();
        } catch (err) {
            console.error('Failed to delete user', err);
        }
    };

    if (authLoading || loading) {
        return (
            <Box sx={{
                width: '100%',
                height: '60vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
            }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Container sx={{ mt: 4 }}>
            <Box sx={{ backgroundColor: 'rgba(240,240,240,0.9)', borderRadius: 2, p: 4 }}>
                <Typography variant="h4" gutterBottom>Admin Panel</Typography>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Name</TableCell>
                            <TableCell>Trainer</TableCell>
                            <TableCell>Admin</TableCell>
                            <TableCell align="center">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map(u => (
                            <TableRow key={u.id}>
                                <TableCell>{u.id}</TableCell>
                                <TableCell>{u.mail}</TableCell>
                                <TableCell>
                                    {editingId === u.id
                                        ? <TextField size="small" value={editedName} onChange={e => setEditedName(e.target.value)} />
                                        : u.name}
                                </TableCell>
                                <TableCell>{u.trainer ? 'Yes' : 'No'}</TableCell>
                                <TableCell>{u.admin ? 'Yes' : 'No'}</TableCell>
                                <TableCell align="center">
                                    {editingId === u.id
                                        ? <>
                                            <IconButton onClick={() => saveName(u.id)}><SaveIcon /></IconButton>
                                            <IconButton onClick={cancelEditing}><CancelIcon /></IconButton>
                                        </>
                                        : <>
                                            <IconButton onClick={() => startEditing(u)}><EditIcon /></IconButton>
                                            <IconButton onClick={() => deleteUser(u.id)}><DeleteIcon /></IconButton>
                                        </>}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </Box>
        </Container>
    );
};

export default AdminPage;
