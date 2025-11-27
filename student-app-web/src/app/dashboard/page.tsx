import React from "react";
import { Box, Paper, Typography, Grid } from "@mui/material";
import ProtectedRoute from "./ProtectedRoute";
import SummaryCard from "./components/SummaryCard";
import HistoryList from "./components/HistoryList";

export default function DashboardPage() {
  return (
    <ProtectedRoute>
      <Box className="dashboard-container" sx={{
        background: "radial-gradient(circle at 50% 50%, #0a1929 0%, #000000 100%)",
        minHeight: "100vh",
        color: "#ffffff",
        p: 4
      }}>
        <Box sx={{ maxWidth: '1200px', width: '100%', mx: 'auto' }}>
          <Typography variant="h4" sx={{ mb: 4, fontWeight: 800, color: "#3b82f6" }}>
            Dashboard
          </Typography>
          <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '7fr 5fr' }, gap: 4 }}>
            <Box>
              <SummaryCard />
            </Box>
            <Box>
              <HistoryList />
            </Box>
          </Box>
        </Box>
      </Box>
    </ProtectedRoute>
  );
}
