import {
createBrowserRouter, Navigate, RouterProvider,
} from 'react-router-dom';
import { useSelector } from 'react-redux';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import RegisterSuccessful from './pages/RegisterSuccessful.jsx';
import Dashboard from './pages/Dashboard.jsx';
import ProtectedRoute from './pages/ProtectedRoute.jsx';
import Home from './pages/dashboard/Home.jsx';
import Accounts from './pages/dashboard/Accounts.jsx';
import Payments from './pages/dashboard/Payments.jsx';
import Convert from './pages/dashboard/Convert.jsx';
import Placeholder from './components/Placeholder.jsx';
import Spinner from './components/Spinner.jsx';
import { selectSpinner } from './features/pageSlice.js';
//add router
const router = createBrowserRouter([
    { path: '/', element: <Navigate to="/login" replace /> },
    { path: '/login', element: <Login /> },
    { path: '/signup', element: <Register /> },
    { path: '/successful', element: <RegisterSuccessful /> },
    {
        path: '/dashboard',
        element: <ProtectedRoute><Dashboard /></ProtectedRoute>,
        children: [
                    { index: true, element: <Home /> },
                    { path: 'accounts', element: <Accounts /> },
                    { path: 'payments', element: <Payments /> },
                    { path: 'convert', element: <Convert /> },
                    ...['transactions', 'cards', 'profile', 'settings'].map(path => ({
                        path,
                        element: <Placeholder title={path} />,
                    })),
                    { path: '*', element: <Placeholder title="Page not found" /> },
                ],
},
        { path: '*', element: <Navigate to="/login" replace /> },
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
