import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
fetchTransactions, historyKey, emptyHistory,
} from '../../features/transactionsSlice.js';
import SectionContainer from '../SectionContainer.jsx';
import TransactionTable from './TransactionTable.jsx';

export default function HistoryPanel({
    scope = 'all', id = '', compact = false, title = 'Transactions',
    }) {
    const dispatch = useDispatch();
    const [page, setPage] = useState(0);
    const [retry, setRetry] = useState(0);
    const key = historyKey({ scope, id });
    const view = useSelector(state => state.transactions.views[key] || emptyHistory);
    const cardRevision = useSelector(state => state.cards.revision);
    const transferId = useSelector(state =>
    state.accounts.transfer.receipt?.transactionId);
    const conversionId = useSelector(state =>
    state.accounts.conversion.receipt?.sourceTransactionId);
    useEffect(() => {
        const task = dispatch(fetchTransactions({ scope, id, page }));
        return () => task.abort();
        }, [dispatch, scope, id, page, retry, cardRevision, transferId, conversionId]);
    const pending = view.status === 'idle' || view.status === 'pending';
    return (
        <SectionContainer title={title} action={
        <button className="link" disabled={pending}
            onClick={() => setRetry(value => value + 1)}>Refresh history</button>
            }>
        {pending ? <p role="status">Loading transactions...</p>
        : view.error ? <p role="alert" className="text-red-700">
        {view.error}{' '}
        <button className="link" onClick={() => setRetry(value => value + 1)}>
        Retry
        </button>
        </p> : <TransactionTable items={compact ? view.items.slice(0, 5) : view.items} />}
            {!compact && <div className="mt-5 flex items-center justify-between gap-3">
        <button className="link" disabled={pending || page === 0}
            onClick={() => setPage(value => value - 1)}>Previous</button>
        <p className="text-sm">
            Page {page + 1} of {Math.max(1, view.totalPages)}
        </p>
        <button className="link"
            disabled={pending || Boolean(view.error) || page + 1 >= view.totalPages}
            onClick={() => setPage(value => value + 1)}>Next</button>
        </div>}
    </SectionContainer>
);
}