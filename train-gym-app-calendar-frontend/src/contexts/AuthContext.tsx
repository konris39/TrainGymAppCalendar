import React, { createContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';

interface User {
    id: number;
    name: string;
    mail: string;
    trainer: boolean;
    admin: boolean;
}

export interface AuthContextProps {
    user: User | null;
    loading: boolean;
    reload: () => void;
}

export const AuthContext = createContext<AuthContextProps>({
    user: null,
    loading: true,
    reload: () => {}
});

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    const loadUser = async () => {
        setLoading(true);
        const token = localStorage.getItem('token');
        if (!token) {
            setUser(null);
            setLoading(false);
            return;
        }
        try {
            const res = await axios.get<User>('/api/user/me', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUser(res.data);
        } catch {
            setUser(null);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUser();
    }, []);

    return (
        <AuthContext.Provider value={{ user, loading, reload: loadUser }}>
            {children}
        </AuthContext.Provider>
    );
};
