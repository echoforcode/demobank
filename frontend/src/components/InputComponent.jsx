export default function InputComponent({
label, name, value, onChange, type = 'text',
error, required = true, ...rest
}) 
{
return (
    <div className="flex flex-col gap-1.5">
        <label htmlFor={name} className="text-sm font-medium">{label}</label>
        <input id={name} name={name} type={type}
            value={value} onChange={onChange} required={required}
            className="field" aria-invalid={Boolean(error)}
            aria-describedby={error ? `${name}-error` : undefined}
            {...rest} />
        {error && (
        <p id={`${name}-error`} className="text-sm text-red-700">
            {error}
        </p>
        )}  
    </div>
);
}