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
}