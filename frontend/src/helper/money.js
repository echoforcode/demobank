import Decimal from 'decimal.js';

export const scaleFor = code => code === 'JPY' ? 0 : 2;
export function money(value, code) {
    return `${code} ${new Decimal(value ?? 0).toFixed(scaleFor(code))}`;
}
export function amountValue(text, code) {
    const pattern = /^\d{1,15}(\.\d{1,2})?$/;
    if (!pattern.test(text)) return null;
    const amount = new Decimal(text);
    if (!amount.isPositive() || amount.isZero()) return null;
    if (code === 'JPY' && !amount.isInteger()) return null;
    return amount;
}

export function charge(amount, code) {
    const fee = amount.mul('0.01')
        .toDecimalPlaces(scaleFor(code), Decimal.ROUND_HALF_UP);
    return { fee, total: amount.plus(fee) };
}

export function conversionPreview(amount, from, to, rates) {
    if (!amount || from === to) return null;
    const source = rates?.[from];
    const destination = rates?.[to];
    if (!(Number(source) > 0 && Number(destination) > 0)) return null;
    return amount.mul(destination).div(source)
        .toDecimalPlaces(scaleFor(to), Decimal.ROUND_HALF_UP);
}