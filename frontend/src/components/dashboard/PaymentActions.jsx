import { Link } from 'react-router-dom';
export default function PaymentActions() {
return (
    <div className="grid gap-4 sm:grid-cols-3">
        <Link className="rounded-xl border border-blue-200 p-5 hover:bg-blue-50"
            to="/dashboard/accounts?transfer=1">
        <h3 className="font-semibold">Transfer funds</h3>
        <p className="mt-2 text-sm text-slate-600">Send to a DemoBank account.</p>
        </Link>
        <Link className="rounded-xl border border-blue-200 p-5 hover:bg-blue-50"
        to="/dashboard/convert">
        <h3 className="font-semibold">Convert currency</h3>
        <p className="mt-2 text-sm text-slate-600">Move between your accounts.</p>
        </Link>
        <div className="rounded-xl border border-slate-200 p-5 text-slate-500">
        <h3 className="font-semibold">Bill payments</h3>
        <p className="mt-2 text-sm">Preview only; no provider integration.</p>
        </div>
    </div>
);
}