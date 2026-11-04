import { money } from '../../helper/money.js';

const THItem = ({ children }) => (
    <th scope="col" className="px-3 py-3 text-left font-semibold">{children}</th>
);
const TDItem = ({ children }) => (
    <td className="border-t border-slate-200 px-3 py-3">{children}</td>
);
function TransactionItem({ transaction: tx }) {
return (
    <tr>
        <TDItem>{tx.type}</TDItem>
        <TDItem>{money(tx.amount, tx.currency || '')}</TDItem>
        <TDItem>{money(tx.txFee, tx.currency || '')}</TDItem>
        <TDItem>{tx.status}</TDItem>
        <TDItem>{tx.createdAt?.replace('T', ' ').slice(0, 19) || '-'}</TDItem>
    </tr>
);
}
export default function TransactionTable({ items }) {
if (items.length === 0) return <p>No transactions on this page.</p>;
return (
    <div className="overflow-x-auto">
        <table className="w-full min-w-[640px] text-sm">
            <thead className="bg-slate-50"><tr>
            <THItem>Type</THItem><THItem>Amount</THItem><THItem>Fee</THItem>
            <THItem>Status</THItem><THItem>Created at</THItem>
            </tr></thead>
            <tbody>{items.map(tx => (
            <TransactionItem key={tx.txId} transaction={tx} />
        ))}</tbody>
        </table>
    </div>
);
}