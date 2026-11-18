import { useEffect,useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { registerUser, resetStatus } from '../features/usersSlice.js';
import { openSpinner, closeSpinner } from '../features/pageSlice.js';
import{validateUser,registrationPayload} from '../helper/ValidateUser.jsx';
import {useNavigate } from 'react-router-dom';
import RegisterView from '../components/RegisterView.jsx';

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
    <RegisterView
    form={form}
    onChange={change}
    onSubmit={submit}
    errors={errors}
    status={status}
    error={error}
  />
);
}
