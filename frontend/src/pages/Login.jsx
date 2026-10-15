import { useState } from 'react';
import { Link } from 'react-router-dom';
import AuthLayout from '../components/AuthLayout.jsx';
import InputComponent from '../components/InputComponent.jsx';
export default function Login() {
    const [form, setForm] = useState({ username: '', password: '' });
    const [message, setMessage] = useState('');
    function change(event) {
    const { name, value } = event.target;
    setForm(previous => ({ ...previous, [name]: value }));
    }
    function submit(event) {
        event.preventDefault();
        setMessage('Form captured locally. API connection comes later.');
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
            <p role="status">{message}</p>
            <button className="primary" type="submit">Log in</button>
        </form>
    </AuthLayout>
);
}   
