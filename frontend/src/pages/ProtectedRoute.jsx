import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
//check the in-memory login state
export default function ProtectedRoute({ children }) {
    const { token, user } = useSelector(state => state.user);
    return token && user ? children : <Navigate to="/login" replace />;
}