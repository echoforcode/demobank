import { Link } from 'react-router-dom';
import { currencyInfo } from '../../helper/currencies.js';
import { money } from '../../helper/money.js';

export default function AccountTile({ account }) {
const info = currencyInfo(account.code);
return (
    <Link to={`/dashboard/accounts?currency=${account.code}`}
        className="rounded-xl border border-slate-200 bg-slate-50 p-5
            transition hover:border-blue-600 hover:bg-blue-50">
        <span className="inline-block rounded bg-blue-100 px-2 py-1
            text-xs font-bold text-blue-800">{info.badge}</span>
        <p className="mt-3 text-sm text-slate-600">{account.label}</p>
        <p className="mt-1 text-xl font-bold">
        {money(account.balance, account.code)}
        </p>
        <p className="mt-3 text-xs text-slate-500">
        Account {account.accountNumber}
        </p>
    </Link>
);
}