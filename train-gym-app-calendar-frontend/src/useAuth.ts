import { useState, useEffect } from 'react';
import axios from 'axios';

type User = {
  id: number;
  name: string;
  mail: string;
  admin: boolean;
  trainer: boolean;
};

export const useAuth = (): {
  user: User | null;
  loading: boolean;
  sessionExpired: boolean;
  setSessionExpired: (val: boolean) => void;
} => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [sessionExpired, setSessionExpired] = useState(false);

  useEffect(() => {
    let isMounted = true;

    const fetchUser = async () => {
      setLoading(true);
      try {
        const res = await axios.get('/api/user/me', { withCredentials: true });
        if (isMounted) {
          setUser(res.data);
          setSessionExpired(false);
        }
      } catch (err: any) {
        if (err?.response?.status === 401) {
          try {
            await axios.post('/api/auth/refresh', {}, { withCredentials: true });
            const res2 = await axios.get('/api/user/me', { withCredentials: true });
            if (isMounted) {
              setUser(res2.data);
              setSessionExpired(false);
            }
          } catch (refreshErr) {
            if (isMounted) {
              setUser(null);
              setSessionExpired(true);
            }
          }
        } else {
          setUser(null);
        }
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    fetchUser();

    return () => { isMounted = false; };
  }, []);

  return { user, loading, sessionExpired, setSessionExpired };
};
