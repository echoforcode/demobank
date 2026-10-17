import { useEffect,useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { registerUser, resetStatus } from '../features/usersSlice.js';
import { openSpinner, closeSpinner } from '../features/pageSlice.js';
import AuthLayout from '../components/AuthLayout.jsx';
import InputComponent from '../components/InputComponent.jsx';
import{validateUser,registrationPayload} from '../helper/ValidateUser.jsx';
const fields = [
    ['firstname', 'First name', 'text', true],
    ['lastname', 'Last name', 'text', true],
    ['username', 'Email', 'email', true],
    ['tel', 'Phone (optional, digits only)', 'tel', false],
    ['dob', 'Date of birth (optional)', 'date', false],
    ['password', 'Password', 'password', true],
    ['confirmPassword', 'Confirm password', 'password', true],
];

export default function Register() {
    const [form, setForm] = useState({
        firstname: '', lastname: '', username: '', tel: '', dob: '',
        gender: '', password: '', confirmPassword: '',
    });
    
    const { status, error } = useSelector(state => state.user);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    useEffect(() => {
        dispatch(resetStatus());
            }, [dispatch]);
    const[errors, setErrors] = useState({});

    function change(event) {
    const { name, value } = event.target;
    setForm(previous => ({ ...previous, [name]: value }));
    }
    async function submit(event) {
        event.preventDefault();
        if (status === 'pending') return;
        const nextErrors = validateUser(form);
        setErrors(nextErrors);
        if (Object.keys(nextErrors).length > 0) return;
        dispatch(openSpinner());
        try {
            await dispatch(registerUser(registrationPayload(form))).unwrap();
            navigate('/successful', { replace: true });
        } catch {
// The rejected reducer has stored the error for the page.
        } finally {
            dispatch(closeSpinner());
}
}
return (
    <AuthLayout title="Create your account"
        footer={<Link className="link" to="/login">Log in</Link>}>
        <form onSubmit={submit} noValidate className="flex flex-col gap-4">
            {fields.map(([name, label, type, required]) => (
            <InputComponent key={name} name={name} label={label}
                type={type} required={required}
                value={form[name]} onChange={change} error={errors[name]}
            autoComplete={type === 'password' ? 'new-password' : undefined} />
    ))}
        <label className="flex flex-col gap-1.5 text-sm">
            Gender (optional)
            <select className="field" name="gender"
                value={form.gender} onChange={change}>
                <option value="">Prefer not to say</option>
                <option value="female">Female</option>
                <option value="male">Male</option>
                <option value="unspecified">Other / unspecified</option>
            </select>
        </label>
        {error && <p role="alert" className="text-red-700">{error}</p>}
            <button className="primary" type="submit"
                disabled={status === 'pending'}>Sign up</button>
        </form>
    </AuthLayout>
);
}
