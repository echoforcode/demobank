import { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchCard } from '../../features/cardSlice.js';
import { fetchAccounts } from '../../features/accountTasks.js';
import { money } from '../../helper/money.js';
import SectionContainer from '../../components/SectionContainer.jsx';
import CreateCard from '../../components/card/CreateCard.jsx';
import CardImage from '../../components/card/CardImage.jsx';
import CardFundForm from '../../components/card/CardFundForm.jsx';
import CardWithdrawForm from '../../components/card/CardWithdrawForm.jsx';
import HistoryPanel from '../../components/transactions/HistoryPanel.jsx';

export default function Card() {
    const dispatch = useDispatch();
    const { card, status, error, receipt } = useSelector(state => state.cards);
    const load = useSelector(state => state.accounts.load);
    const [showFundCardForm, setShowFundCardForm] = useState(false);
    const [showWithdrawForm, setShowWithdrawForm] = useState(false);
    if (status === 'idle' || (status === 'pending' && !card)) {
        return <p role="status">Loading card...</p>;
    }
    return (
    <div className="space-y-6">
        {error && <p role="alert" className="text-red-700">{error}</p>}
        {load.error && <p role="alert" className="text-red-700">
        Account refresh failed: {load.error}{' '}
        <button className="link" onClick={() => dispatch(fetchAccounts())}>
            Refresh accounts
        </button>
        </p>}
            {receipt && <div role="status" className="rounded-xl bg-green-50 p-4">
        <p>{receipt.operation} completed.</p>
        <p>Amount: {money(receipt.amount, 'USD')}; fee: {money(receipt.fee, 'USD')}</p>
        <p>USD account: {money(receipt.usdBalance, 'USD')}</p>
        <p>Card: {money(receipt.card.balance, 'USD')}</p>
        </div>}
        <SectionContainer title="Your USD card"
            action={<button className="link" disabled={status === 'pending'}
            onClick={() => dispatch(fetchCard())}>Refresh card</button>}>
            {card ? <CardImage userCard={card}
            setShowFundCardForm={setShowFundCardForm}
            setShowWithdrawForm={setShowWithdrawForm} />
                : status === 'success' && (
            load.status === 'success' ? <CreateCard />
            : <p>Load your accounts before creating a card.</p>
            )}
        </SectionContainer>
            {card && showFundCardForm && <CardFundForm userCard={card}
                onClose={() => setShowFundCardForm(false)} />}
            {card && showWithdrawForm && <CardWithdrawForm userCard={card}
                onClose={() => setShowWithdrawForm(false)} />}
            {card && <HistoryPanel key={card.cardId} scope="card" id={card.cardId}
                title="Card transactions" />}
    </div>
);
}