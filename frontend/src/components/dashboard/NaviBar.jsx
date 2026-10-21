import { NavLink } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { closeNav } from '../../features/pageSlice.js';

const links = [
    ['', 'Home'], ['accounts', 'Accounts'], ['payments', 'Payments'],
    ['convert', 'Convert'], ['transactions', 'Transactions'],
    ['cards', 'Cards'], ['profile', 'Profile'], ['settings', 'Settings'],
];
export default function NavBar() {
    const dispatch = useDispatch();
    const showNav = useSelector(state => state.pages.showNav);
return (
    <nav id="dashboard-nav" aria-label="Dashboard"
        className={`${showNav ? 'block' : 'hidden'} w-full shrink-0
        border-r border-slate-200 bg-white p-4 lg:block lg:w-56`}>
            <ul className="space-y-2">
            {links.map(([path, label]) => (
            <li key={label}>
            <NavLink to={`/dashboard${path ? '/' + path : ''}`}
                end={!path} onClick={() => dispatch(closeNav())}
                className={({ isActive }) =>
                `block rounded-lg px-4 py-3 ${isActive
                ? 'bg-blue-700 font-semibold text-white'
                : 'text-slate-700 hover:bg-slate-100'}`}>
                {label}
            </NavLink>
            </li>
    ))}
            </ul>
    </nav>
);
}