import { useEffect, useMemo, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
getExchangeRate, convertCurrency, fetchAccounts,
} from '../../features/accountTasks.js';
import { openSpinner, closeSpinner } from '../../features/pageSlice.js';
import {
amountValue, charge, conversionPreview, money,
} from '../../helper/money.js';
import SectionContainer from '../../components/SectionContainer.jsx';
import InputComponent from '../../components/InputComponent.jsx';

export default function Convert() {
    const dispatch = useDispatch();
    const { items, rates, conversion } = useSelector(state => state.accounts);
    const [pair, setPair] = useState({ from: '', to: '' });
    const [amount, setAmount] = useState('');
    const [receipt, setReceipt] = useState(null);
    const [refreshWarning, setRefreshWarning] = useState('');
    const from = items.some(item => item.code === pair.from)
        ? pair.from : items[0]?.code || '';
    const to = items.some(item => item.code === pair.to)
        ? pair.to : items[1]?.code || items[0]?.code || '';
    const source = items.find(item => item.code === from);
    useEffect(() => { dispatch(getExchangeRate()); }, [dispatch]);
    const parsed = amountValue(amount, from);
    const cost = parsed ? charge(parsed, from) : null;
    const preview = useMemo(() => conversionPreview(
    amountValue(amount, from), from, to, rates.snapshot?.rates,
        ), [amount, from, to, rates.snapshot]);
    const busy = conversion.status === 'pending';
    const canConvert = source && from !== to && cost && preview?.gt(0)
        && cost.total.lte(source.balance) && rates.status === 'success'
        && !busy && !receipt;
    async function submit(event) {
        event.preventDefault();
        if (!canConvert) return;
            dispatch(openSpinner());
        try {
            const result = await dispatch(convertCurrency({
            fromCurrency: from, toCurrency: to, amount,
            })).unwrap();
        setReceipt(result);
        setAmount('');
        const refresh = await dispatch(fetchAccounts());
        if (fetchAccounts.rejected.match(refresh)) {
            setRefreshWarning('Conversion succeeded. Refresh accounts for new balances.');
        }
        } catch {
        // The conversion slot supplies the error.
        } finally {
            dispatch(closeSpinner());
}
}
return (
<SectionContainer title="Convert currency">
    {items.length < 2 ? <p>Create at least two currency accounts first.</p> : (
        <>
    <button type="button" className="link mb-4"
        disabled={rates.status === 'pending' || busy}
        onClick={() => dispatch(getExchangeRate())}>
        {rates.status === 'pending' ? 'Loading rates...' : 'Refresh rates'}
    </button>
        {rates.error && <p role="alert" className="mb-4 text-red-700">
            {rates.error}
        </p>}
        {rates.snapshot && <p className="mb-5 text-sm text-slate-600">
            Provider timestamp: {rates.snapshot.asOf}
        </p>}
    <form onSubmit={submit} className="flex max-w-xl flex-col gap-4">
    <div className="grid gap-4 sm:grid-cols-2">
        {['from', 'to'].map(field => (
        <label key={field} className="flex flex-col gap-2">
        {field === 'from' ? 'From currency' : 'To currency'}
        <select className="field" name={field}
            value={field === 'from' ? from : to}
            disabled={busy || Boolean(receipt)}
            onChange={event => setPair({
            from, to, [field]: event.target.value,
        })}>
            {items.map(item => (
            <option key={item.accountId} value={item.code}>
                {item.code}
            </option>
        ))}
        </select>
        </label>
))}
    </div>
    <button className="link self-start" type="button"
        disabled={busy || Boolean(receipt)}
        onClick={() => { setPair({ from: to, to: from }); setAmount(''); }}>
        Swap currencies
    </button>
    <p>Available: {money(source?.balance, from)}</p>
    <InputComponent label={`Amount (${from})`} name="convertAmount"
        value={amount} onChange={event => setAmount(event.target.value)}
        inputMode="decimal" disabled={busy || Boolean(receipt)} />
        {from === 'JPY' && <p>Use whole yen.</p>}
        {preview && cost && <div className="rounded-lg bg-blue-50 p-4">
        <p>Estimated receive: {money(preview, to)}</p>
        <p>Fee: {money(cost.fee, from)}</p>
        <p>Total debit: {money(cost.total, from)}</p>
        <p className="text-sm">Final values come from the server receipt.</p>
    </div>}
        {cost && source && cost.total.gt(source.balance) && (
        <p className="text-red-700">Insufficient funds including the fee.</p>
            )}
            {conversion.error && <p role="alert" className="text-red-700">
            {conversion.error} Check balances before retrying an uncertain POST.
        </p>}
        <button className="primary" disabled={!canConvert}>
            {busy ? 'Converting...' : 'Convert'}
        </button>
        </form>
            {receipt && <div role="status" className="mt-6 space-y-2">
            <p className="font-semibold text-green-800">Conversion completed.</p>
            <p>Received: {money(receipt.convertedAmount, receipt.toCurrency)}</p>
            <p>Fee: {money(receipt.fee, receipt.fromCurrency)}</p>
            <p>Source balance: {money(receipt.sourceBalance, receipt.fromCurrency)}</p>
            <p>Destination: {money(receipt.destinationBalance, receipt.toCurrency)}</p>
                {refreshWarning && <p>{refreshWarning}</p>}
        <button className="link" onClick={() => setReceipt(null)}>
            New conversion
        </button>
    </div>}
</>
)}
</SectionContainer>
);
}