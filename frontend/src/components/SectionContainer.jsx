export default function SectionContainer({ title, children, action }) {
return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6">
        <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
            <h2 className="text-xl font-semibold">{title}</h2>
            {action}
        </div>
        {children}
    </section>
);
}