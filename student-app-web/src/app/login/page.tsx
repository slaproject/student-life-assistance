"use client";
import React, { useState } from "react";
import { Box, Button, TextField, Typography, Alert, Link, CircularProgress, Paper } from "@mui/material";
import axios from "axios";
import { useRouter } from "next/navigation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function LoginPage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await axios.post(`${API_BASE_URL}/api/auth/login`, { username, password });
      // Store JWT in localStorage (for demo; use HttpOnly cookies for production)
      localStorage.setItem("token", res.data.token);
      router.push("/dashboard");
    } catch (err: any) {
      setError(err.response?.data?.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box minHeight="100vh" display="flex" alignItems="center" justifyContent="center" bgcolor="#f5f6fa">
      <Paper elevation={4} sx={{ p: 4, minWidth: 350, borderRadius: 3 }}>
        <Typography variant="h4" fontWeight={700} mb={2} align="center">Login</Typography>
        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
        <form onSubmit={handleSubmit}>
          <TextField
            label="Username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            fullWidth
            margin="normal"
            required
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            fullWidth
            margin="normal"
            required
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            sx={{ mt: 2, mb: 1, py: 1.2, fontWeight: 600 }}
            disabled={loading}
            startIcon={loading && <CircularProgress size={20} />}
          >
            {loading ? "Logging in..." : "Login"}
          </Button>
        </form>
        <Typography variant="body2" align="center" mt={2}>
          Don&apos;t have an account? <Link href="/signup">Sign up</Link>
        </Typography>
      </Paper>
    </Box>
  );
} 