"use client";
import React from "react";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "../AuthContext";
import "../globals.css"; // Ensure the global styles are imported

export default function Navbar() {
  const pathname = usePathname();
  const { isAuthenticated, logout } = useAuth();

  // Hide on auth pages
  if (pathname?.startsWith("/login") || pathname?.startsWith("/signup")) {
    return null;
  }

  return (
    <AppBar position="sticky" color="primary" className="navbar-appbar">
      <Toolbar className="navbar-toolbar">
        <Typography variant="h6" component="div" className="navbar-title">
          Student Life Assistance Project (SLAP)
        </Typography>
        <Box className="navbar-links">
          <Button component={Link} href="/dashboard" className="navbar-link">Dashboard</Button>
          <Button component={Link} href="/calendar" className="navbar-link">Calendar</Button>
          <Button component={Link} href="/tasks" className="navbar-link">To-Do List</Button>
          <Button component={Link} href="/pomodoro" className="navbar-link">Pomodoro</Button>
          <Button component={Link} href="/finance" className="navbar-link">Finance</Button>
          {isAuthenticated && (
            <Button className="navbar-link" onClick={logout}>Logout</Button>
          )}
        </Box>
      </Toolbar>
    </AppBar>
  );
}
