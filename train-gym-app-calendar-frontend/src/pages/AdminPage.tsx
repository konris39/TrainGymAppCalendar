import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Container,
    Typography,
    Table,
    TableHead,
    TableRow,
    TableCell,
    TableBody,
    IconButton,
    TextField,
    Button,
    Box
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SaveIcon from '@mui/icons-material/Save';
import CancelIcon from '@mui/icons-material/Cancel';

interface User {
    id: number;
    name: string;
    mail: string;
    trainer: boolean;
    admin: boolean;
}

const AdminPage: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [editingId, setEditingId] = useState<number | null>(null);
    const [editedName, setEditedName] = useState<string>('');
    const token = localStorage.getItem('token') || '';

    const fetchUsers = async () => {
        try {
            const res = await axios.get<User[]>('/api/user', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUsers(res.data);
        } catch (err) {
            console.error('Could not fetch users', err);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const startEditing = (user: User) => {
        setEditingId(user.id);
        setEditedName(user.name);
    };

    const cancelEditing = () => {
        setEditingId(null);
        setEditedName('');
    };

    const saveName = async (id: number) => {
        try {
            await axios.patch(
                `/api/user/updateName/${id}`,
                { name: editedName },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setEditingId(null);
            fetchUsers();
        } catch (err) {
            console.error('Failed to update name', err);
        }
    };

    const deleteUser = async (id: number) => {
        if (!window.confirm('Are you sure you want to delete this user?')) return;
        try {
            await axios.delete(`/api/user/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchUsers();
        } catch (err) {
            console.error('Failed to delete user', err);
        }
    };

    return (
        <Container sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom>
                Admin Panel
            </Typography>
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
                    {users.map(user => (
                        <TableRow key={user.id}>
                            <TableCell>{user.id}</TableCell>
                            <TableCell>{user.mail}</TableCell>
                            <TableCell>
                                {editingId === user.id ? (
                                    <TextField
                                        value={editedName}
                                        size="small"
                                        onChange={e => setEditedName(e.target.value)}
                                    />
                                ) : (
                                    user.name
                                )}
                            </TableCell>
                            <TableCell>{user.trainer ? 'Yes' : 'No'}</TableCell>
                            <TableCell>{user.admin ? 'Yes' : 'No'}</TableCell>
                            <TableCell align="center">
                                {editingId === user.id ? (
                                    <Box>
                                        <IconButton
                                            size="small"
                                            onClick={() => saveName(user.id)}
                                        >
                                            <SaveIcon />
                                        </IconButton>
                                        <IconButton size="small" onClick={cancelEditing}>
                                            <CancelIcon />
                                        </IconButton>
                                    </Box>
                                ) : (
                                    <Box>
                                        <IconButton size="small" onClick={() => startEditing(user)}>
                                            <EditIcon />
                                        </IconButton>
                                        <IconButton
                                            size="small"
                                            onClick={() => deleteUser(user.id)}
                                        >
                                            <DeleteIcon />
                                        </IconButton>
                                    </Box>
                                )}
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
            <Box sx={{ mt: 2 }}>
                <Button variant="contained" onClick={fetchUsers}>
                    Refresh
                </Button>
            </Box>
        </Container>
    );
};

export default AdminPage;
