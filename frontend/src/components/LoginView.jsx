
import {
  Alert,
  Box,
  Button,
  Paper,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { Link } from 'react-router-dom';
import fdomine from '../theme/fdomine';

// DemoBank branding


export default function LoginView({
  form,
  onChange,
  onSubmit,
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
          gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
          bgcolor: '#f5f7fb',
        }}
      >
        {/* Left: bank branding */}
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

          <Typography
            variant="h3"
            sx={{ fontWeight: 700, mt: 3 }}
          >
            Banking made simple.
          </Typography>

          <Typography variant="body1" sx={{ mt: 2, opacity: 0.85 }}>
            Manage your accounts, track transactions,
            and transfer funds in one place.
          </Typography>
        </Box>

        {/* Right: sign-in form */}
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
              maxWidth: 440,
              p: { xs: 3, sm: 5 },
              border: '1px solid #e2e8f0',
            }}
          >
            <Typography 
            variant="h4"
            component="h1"
            sx={{
                fontSize: '2rem',
                fontWeight: 600,
                letterSpacing: '-0.04em',
                color: '#071B36',
            }}>
              Welcome back
            </Typography>

            <Typography color="text.secondary" sx={{ mt: 1 }}>
              Sign in to your DemoBank account
            </Typography>

            <Box component="form" onSubmit={onSubmit} sx={{ mt: 4 }}>
              <Stack spacing={2.5}>
                <TextField
                  label="Email"
                  name="username"
                  type="email"
                  value={form.username}
                  onChange={onChange}
                  autoComplete="username"
                  required
                  fullWidth
                />

                <TextField
                  label="Password"
                  name="password"
                  type="password"
                  value={form.password}
                  onChange={onChange}
                  autoComplete="current-password"
                  required
                  fullWidth
                />

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
                  disabled={
                    status === 'pending' ||
                    !form.username.trim() ||
                    !form.password
                  }
                  sx={{
                    py: 1.5,
                    textTransform: 'none',
                    fontWeight: 600,
                  }}
                >
                  Sign in
                </Button>

                <Typography variant="body2" textAlign="center">
                  New to DemoBank?{' '}
                  <Link to="/signup" style={{ color: '#12518A' }}>
                    Create an account
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
