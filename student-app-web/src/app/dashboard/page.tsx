"use client";
import React from "react";
import { Box, Typography, Paper } from "@mui/material";
import ProtectedRoute from "./ProtectedRoute";

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <Box minHeight="100vh" display="flex" alignItems="center" justifyContent="center" bgcolor="#f5f6fa">
        <Paper elevation={4} sx={{ p: 6, minWidth: 400, borderRadius: 3, textAlign: "center" }}>
          <Typography variant="h3" fontWeight={700} mb={2} color="primary">
            Dashboard
          </Typography>
          <Typography variant="h6" color="text.secondary" mb={2}>
            Welcome to your student life dashboard!
          </Typography>
          <Typography variant="body1" color="text.secondary">
            This is a placeholder. Your calendar, events, and more will appear here soon.
          </Typography>
        </Paper>
      </Box>
    </ProtectedRoute>
  );
} 