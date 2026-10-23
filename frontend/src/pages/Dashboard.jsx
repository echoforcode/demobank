import { Outlet } from 'react-router-dom';
import { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import { fetchAccounts } from '../features/accountTasks.js';
import Header from '../components/dashboard/Header.jsx';
import NavBar from '../components/dashboard/NavBar.jsx';

export default function Dashboard() {
const dispatch = useDispatch();
useEffect(() => {
    dispatch(fetchAccounts());
    }, [dispatch]);
return (
    <div className="min-h-screen bg-slate-100 text-slate-900">
        <Header />
            <div className="flex min-h-[calc(100vh-81px)] flex-col lg:flex-row">
                <NavBar />
                <main className="min-w-0 flex-1 p-4 sm:p-6 lg:p-8">
                <Outlet />
                </main>
            </div>
        </div>
);
}