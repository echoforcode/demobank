
import {
  Alert,
  Box,
  Button,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { ThemeProvider } from '@mui/material/styles';
import { Link } from 'react-router-dom';
import fdomine from '../theme/fdomine';

//use mui to change the register view to match the login view, with a left side for branding and a right side for the form
const fields = [
  {
    name: 'firstname',
    label: 'First name',
    type: 'text',
    required: true,
    half: true,
    autoComplete: 'given-name',
  },
  {
    name: 'lastname',
    label: 'Last name',
    type: 'text',
    required: true,
    half: true,
    autoComplete: 'family-name',
  },
  {
    name: 'username',
    label: 'Email address',
    type: 'email',
    required: true,
    autoComplete: 'email',
  },
  {
    name: 'tel',
    label: 'Phone number (optional)',
    type: 'tel',
    half: true,
    autoComplete: 'tel',
  },
  {
    name: 'dob',
    label: 'Date of birth (optional)',
    type: 'date',
    half: true,
  },
  {
    name: 'password',
    label: 'Password',
    type: 'password',
    required: true,
    autoComplete: 'new-password',
  },
  {
    name: 'confirmPassword',
    label: 'Confirm password',
    type: 'password',
    required: true,
    autoComplete: 'new-password',
  },
];

export default function RegisterView({
  form,
  onChange,
  onSubmit,
  errors,
  status,
  error,
}) {
  return (
    <ThemeProvider theme={fdomine}>
      <Box
        component="main"
        sx={{
          minHeight: '100vh',
          display: 'grid',
          gridTemplateColumns: {
            xs: '1fr',
            md: '1fr 1fr',
          },
          bgcolor: '#f5f7fb',
        }}
      >
        {/* Left: same branding as LoginView */}
        <Box
          sx={{
            display: { xs: 'none', md: 'flex' },
            flexDirection: 'column',
            justifyContent: 'center',
            px: { md: 6, lg: 10 },
            color: 'white',
            background:
              'linear-gradient(135deg, #071B36, #145A91)',
          }}
        >
          <Typography variant="overline" sx={{ letterSpacing: 3 }}>
            DEMOBANK
          </Typography>

          <Typography variant="h3" sx={{ fontWeight: 700, mt: 3 }}>
            Your banking journey starts here.
          </Typography>

          <Typography variant="body1" sx={{ mt: 2, opacity: 0.85 }}>
            Create your account to manage funds,
            track transactions, and explore DemoBank.
          </Typography>
        </Box>

        {/* Right: registration form */}
        <Box
          sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            p: { xs: 2, sm: 4 },
          }}
        >
          <Paper
            elevation={0}
            sx={{
              width: '100%',
              maxWidth: 560,
              p: { xs: 3, sm: 5 },
              border: '1px solid #e2e8f0',
            }}
          >
            <Typography variant="h4" component="h1" fontWeight={700}>
              Create your account
            </Typography>

            <Typography color="text.secondary" sx={{ mt: 1 }}>
              Join DemoBank to get started
            </Typography>

            <Box
              component="form"
              onSubmit={onSubmit}
              noValidate
              sx={{ mt: 4 }}
            >
              <Stack spacing={2.5}>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: {
                      xs: '1fr',
                      sm: '1fr 1fr',
                    },
                    gap: 2,
                  }}
                >
                  {fields.map((field) => (
                    <TextField
                      key={field.name}
                      name={field.name}
                      label={field.label}
                      type={field.type}
                      value={form[field.name]}
                      onChange={onChange}
                      required={Boolean(field.required)}
                      autoComplete={field.autoComplete}
                      error={Boolean(errors[field.name])}
                      helperText={errors[field.name]}
                      slotProps={
                        field.type === 'date'
                          ? { inputLabel: { shrink: true } }
                          : undefined
                      }
                      fullWidth
                      sx={{
                        gridColumn: field.half
                          ? 'auto'
                          : '1 / -1',
                      }}
                    />
                  ))}

                  <TextField
                    select
                    fullWidth
                    name="gender"
                    label="Gender (optional)"
                    value={form.gender}
                    onChange={onChange}
                    sx={{ gridColumn: '1 / -1' }}
                  >
                    <MenuItem value="">Prefer not to say</MenuItem>
                    <MenuItem value="female">Female</MenuItem>
                    <MenuItem value="male">Male</MenuItem>
                    <MenuItem value="unspecified">
                      Other / unspecified
                    </MenuItem>
                  </TextField>
                </Box>

                {error && (
                  <Alert severity="error" role="alert">
                    {error}
                  </Alert>
                )}

                <Button
                  type="submit"
                  variant="contained"
                  size="large"
                  fullWidth
                  disabled={status === 'pending'}
                  sx={{
                    py: 1.5,
                    textTransform: 'none',
                    fontWeight: 600,
                  }}
                >
                  {status === 'pending'
                    ? 'Creating account...'
                    : 'Create account'}
                </Button>

                <Typography variant="body2" textAlign="center">
                  Already have an account?{' '}
                  <Link to="/login" style={{ color: '#12518A' }}>
                    Sign in
                  </Link>
                </Typography>
              </Stack>
            </Box>
          </Paper>
        </Box>
      </Box>
    </ThemeProvider>
  );
}
