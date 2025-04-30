import React, {JSX, useContext} from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';

const AdminRoute: React.FC<{ children: JSX.Element }> = ({ children }) => {
    const { user, loading } = useContext(AuthContext);

    if (loading) {
        return null;
    }

    if (!user?.admin) {
        return <Navigate to="/main" replace />;
    }

    return children;
};

export default AdminRoute;
