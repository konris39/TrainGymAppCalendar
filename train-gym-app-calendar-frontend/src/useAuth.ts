import { useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
    sub: string;
    admin: boolean;
    trainer: boolean;
    exp: number;
}

export const useAuth = (): { user: DecodedToken | null; loading: boolean } => {
    const [user, setUser] = useState<DecodedToken | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded: DecodedToken = jwtDecode(token);
                setUser(decoded);
            } catch (e) {
                console.error('Token decode error:', e);
                setUser(null);
            }
        } else {
            setUser(null);
        }
        setLoading(false);
    }, []);

    return { user, loading };
};
