import { Link } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { fetchAccounts } from '../../features/accountTasks.js';
import SectionContainer from '../../components/SectionContainer.jsx';
import AccountTile from '../../components/account/AccountTile.jsx';
import PaymentActions from '../../components/dashboard/PaymentActions.jsx';
import HistoryPanel from '../../components/transactions/HistoryPanel.jsx';

export default function Home() {
  const { items, load } = useSelector(state => state.accounts);
  const dispatch = useDispatch();
  return (
    <div className="space-y-6">
      <SectionContainer title="Your currency accounts"
        action={<Link className="link" to="/dashboard/accounts">Manage</Link>}>
        {load.status === 'pending' && <p role="status">Loading accounts...</p>}
        {load.error && <div role="alert" className="mb-4 text-red-700">
          {load.error}{' '}
          <button className="link" onClick={() => dispatch(fetchAccounts())}>
            Retry
          </button>
        </div>}
        {load.status === 'success' && items.length === 0 && (
          <p>No accounts yet. <Link className="link"
            to="/dashboard/accounts">Create your first account</Link>.</p>
        )}
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {items.map(account => (
            <AccountTile key={account.accountId} account={account} />
          ))}
        </div>
      </SectionContainer>
      <SectionContainer title="Quick actions"><PaymentActions /></SectionContainer>
      <HistoryPanel title="Recent transactions" compact />
    </div>
  );
}
