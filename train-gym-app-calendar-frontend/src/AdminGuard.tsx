import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from './useAuth';

const AdminGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const { user, loading } = useAuth();

    if (loading) return null;
    if (!user) return <Navigate to="/login" replace />;
    if (!user.admin) return <Navigate to="/main" replace />;

    return <>{children}</>;
};

export default AdminGuard;
