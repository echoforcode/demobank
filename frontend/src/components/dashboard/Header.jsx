import { useDispatch, useSelector } from 'react-redux';
import { toggleNav } from '../../features/pageSlice.js';
import { logout } from '../../features/usersSlice.js';
export default function Header() {
    const dispatch = useDispatch();
    const user = useSelector(state => state.user.user);
    const showNav = useSelector(state => state.pages.showNav);
return (
    <header className="flex items-center justify-between gap-4 border-b
        border-slate-200 bg-white px-5 py-4">
        <div className="flex items-center gap-4">
            <button type="button" className="link lg:hidden"
                aria-controls="dashboard-nav" aria-expanded={showNav}
                onClick={() => dispatch(toggleNav())}>
                {showNav ? 'Close menu' : 'Menu'}
            </button>
            <div>
                <p className="font-bold text-blue-800">DemoBank</p>
                <p className="text-sm text-slate-600">
                    Welcome, {user.firstname || user.username}
                </p>
            </div>
        </div>
        <button type="button" className="link"
        onClick={() => dispatch(logout())}>Log out</button>
    </header>
);
}