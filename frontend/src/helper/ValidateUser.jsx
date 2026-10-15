export function ValidateUser(user) {
    const errors = {};
    for (const field of ['firstname', 'lastname', 'username']) {
        if (!user[field].trim()) errors[field] = 'This field is required.';
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(user.username.trim())) {
        errors.username = 'Enter an email address.';
    }
    const bytes = new TextEncoder().encode(user.password).length;
    if (user.password.length < 8 || bytes > 72) {
        errors.password = 'Use at least 8 characters and at most 72 UTF-8 bytes.';
    }
    if (user.password !== user.confirmPassword) {
        errors.confirmPassword = 'Passwords do not match.';
    }
    if (user.tel && !/^\d{1,15}$/.test(user.tel)) {
        errors.tel = 'Use 1-15 digits for this backend.';
    }
    if (user.dob) {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const date = new Date(`${user.dob}T00:00:00`);
    
        if (Number.isNaN(date.getTime()) || date >= today) {
            errors.dob = 'Date of birth must be before today.';
        }
    }
    return errors;
}
export function registrationPayload(user) {
    const { confirmPassword, ...body } = user;
    return {
        ...body,
        firstname: body.firstname.trim(),
        lastname: body.lastname.trim(),
        username: body.username.trim(),
        tel: body.tel ? Number(body.tel) : 0,
        dob: body.dob || null,
};
}