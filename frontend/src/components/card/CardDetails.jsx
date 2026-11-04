import { useSelector } from 'react-redux';
import { selectCard } from '../../features/cardSlice.js';
import TextComponent from '../TextComponent.jsx';
import { money } from '../../helper/money.js';

export default function CardDetails() {
    const userCard = useSelector(selectCard);
    if (!userCard) return null;
    return (
        <div className="flex flex-wrap">
            <TextComponent textProp={{ tag: 'Card holder', value: userCard.cardHolder }} />
            <TextComponent textProp={{ tag: 'Card number', value: `**** ${userCard.lastFour}` }} />
            <TextComponent textProp={{ tag: 'Balance', value: money(userCard.balance, 'USD') }} />
            <TextComponent textProp={{ tag: 'Issued on', value: userCard.issuedOn }} />
            <TextComponent textProp={{ tag: 'Expires at', value: userCard.expiresAt?.slice(0, 10) }} />
        </div>
);
}