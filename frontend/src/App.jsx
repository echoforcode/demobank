import {
createBrowserRouter, Navigate, RouterProvider,
} from 'react-router-dom';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import RegisterSuccessful from './pages/RegisterSuccessful.jsx';
import Dashboard from './pages/Dashboard.jsx';
import { useSelector } from 'react-redux';
import Spinner from './components/Spinner.jsx';
import { selectSpinner } from './features/pageSlice.js';
import ProtectedRoute from './pages/ProtectedRoute.jsx';
//add the protected route to the dashboard page
const router = createBrowserRouter([
{ path: '/', element: <Navigate to="/login" replace /> },
{ path: '/login', element: <Login /> },
{ path: '/signup', element: <Register /> },
{ path: '/successful', element: <RegisterSuccessful /> },
{ path: '/dashboard', 
    element: <ProtectedRoute><Dashboard /></ProtectedRoute>,
 },
]);

export default function App() {
    const showSpinner = useSelector(selectSpinner);
    return (
        <>
            <RouterProvider router={router} />
            {showSpinner && <Spinner />}
        </>
);
}
