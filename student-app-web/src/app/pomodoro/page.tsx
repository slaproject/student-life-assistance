"use client";
import React from 'react';
import { Box, Typography, Container, Paper } from '@mui/material';
import PomodoroTimer from '../components/PomodoroTimer';
import ProtectedRoute from '../dashboard/ProtectedRoute';

export default function PomodoroPage() {
  return (
    <ProtectedRoute>
      <Box sx={{ bgcolor: "#000000", minHeight: "100vh", color: "#ffffff" }}>
        <Container maxWidth="lg" sx={{ py: 4 }}>
          <Box sx={{ mb: 4, textAlign: 'center' }}>
            <Typography
              variant="h4"
              component="h1"
              fontWeight="bold"
              sx={{
                mb: 2,
                background: 'linear-gradient(90deg, #3b82f6, #60a5fa)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                textFillColor: 'transparent',
              }}
            >
              Pomodoro Technique
            </Typography>
            <Typography variant="body1" sx={{ color: "#94a3b8", maxWidth: 700, mx: 'auto' }}>
              The Pomodoro Technique is a time management method that uses a timer to break work into intervals,
              traditionally 25 minutes in length, separated by short breaks. Use this timer to boost your productivity and focus.
            </Typography>
          </Box>

          <Box sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, gap: 4 }}>
            {/* Timer Component */}
            <Box sx={{ flex: 1 }}>
              <PomodoroTimer />
            </Box>

            {/* Instructions Panel */}
            <Box sx={{ flex: 1 }}>
              <Paper
                elevation={0}
                sx={{
                  p: 3,
                  borderRadius: 4,
                  height: '100%',
                  bgcolor: "#0a0a0a",
                  border: "1px solid #1e293b",
                  color: "#ffffff"
                }}
              >
                <Typography variant="h5" fontWeight="bold" sx={{ mb: 2, color: '#3b82f6' }}>
                  How It Works
                </Typography>

                <Box component="ol" sx={{ pl: 2 }}>
                  <Box component="li" sx={{ mb: 2 }}>
                    <Typography variant="body1" fontWeight="medium">
                      Choose a task to focus on
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Select something meaningful that requires your full attention.
                    </Typography>
                  </Box>

                  <Box component="li" sx={{ mb: 2 }}>
                    <Typography variant="body1" fontWeight="medium">
                      Set the Pomodoro timer (25 minutes)
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Commit to focusing on your task for the full duration.
                    </Typography>
                  </Box>

                  <Box component="li" sx={{ mb: 2 }}>
                    <Typography variant="body1" fontWeight="medium">
                      Work on the task until the timer rings
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Avoid distractions and stay committed to your work.
                    </Typography>
                  </Box>

                  <Box component="li" sx={{ mb: 2 }}>
                    <Typography variant="body1" fontWeight="medium">
                      Take a short break (5 minutes)
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Step away from your work to rest your mind.
                    </Typography>
                  </Box>

                  <Box component="li">
                    <Typography variant="body1" fontWeight="medium">
                      After 4 pomodoros, take a longer break (15-30 minutes)
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      This longer break helps to recharge your brain for the next round.
                    </Typography>
                  </Box>
                </Box>

                <Typography variant="subtitle2" sx={{ mt: 3, fontStyle: 'italic', color: '#3b82f6' }}>
                  &ldquo;The Pomodoro Technique isn&apos;t about how many pomodoros you complete &ndash; it&apos;s about how you feel at the end of the day.&rdquo;
                </Typography>
              </Paper>
            </Box>
          </Box>

          {/* Benefits Section */}
          <Box sx={{ mt: 4 }}>
            <Typography variant="h5" fontWeight="bold" sx={{ mb: 3, color: '#3b82f6' }}>
              Benefits of the Pomodoro Technique
            </Typography>

            <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr' }, gap: 3 }}>
              {[
                { title: "Improved Focus", desc: "Minimizing interruptions and distractions during focused work periods." },
                { title: "Reduced Burnout", desc: "Regular breaks prevent mental fatigue and help maintain energy levels." },
                { title: "Increased Accountability", desc: "The timer creates a sense of urgency and helps track productivity." },
                { title: "Better Planning", desc: "Breaking work into intervals helps estimate time required for tasks." },
                { title: "Improved Work Quality", desc: "Concentrated focus leads to fewer errors and better results." },
                { title: "Work-Life Balance", desc: "Structured work time helps create boundaries between work and personal life." }
              ].map((item, index) => (
                <Paper key={index} sx={{
                  p: 2,
                  borderRadius: 2,
                  bgcolor: "#0a0a0a",
                  border: "1px solid #1e293b",
                  color: "#ffffff"
                }}>
                  <Typography variant="h6" fontWeight="bold" sx={{ mb: 1, color: "#fff" }}>
                    {item.title}
                  </Typography>
                  <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                    {item.desc}
                  </Typography>
                </Paper>
              ))}
            </Box>
          </Box>
        </Container>
      </Box>
    </ProtectedRoute>
  );
}
