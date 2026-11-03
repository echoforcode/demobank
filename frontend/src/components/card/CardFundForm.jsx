import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { creditCard } from '../../features/cardSlice.js';
import { fetchAccounts } from '../../features/accountTasks.js';
import { openSpinner, closeSpinner } from '../../features/pageSlice.js';
import { amountValue, money } from '../../helper/money.js';
import InputComponent from '../InputComponent.jsx';
import SectionContainer from '../SectionContainer.jsx';

export default function CardFundForm({ userCard, onClose }) {
    const dispatch = useDispatch();
    const usd = useSelector(state => state.accounts.items
        .find(item => item.code === 'USD'));
    const busy = useSelector(state => state.cards.status === 'pending');
    const [amount, setAmount] = useState('');
    const [done, setDone] = useState(false);
    const parsed = amountValue(amount, 'USD');
    const available = usd?.balance ?? 0;
    const valid = parsed && parsed.lte(available) && !busy && !done;
    async function submit(event) {
        event.preventDefault();
        if (!valid) return;
        dispatch(openSpinner());
        try {
            await dispatch(creditCard(amount)).unwrap();
            setDone(true);
            setAmount('');
            await dispatch(fetchAccounts());
        } catch {
        // A rejected POST is shown by Card.jsx; do not report success.
        } finally {
            dispatch(closeSpinner());
}
}
return (
    <SectionContainer title="Fund your card"
        action={<button className="link" disabled={busy} onClick={onClose}>Close</button>}>
        <p className="mb-4">Available: {money(available, 'USD')}</p>
        {done ? <p role="status">Completed. See the server receipt above.</p> : (
        <form onSubmit={submit} className="flex max-w-lg flex-col gap-4">
            <InputComponent label="Amount (USD)" name="cardFundAmount"
                value={amount} onChange={event => setAmount(event.target.value)}
                inputMode="decimal" disabled={busy} />
            <button className="primary" disabled={!valid}>Fund card</button>
        </form>
        )}
    </SectionContainer>
);
}