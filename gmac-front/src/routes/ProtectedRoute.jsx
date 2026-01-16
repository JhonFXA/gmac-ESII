import {Navigate} from 'react-router-dom';
import {useAuth} from '../context/AuthContext';

export function ProtectedRoute({children, allowedProfiles}) {
    const {token, perfil} = useAuth();

    if (!token) {
        return <Navigate to="/" replace />;
    }

    return children;
}