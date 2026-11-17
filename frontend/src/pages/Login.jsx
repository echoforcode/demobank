import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { authenticateUser, resetStatus } from '../features/usersSlice.js';
import { openSpinner, closeSpinner } from '../features/pageSlice.js';
import LoginView from '../components/LoginView.jsx';
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
    <LoginView
        form={form}
        onChange={change}
        onSubmit={submit}
        status={status}
        error={error}
  />
);
}