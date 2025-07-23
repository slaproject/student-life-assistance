"use client";
import React from "react";
import { Box, Button, Typography, AppBar, Toolbar, Container, Paper } from "@mui/material";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();

  return (
    <Box
      minHeight="100vh"
      sx={{
        background: "linear-gradient(135deg, #6a11cb 0%, #2575fc 100%)",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <AppBar position="static" elevation={0} sx={{ background: "transparent", boxShadow: "none", p: 2 }}>
        <Toolbar sx={{ justifyContent: "flex-end" }}>
          <Button
            color="inherit"
            variant="outlined"
            sx={{ borderColor: "#fff", color: "#fff", fontWeight: 600, borderRadius: 2 }}
            onClick={() => router.push("/login")}
          >
            Login
          </Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth="md" sx={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center" }}>
        <Paper
          elevation={8}
          sx={{
            p: { xs: 3, md: 6 },
            borderRadius: 4,
            background: "rgba(255,255,255,0.85)",
            textAlign: "center",
            boxShadow: "0 8px 32px 0 rgba(31, 38, 135, 0.37)",
          }}
        >
          <Typography variant="h2" fontWeight={800} color="primary" mb={2}>
            Student Life Assistant
          </Typography>
          <Typography variant="h5" color="text.secondary" mb={3}>
            Organize your student life, manage your calendar, and never miss an event again.
          </Typography>
          <Button
            variant="contained"
            size="large"
            sx={{
              background: "linear-gradient(90deg, #6a11cb 0%, #2575fc 100%)",
              color: "#fff",
              fontWeight: 700,
              px: 5,
              py: 1.5,
              borderRadius: 3,
              fontSize: "1.2rem",
              boxShadow: "0 4px 20px 0 rgba(106,17,203,0.2)",
              mt: 2,
              transition: "0.2s",
              '&:hover': {
                background: "linear-gradient(90deg, #2575fc 0%, #6a11cb 100%)",
                boxShadow: "0 6px 24px 0 rgba(37,117,252,0.2)",
              },
            }}
            onClick={() => router.push("/signup")}
          >
            Get Started
          </Button>
        </Paper>
      </Container>
    </Box>
  );
}
