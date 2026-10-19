import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { api, errorMessage } from '../api/api.js';
import { logout } from '../features/usersSlice.js';

export default function Dashboard() {
    const { user, token } = useSelector(state => state.user);
    const dispatch = useDispatch();
    const [message, setMessage] = useState('');
    const [busy, setBusy] = useState(false);
    async function verify() {
        setBusy(true);
        setMessage('Checking...');
    try {
        const { data } = await api.get('/user/me', {
        headers: { Authorization: `Bearer ${token}` },
    });
    setMessage(`Server confirmed: ${data.username}`);
    } catch (error) {
        if (error.response?.status === 401) dispatch(logout());
        else setMessage(errorMessage(error));
    } finally {
    setBusy(false);
}
}
return (
<main className="mx-auto max-w-2xl p-8 text-slate-900">
    <h1 className="mb-4 text-3xl font-bold">
        Hello, {user.firstname || user.username}
    </h1>
    <p className="mb-6">Your login checkpoint is ready.</p>
    <div className="flex flex-wrap gap-4">
        <button className="primary" disabled={busy} onClick={verify}>
        Verify protected API
        </button>
        <button className="link" onClick={() => dispatch(logout())}>
        Log out
        </button>
    </div>
    <p role="status" className="mt-6">{message}</p>
</main>
);
}