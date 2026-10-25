import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { createAccount } from '../../features/accountTasks.js';
import { currencies } from '../../helper/currencies.js';
import SectionContainer from '../SectionContainer.jsx';

export default function NewAccount({ onClose }) {
    const { items, create } = useSelector(state => state.accounts);
    const available = currencies.filter(currency =>
        !items.some(item => item.code === currency.code));
    const [choice, setChoice] = useState('');
    const code = available.some(item => item.code === choice)
        ? choice : available[0]?.code || '';
    const dispatch = useDispatch();
    const navigate = useNavigate();
    async function submit(event) {
        event.preventDefault();
        if (!code || create.status === 'pending') return;
        try {
            const account = await dispatch(createAccount(code)).unwrap();
            navigate(`/dashboard/accounts?currency=${account.code}`);
        onClose();
        } catch {
        // The slice supplies the visible error.
    }
}
return (
    <SectionContainer title="Create a currency account"
        action={<button className="link" disabled={create.status === 'pending'}
        onClick={onClose}>Close</button>}>
        {available.length === 0 ? <p>You already have all supported currencies.</p> : (
    <form onSubmit={submit} className="flex max-w-lg flex-col gap-4">
    <label htmlFor="new-currency">Currency</label>
    <select id="new-currency" className="field" value={code}
        disabled={create.status === 'pending'}
        onChange={event => setChoice(event.target.value)}>
        {available.map(item => (
        <option key={item.code} value={item.code}>
            {item.code} - {item.label}
        </option>
    ))}
    </select>
    <p className="text-sm text-slate-600">
        This learning app gives new accounts fictional demo funds.
    </p>
    {create.error && <p role="alert" className="text-red-700">{create.error}</p>}
    <button className="primary" disabled={create.status === 'pending'}>
        {create.status === 'pending' ? 'Creating...' : 'Create bank account'}
    </button>
    </form>
    )}
    </SectionContainer>
);
}