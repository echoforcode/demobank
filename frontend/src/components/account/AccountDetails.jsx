import TextComponent from '../TextComponent.jsx';
import { money } from '../../helper/money.js';

export default function AccountDetails({ account }) {
return (
    <div className="flex flex-wrap">
        <TextComponent textProp={{ tag: 'Account number', value: account.accountNumber }} />
        <TextComponent textProp={{ tag: 'Currency', value: account.code }} />
        <TextComponent textProp={{ tag: 'Account label', value: account.label }} />
        <TextComponent textProp={{ tag: 'Balance', value: money(account.balance, account.code) }} />
    </div>
);
}