"use client";
import React from "react";
import { Box, Typography, Paper } from "@mui/material";
import ProtectedRoute from "./ProtectedRoute";

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <Box className="dashboard-container">
        <Paper elevation={0} className="dashboard-card">
          <Typography variant="h3" className="dashboard-title">
            Dashboard
          </Typography>
          <Typography variant="h6" className="dashboard-subtitle">
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
