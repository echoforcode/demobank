import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { authenticateUser, resetStatus } from '../features/usersSlice.js';
import { openSpinner, closeSpinner } from '../features/pageSlice.js';
import AuthLayout from '../components/AuthLayout.jsx';
import InputComponent from '../components/InputComponent.jsx';
//apply the registration pattern to login
export default function Login() {
    const [form, setForm] = useState({ username: '', password: '' });
    const { status, error } = useSelector(state => state.user);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    useEffect(() => {
        dispatch(resetStatus());
    }, [dispatch]);
    function change(event) {
        const { name, value } = event.target;
        setForm(previous => ({ ...previous, [name]: value }));
    }
    async function submit(event) {
        event.preventDefault();
        if (status === 'pending') return;
        dispatch(openSpinner());
    try {
        const credentials = {
        username: form.username.trim(), password: form.password,
    };
    await dispatch(authenticateUser(credentials)).unwrap();
    navigate('/dashboard', { replace: true });
    } catch {
// The rejected reducer has stored the error for the page.
    } finally {
    dispatch(closeSpinner());
    }
    }
return (
    <AuthLayout title="Log in to your account"
    footer={<Link className="link" to="/signup">Create an account</Link>}>
    <form onSubmit={submit} className="flex flex-col gap-5">
        <InputComponent label="Email" name="username" type="email"
            value={form.username} onChange={change} autoComplete="username" />
        <InputComponent label="Password" name="password" type="password"
            value={form.password} onChange={change}
        autoComplete="current-password" />
        {error && <p role="alert" className="text-red-700">{error}</p>}
        <button className="primary" type="submit"
            disabled={status === 'pending' || !form.username.trim()
            || !form.password}>Log in</button>
    </form>
    </AuthLayout>
);
}