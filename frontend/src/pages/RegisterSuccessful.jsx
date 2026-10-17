import { Link } from 'react-router-dom';
import AuthLayout from '../components/AuthLayout.jsx';

export default function RegisterSuccessful() {
    return (
        <AuthLayout title="Registration successful">
            <p>Your profile is ready.</p>
            <Link className="link" to="/login">Log in</Link>
        </AuthLayout>
);
}