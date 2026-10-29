import { useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { fetchAccounts } from '../../features/accountTasks.js';
import SectionContainer from '../../components/SectionContainer.jsx';
import { money } from '../../helper/money.js';
import NewAccount from '../../components/account/NewAccount.jsx';

export default function Accounts() {
    const { items, load } = useSelector(state => state.accounts);
    const [params, setParams] = useSearchParams();
    const [showCreate, setShowCreate] = useState(false);
    const dispatch = useDispatch();
    const requested = params.get('currency');
    const account = items.find(item => item.code === requested) || items[0];
return (
    <div className="space-y-6">
        <SectionContainer title={`Accounts (${items.length})`}
            action={<button className="link" onClick={() =>
            setShowCreate(value => !value)}>
            {showCreate ? 'Close creation form' : 'Create new account'}
        </button>}>
        {load.status === 'pending' && <p role="status">Loading accounts...</p>}
        {load.error && <p role="alert" className="text-red-700">{load.error}</p>}
    <div className="mb-5 flex flex-wrap gap-2">
        {items.map(item => (
        <button key={item.accountId} type="button"
            aria-pressed={item.code === account?.code}
            className={`rounded-lg border px-4 py-2 ${item.code === account?.code
            ? 'bg-blue-700 text-white' : 'bg-white'}`}
            onClick={() => setParams({ currency: item.code })}>
        {item.code}
        </button>
    ))}
    </div>
        {requested && !items.some(item => item.code === requested) && (
        <p className="mb-3 text-sm">That currency is unavailable; showing a fallback.</p>
        )}
        {account ? (
    <div className="space-y-3">
        <h3 className="text-lg font-semibold">{account.label}</h3>
        <p>Account number: {account.accountNumber}</p>
        <p className="text-3xl font-bold">
            {money(account.balance, account.code)}
        </p>
    </div>
            ) : load.status === 'success' && <p>Create your first currency account.</p>}
        <button className="link mt-5" disabled={load.status === 'pending'}
            onClick={() => dispatch(fetchAccounts())}>Refresh accounts</button>
        </SectionContainer>
        {showCreate && <NewAccount onClose={() => setShowCreate(false)} />}
    </div>
);
}