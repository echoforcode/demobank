export default function TextComponent({ textProp }) {
return (
    <div className="min-w-[180px] flex-1 p-3">
        <p className="text-sm text-slate-500">{textProp.tag}</p>
        <p className="mt-1 break-all font-medium">{textProp.value ?? '-'}</p>
    </div>
);
}