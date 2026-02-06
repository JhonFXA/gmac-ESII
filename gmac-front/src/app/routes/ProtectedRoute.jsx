import {Navigate} from 'react-router-dom';
import {useAuth} from '../providers/AuthContext';

export function ProtectedRoute({children, allowedProfiles}) {
    const {token, perfil} = useAuth();

    if (!token) {
        return <Navigate to="/" replace />;
    }
    
    if (allowedProfiles && !allowedProfiles.includes(perfil)) {
        return <Navigate to="/painel-principal" replace />;
    }

    return children;
}