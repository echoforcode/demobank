export default function AuthLayout({ title, children, footer }) 
{
return (
<main className="min-h-screen bg-gradient-to-br from-slate-200
to-white px-4 py-10 text-slate-900">
<section className="mx-auto w-full max-w-xl">
<p className="mb-7 text-center font-bold text-blue-700">
DEMOBANK
</p>
<h1 className="mb-7 text-center text-3xl font-bold">{title}</h1>
<div className="rounded-2xl bg-white p-6 shadow-sm sm:p-8">
{children}
</div>
<div className="mt-6 text-center text-sm">{footer}</div>
</section>
</main>
);
}