import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { createCard } from '../../features/cardSlice.js';
import { fetchAccounts } from '../../features/accountTasks.js';
import { openSpinner, closeSpinner } from '../../features/pageSlice.js';
import { amountValue, money } from '../../helper/money.js';
import InputComponent from '../InputComponent.jsx';

export default function CreateCard() {
    const dispatch = useDispatch();
    const usd = useSelector(state => state.accounts.items
        .find(item => item.code === 'USD'));
    const busy = useSelector(state => state.cards.status === 'pending');
    const [amount, setAmount] = useState('');
    const parsed = amountValue(amount, 'USD');
    const valid = usd && parsed?.gte(2) && parsed.lte(usd.balance) && !busy;
    async function submit(event) {
        event.preventDefault();
        if (!valid) return;
        dispatch(openSpinner());
        try {
            await dispatch(createCard(amount)).unwrap();
            await dispatch(fetchAccounts());
        } catch {
        // The card slice stores the failed POST message.
        } finally {
        dispatch(closeSpinner());
}
}
if (!usd) return (
    <p>You need a USD account before creating a card.{' '}
        <Link className="link" to="/dashboard/accounts">Create USD account</Link>
    </p>
);
return (
    <form onSubmit={submit} className="flex max-w-lg flex-col gap-4">
        <h3 className="text-lg font-semibold">Create your USD card</h3>
        <p>Available: {money(usd.balance, 'USD')}</p>
        <p>Minimum USD 2.00. A USD 1.00 issue fee is included in this amount.</p>
        <InputComponent label="Creation amount (USD)" name="cardCreateAmount"
            value={amount} onChange={event => setAmount(event.target.value)}
            inputMode="decimal" disabled={busy} />
        <button className="primary" disabled={!valid}>Create card</button>
    </form>
);
}