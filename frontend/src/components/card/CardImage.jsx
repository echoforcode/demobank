import { FaPlus, FaExchangeAlt } from 'react-icons/fa';
import creditCardImage from '../../img/credit_card.png';
import { money } from '../../helper/money.js';

export default function CardImage({
    userCard, setShowFundCardForm, setShowWithdrawForm,
}) {
if (!userCard) return null;
return (
    <div>
        <div className="relative mx-auto max-w-[400px] text-white">
            <img src={creditCardImage} alt="Demo card background"
            className="w-full rounded-xl" />
            <div aria-hidden="true" className="absolute right-6 top-6 flex">
                <span className="h-7 w-7 rounded-full bg-red-500" />
                <span className="-ml-3 h-7 w-7 rounded-full bg-amber-400 opacity-80" />
            </div>
            <p className="absolute left-6 top-6 text-sm">DemoBank USD card</p>
            <p className="absolute bottom-16 left-6">**** {userCard.lastFour}</p>
        <div className="absolute bottom-6 left-6 right-6 flex justify-between gap-3">
            <p>{money(userCard.balance, 'USD')}</p>
            <p>{userCard.cardHolder}</p>
        </div>
    </div>
    <div className="mt-5 flex justify-center gap-6">
        <button className="link flex items-center gap-2"
            onClick={() => { setShowWithdrawForm(false); setShowFundCardForm(true); }}>
            <FaPlus aria-hidden="true" /> Fund
        </button>
        <button className="link flex items-center gap-2"
            onClick={() => { setShowFundCardForm(false); setShowWithdrawForm(true); }}>
            <FaExchangeAlt aria-hidden="true" /> Withdraw
        </button>
    </div>
    </div>
);
}