"use client";
import React, { useState } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  Link,
  CircularProgress,
  Paper,
  Stack,
  InputAdornment,
  IconButton
} from "@mui/material";
import { Person, Email, Lock, Visibility, VisibilityOff } from "@mui/icons-material";
import axios from "axios";
import { useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function SignupPage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");
    try {
      await axios.post(`${API_BASE_URL}/api/auth/signup`, { username, email, password });
      setSuccess("Signup successful! Please login.");
      setTimeout(() => router.push("/login"), 1500);
    } catch (err: unknown) {
      const errorMessage = err instanceof Error
        ? err.message
        : (err as { response?: { data?: { message?: string } } })?.response?.data?.message || "Signup failed";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const toggleShowPassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        position: 'relative',
        overflow: 'hidden',
        bgcolor: '#000000',
        background: 'radial-gradient(circle at 50% 50%, #0a1929 0%, #000000 100%)',
        padding: { xs: 2, md: 4 }
      }}
    >
      <Box
        sx={{
          position: 'absolute',
          top: '20%',
          left: '20%',
          width: 300,
          height: 300,
          background: 'linear-gradient(135deg, #1e3a8a 0%, #000000 100%)',
          filter: 'blur(100px)',
          opacity: 0.4,
          borderRadius: '50%'
        }}
      />
      <Box
        sx={{
          position: 'absolute',
          bottom: '20%',
          right: '20%',
          width: 300,
          height: 300,
          background: 'linear-gradient(135deg, #2563eb 0%, #000000 100%)',
          filter: 'blur(100px)',
          opacity: 0.3,
          borderRadius: '50%'
        }}
      />

      <Paper
        elevation={0}
        sx={{
          width: '100%',
          maxWidth: 450,
          borderRadius: 3,
          overflow: 'hidden',
          display: 'flex',
          flexDirection: 'column',
          position: 'relative',
          zIndex: 1,
          bgcolor: '#0a0a0a',
          border: '1px solid #1e293b',
          color: '#ffffff'
        }}
      >
        <Box
          sx={{
            p: 4,
            pb: 3,
            borderBottom: '1px solid #1e293b',
            position: 'relative',
            textAlign: 'center'
          }}
        >
          <Typography variant="h4" sx={{ fontWeight: 700, mb: 1, color: '#3b82f6' }}>
            Create Account
          </Typography>
          <Typography variant="body2" sx={{ color: '#94a3b8' }}>
            Join Student Life Assistance to organize your academic journey
          </Typography>
        </Box>

        <Box sx={{ p: 4, pt: 3 }}>
          {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{error}</Alert>}
          {success && <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }}>{success}</Alert>}

          <form onSubmit={handleSubmit}>
            <Stack spacing={3}>
              <TextField
                label="Username"
                value={username}
                onChange={e => setUsername(e.target.value)}
                fullWidth
                required
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Person sx={{ color: '#94a3b8' }} />
                    </InputAdornment>
                  ),
                  sx: {
                    borderRadius: 2,
                    color: '#ffffff',
                    '& .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#334155'
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#475569'
                    },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#3b82f6'
                    }
                  }
                }}
                InputLabelProps={{
                  sx: { color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }
                }}
              />
              <TextField
                label="Email"
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                fullWidth
                required
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Email sx={{ color: '#94a3b8' }} />
                    </InputAdornment>
                  ),
                  sx: {
                    borderRadius: 2,
                    color: '#ffffff',
                    '& .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#334155'
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#475569'
                    },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#3b82f6'
                    }
                  }
                }}
                InputLabelProps={{
                  sx: { color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }
                }}
              />
              <TextField
                label="Password"
                type={showPassword ? "text" : "password"}
                value={password}
                onChange={e => setPassword(e.target.value)}
                fullWidth
                required
                variant="outlined"
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">
                      <Lock sx={{ color: '#94a3b8' }} />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label="toggle password visibility"
                        onClick={toggleShowPassword}
                        edge="end"
                        sx={{ color: '#94a3b8' }}
                      >
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  ),
                  sx: {
                    borderRadius: 2,
                    color: '#ffffff',
                    '& .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#334155'
                    },
                    '&:hover .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#475569'
                    },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                      borderColor: '#3b82f6'
                    }
                  }
                }}
                InputLabelProps={{
                  sx: { color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }
                }}
              />

              <Button
                type="submit"
                variant="contained"
                fullWidth
                disabled={loading}
                sx={{
                  mt: 2,
                  py: 1.5,
                  borderRadius: 2,
                  bgcolor: '#2563eb',
                  '&:hover': {
                    bgcolor: '#1d4ed8'
                  }
                }}
                startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
              >
                {loading ? "Creating Account..." : "Sign Up"}
              </Button>
            </Stack>
          </form>

          <Box sx={{ mt: 3, textAlign: 'center' }}>
            <Typography variant="body2" sx={{ color: '#94a3b8' }}>
              Already have an account?{' '}
              <Link
                href="/login"
                sx={{
                  fontWeight: 600,
                  color: '#3b82f6',
                  textDecoration: 'none',
                  '&:hover': {
                    textDecoration: 'underline'
                  }
                }}
              >
                Log in
              </Link>
            </Typography>
          </Box>
        </Box>
      </Paper>
    </Box>
  );
}