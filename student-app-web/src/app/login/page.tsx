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
import { Person, Lock, Visibility, VisibilityOff } from "@mui/icons-material";
import axios from "axios";
import { useRouter } from "next/navigation";
import { useAuth } from "../AuthContext";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await axios.post(`${API_BASE_URL}/api/auth/login`, { username, password });
      const token = typeof res.data === "string" ? res.data : res.data?.token;
      if (!token) throw new Error("Invalid login response");
      // Store JWT via AuthContext (also persists to localStorage there)
      login(token);
      router.push("/dashboard");
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  const toggleShowPassword = () => {
    setShowPassword(!showPassword);
  };

//   return (
//     <Box
//       sx={{
//         minHeight: '100vh',
//         display: 'flex',
//         alignItems: 'center',
//         justifyContent: 'center',
//         background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)',
//         padding: {xs: 2, md: 4}
//       }}
//     >
//       <Paper
//         elevation={6}
//         sx={{
//           width: '100%',
//           maxWidth: 450,
//           borderRadius: 3,
//           overflow: 'hidden',
//           display: 'flex',
//           flexDirection: 'column',
//           position: 'relative',
//         }}
//       >
//         <Box
//           sx={{
//             p: 4,
//             pb: 3,
//             background: 'linear-gradient(90deg, #3f51b5, #3d5afe)',
//             color: 'white',
//             position: 'relative',
//             textAlign: 'center'
//           }}
//         >
//           <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
//             Welcome Back
//           </Typography>
//           <Typography variant="body2" sx={{ opacity: 0.8 }}>
//             Sign in to continue to Student Life Assistance
//           </Typography>
//         </Box>

//         <Box sx={{ p: 4, pt: 3 }}>
//           {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{error}</Alert>}

//           <form onSubmit={handleSubmit}>
//             <Stack spacing={3}>
//               <TextField
//                 label="Username"
//                 value={username}
//                 onChange={e => setUsername(e.target.value)}
//                 fullWidth
//                 required
//                 variant="outlined"
//                 InputProps={{
//                   startAdornment: (
//                     <InputAdornment position="start">
//                       <Person color="action" />
//                     </InputAdornment>
//                   ),
//                   sx: { borderRadius: 2 }
//                 }}
//               />
//               <TextField
//                 label="Password"
//                 type={showPassword ? "text" : "password"}
//                 value={password}
//                 onChange={e => setPassword(e.target.value)}
//                 fullWidth
//                 required
//                 variant="outlined"
//                 InputProps={{
//                   startAdornment: (
//                     <InputAdornment position="start">
//                       <Lock color="action" />
//                     </InputAdornment>
//                   ),
//                   endAdornment: (
//                     <InputAdornment position="end">
//                       <IconButton
//                         aria-label="toggle password visibility"
//                         onClick={toggleShowPassword}
//                         edge="end"
//                       >
//                         {showPassword ? <VisibilityOff /> : <Visibility />}
//                       </IconButton>
//                     </InputAdornment>
//                   ),
//                   sx: { borderRadius: 2 }
//                 }}
//               />

//               <Button
//                 type="submit"
//                 variant="contained"
//                 fullWidth
//                 disabled={loading}
//                 sx={{
//                   mt: 2,
//                   py: 1.5,
//                   borderRadius: 2,
//                   background: 'linear-gradient(90deg, #3f51b5, #3d5afe)',
//                   '&:hover': {
//                     background: 'linear-gradient(90deg, #32408f, #3651e2)'
//                   }
//                 }}
//                 startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
//               >
//                 {loading ? "Logging in..." : "Log In"}
//               </Button>
//             </Stack>
//           </form>

//           <Box sx={{ mt: 3, textAlign: 'center' }}>
//             <Typography variant="body2" color="text.secondary">
//               Don&apos;t have an account?{' '}
//               <Link
//                 href="/signup"
//                 sx={{
//                   fontWeight: 600,
//                   color: 'primary.main',
//                   textDecoration: 'none',
//                   '&:hover': {
//                     textDecoration: 'underline'
//                   }
//                 }}
//               >
//                 Sign up
//               </Link>
//             </Typography>
//           </Box>
//         </Box>
//       </Paper>
//     </Box>
//   );
// }
return (
  <Box
    sx={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      position: 'relative',
      overflow: 'hidden',
      background:
        'linear-gradient(135deg, rgba(37,99,235,1) 0%, rgba(147,51,234,1) 50%, rgba(20,184,166,1) 100%)',
      padding: {xs: 2, md: 4}
    }}
  >
    <Box
      sx={{
        position: 'absolute',
        inset: 0,
        opacity: 0.18,
        backgroundImage:
          "url('https://images.pexels.com/photos/1438081/pexels-photo-1438081.jpeg?auto=compress&cs=tinysrgb&w=1920&h=1280&fit=crop')",
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }}
    />
    <Box
      sx={{
        position: 'absolute',
        top: '20%',
        left: '20%',
        width: 56,
        height: 56,
        bgcolor: '#f59e0b',
        borderRadius: '50%',
        opacity: 0.6
      }}
    />
    <Box
      sx={{
        position: 'absolute',
        bottom: '22%',
        right: '18%',
        width: 44,
        height: 44,
        bgcolor: '#14b8a6',
        borderRadius: '50%',
        opacity: 0.6
      }}
    />
    <Box
      sx={{
        position: 'absolute',
        bottom: '30%',
        left: '35%',
        width: 32,
        height: 32,
        bgcolor: '#a855f7',
        borderRadius: '50%',
        opacity: 0.6
      }}
    />
    <Paper
      elevation={6}
      sx={{
        width: '100%',
        maxWidth: 450,
        borderRadius: 3,
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
        position: 'relative',
        zIndex: 1,
        bgcolor: '#fff'
      }}
    >
      <Box
        sx={{
          p: 4,
          pb: 3,
          background: 'linear-gradient(90deg, #3f51b5, #3d5afe)',
          color: 'white',
          position: 'relative',
          textAlign: 'center'
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: 700, mb: 1 }}>
          Welcome Back
        </Typography>
        <Typography variant="body2" sx={{ opacity: 0.8 }}>
          Sign in to continue to Student Life Assistance
        </Typography>
      </Box>

      <Box sx={{ p: 4, pt: 3 }}>
        {error && <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>{error}</Alert>}

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
                    <Person color="action" />
                  </InputAdornment>
                ),
                sx: { borderRadius: 2 }
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
                    <Lock color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={toggleShowPassword}
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
                sx: { borderRadius: 2 }
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
                background: 'linear-gradient(90deg, #3f51b5, #3d5afe)',
                '&:hover': {
                  background: 'linear-gradient(90deg, #32408f, #3651e2)'
                }
              }}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
            >
              {loading ? "Logging in..." : "Log In"}
            </Button>
          </Stack>
        </form>

        <Box sx={{ mt: 3, textAlign: 'center' }}>
          <Typography variant="body2" color="text.secondary">
            Don&apos;t have an account?{' '}
            <Link
              href="/signup"
              sx={{
                fontWeight: 600,
                color: 'primary.main',
                textDecoration: 'none',
                '&:hover': {
                  textDecoration: 'underline'
                }
              }}
            >
              Sign up
            </Link>
          </Typography>
        </Box>
      </Box>
    </Paper>
  </Box>
);
}
