export default function Spinner() {
return (
    <div role="status" aria-live="polite"
        className="fixed inset-0 z-50 flex items-center justify-center
        bg-white/80">
        <div className="flex items-center gap-3 rounded-xl bg-white p-6 shadow">
            <span aria-hidden="true"
                className="h-7 w-7 animate-spin rounded-full border-4
                    border-blue-100 border-t-blue-700" />
            <span>Working...</span>
        </div>
    </div>
);
}