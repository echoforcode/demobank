import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { findRecipient, transferFunds, fetchAccounts }
from '../../features/accountTasks.js';
import { resetRecipient } from '../../features/accountsSlice.js';
import { openSpinner, closeSpinner } from '../../features/pageSlice.js';
import { amountValue, charge, money } from '../../helper/money.js';
import SectionContainer from '../SectionContainer.jsx';
import InputComponent from '../InputComponent.jsx';

export default function TransferForm({ account, onClose }) {
    const dispatch = useDispatch();
    const { recipient, transfer } = useSelector(state => state.accounts);
    const [number, setNumber] = useState('');
    const [amount, setAmount] = useState('');
    const [receipt, setReceipt] = useState(null);
    const [refreshWarning, setRefreshWarning] = useState('');
    const code = account.code;
    useEffect(() => {
    dispatch(resetRecipient());
    if (!/^[1-9]\d{9}$/.test(number)) return;
    let task;
    const timer = setTimeout(() => {
        task = dispatch(findRecipient({ accountNumber: Number(number), code }));
        }, 300);
        return () => {
        clearTimeout(timer);
        task?.abort();
    };
    }, [dispatch, number, code]);

    const found = recipient.status === 'success'
        && String(recipient.value?.accountNumber) === number
        && recipient.value?.code === code;
    const parsed = amountValue(amount, code);
    const cost = parsed ? charge(parsed, code) : null;
    const busy = transfer.status === 'pending';
    const canSend = found && cost && cost.total.lte(account.balance)
        && number !== String(account.accountNumber) && !busy && !receipt;
    async function submit(event) {
        event.preventDefault();
        if (!canSend) return;
        dispatch(openSpinner());
        try {
            const result = await dispatch(transferFunds({
                code, recipientAccountNumber: Number(number), amount,
                })).unwrap();
                setReceipt(result);
                setAmount('');
            const refresh = await dispatch(fetchAccounts());
            if (fetchAccounts.rejected.match(refresh)) {
        setRefreshWarning('Transfer succeeded. Refresh accounts to update balances.');
        }
        } catch {
        } finally {
        dispatch(closeSpinner());
// Only a failed transfer reaches here; the slice displays its error.
        }
}
return (
    <SectionContainer title={`Transfer from ${code}`}
        action={<button className="link" disabled={busy} onClick={onClose}>
        Close
        </button>}>
            <p className="mb-4">Available: {money(account.balance, code)}</p>
                {receipt ? (
        <div role="status" className="space-y-2">
            <p className="font-semibold text-green-800">Transfer completed.</p>
            <p>Transaction: {receipt.transactionId}</p>
            <p>Sent: {money(receipt.amount, code)}</p>
            <p>Fee: {money(receipt.fee, code)}</p>
            <p>New balance: {money(receipt.senderBalance, code)}</p>
            {refreshWarning && <p>{refreshWarning}</p>}
            <button className="link" onClick={onClose}>Done</button>
        </div>
    ) : (
    <form onSubmit={submit} className="flex max-w-lg flex-col gap-4">
    <InputComponent label="Recipient account number" name="recipientNumber"
        value={number} onChange={event => setNumber(event.target.value)}
        inputMode="numeric" maxLength={10} disabled={busy} />
        {recipient.status === 'pending' && <p role="status">Finding recipient...</p>}
        {found && <p>Recipient: {recipient.value.displayName}</p>}
        {recipient.error && <p role="alert" className="text-red-700">
        {recipient.error}
        </p>}
    <InputComponent label={`Amount (${code})`} name="transferAmount"
        value={amount} onChange={event => setAmount(event.target.value)}
        inputMode="decimal" disabled={!found || busy} />
        <p className="text-sm text-slate-600">
        {code === 'JPY' ? 'Use whole yen.' : 'Use up to two decimal places.'}
        {' '}A 1% fee is added to the amount.
        </p>
            {cost && <p>Estimated fee: {money(cost.fee, code)};
            {' '}total debit: {money(cost.total, code)}</p>}
            {cost && cost.total.gt(account.balance) && (
        <p className="text-red-700">Insufficient funds including the fee.</p>
)}
            {transfer.error && <p role="alert" className="text-red-700">
            {transfer.error} If the outcome is uncertain, check balances first.
        </p>}
        <button className="primary" disabled={!canSend}>
            {busy ? 'Sending...' : 'Send transfer'}
        </button>
        </form>
)}
    </SectionContainer>
);
}