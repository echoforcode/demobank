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

const router = createBrowserRouter([
{ path: '/', element: <Navigate to="/login" replace /> },
{ path: '/login', element: <Login /> },
{ path: '/signup', element: <Register /> },
{ path: '/successful', element: <RegisterSuccessful /> },
{ path: '/dashboard', element: <Dashboard /> },
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
