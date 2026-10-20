export const currencies = [
    { code: 'USD', label: 'United States Dollar', badge: 'US' },
    { code: 'EUR', label: 'Euro', badge: 'EU' },
    { code: 'GBP', label: 'British Pound', badge: 'GB' },
    { code: 'JPY', label: 'Japanese Yen', badge: 'JP' },
    { code: 'NGN', label: 'Nigerian Naira', badge: 'NG' },
    { code: 'INR', label: 'Indian Rupee', badge: 'IN' },
];
export function currencyInfo(code) {
    return currencies.find(item => item.code === code)
        || { code, label: code, badge: code };
}