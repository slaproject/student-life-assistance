"use client";
import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  Container, 
  Paper,
  IconButton,
  Collapse,
  Chip
} from '@mui/material';
import { 
  ExpandMore, 
  ExpandLess,
  MusicNote,
  PlayArrow
} from '@mui/icons-material';
import PomodoroTimer from '../components/PomodoroTimer';
import ProtectedRoute from '../dashboard/ProtectedRoute';

export default function PomodoroPage() {
  const [showMusic, setShowMusic] = useState(false);

  // Scroll to top on mount to ensure full visibility
  useEffect(() => {
    // Use instant scroll on first load to avoid visual glitches
    window.scrollTo({ top: 0, behavior: 'auto' });
  }, []);

  return (
    <ProtectedRoute>
      <Box sx={{ 
        bgcolor: "#000000", 
        minHeight: "100vh", 
        color: "#ffffff",
        pt: { xs: 2, sm: 4 },
        pb: 6,
        px: { xs: 1, sm: 2 }
      }}>
        <Container 
          maxWidth="lg" 
          sx={{ 
            pt: { xs: 2, sm: 3 },
            px: { xs: 2, sm: 3 }
          }}
        >
          {/* Header - Simplified */}
          <Box sx={{ 
            mb: { xs: 3, sm: 4 }, 
            textAlign: 'center',
            pt: { xs: 1, sm: 2 }
          }}>
            <Typography
              variant="h3"
              component="h1"
              fontWeight={700}
              sx={{
                mb: { xs: 1, sm: 1.5 },
                background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                letterSpacing: '-0.5px',
                fontSize: { xs: '2rem', sm: '2.5rem', md: '3rem' },
              }}
            >
              Pomodoro Timer
            </Typography>
            <Typography 
              variant="body1" 
              sx={{ 
                color: "#94a3b8", 
                maxWidth: 600, 
                mx: 'auto',
                fontSize: { xs: '0.95rem', sm: '1.05rem' },
                lineHeight: 1.7,
                px: { xs: 1, sm: 0 },
              }}
            >
              Focus for 25 minutes, then take a 5-minute break. Repeat to boost productivity and maintain mental clarity.
            </Typography>
          </Box>

          {/* Main Content - Timer Centered */}
          <Box sx={{ 
            display: 'flex', 
            flexDirection: 'column',
            alignItems: 'center',
            gap: { xs: 3, sm: 4 },
            mb: { xs: 3, sm: 4 },
            width: '100%'
          }}>
            {/* Timer Component */}
            <Box sx={{ 
              width: '100%', 
              maxWidth: 600,
              px: { xs: 0, sm: 0 }
            }}>
              <PomodoroTimer />
            </Box>

            {/* Quick Guide */}
            <Paper
              elevation={0}
              sx={{
                p: { xs: 2.5, sm: 3 },
                borderRadius: 3,
                width: '100%',
                maxWidth: 600,
                bgcolor: "rgba(15, 23, 42, 0.6)",
                backdropFilter: 'blur(10px)',
                border: "1px solid rgba(148, 163, 184, 0.1)",
                color: "#ffffff",
                mx: { xs: 0, sm: 'auto' }
              }}
            >
              <Typography variant="h6" fontWeight={600} sx={{ mb: 2, color: '#6366f1' }}>
                Quick Start Guide
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                  <Chip 
                    label="1" 
                    size="small" 
                    sx={{ 
                      bgcolor: '#6366f1', 
                      color: '#fff',
                      fontWeight: 600,
                      minWidth: 32,
                      height: 32,
                    }} 
                  />
                  <Box>
                    <Typography variant="body1" fontWeight={500} sx={{ mb: 0.5 }}>
                      Choose a task
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Pick something that needs your full attention
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                  <Chip 
                    label="2" 
                    size="small" 
                    sx={{ 
                      bgcolor: '#6366f1', 
                      color: '#fff',
                      fontWeight: 600,
                      minWidth: 32,
                      height: 32,
                    }} 
                  />
                  <Box>
                    <Typography variant="body1" fontWeight={500} sx={{ mb: 0.5 }}>
                      Set timer & start
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Click Start and work until the timer completes
                    </Typography>
                  </Box>
                </Box>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                  <Chip 
                    label="3" 
                    size="small" 
                    sx={{ 
                      bgcolor: '#6366f1', 
                      color: '#fff',
                      fontWeight: 600,
                      minWidth: 32,
                      height: 32,
                    }} 
                  />
                  <Box>
                    <Typography variant="body1" fontWeight={500} sx={{ mb: 0.5 }}>
                      Take a break
                    </Typography>
                    <Typography variant="body2" sx={{ color: "#94a3b8" }}>
                      Rest for 5 minutes, then repeat
                    </Typography>
                  </Box>
                </Box>
              </Box>
            </Paper>

            {/* Study Music Section */}
            <Paper
              elevation={0}
              sx={{
                width: '100%',
                maxWidth: 600,
                borderRadius: 3,
                overflow: 'hidden',
                bgcolor: "rgba(15, 23, 42, 0.6)",
                backdropFilter: 'blur(10px)',
                border: "1px solid rgba(148, 163, 184, 0.1)",
                color: "#ffffff",
                mx: { xs: 0, sm: 'auto' }
              }}
            >
              <Box
                onClick={() => setShowMusic(!showMusic)}
                sx={{
                  p: 2.5,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  cursor: 'pointer',
                  transition: 'all 0.2s ease',
                  '&:hover': {
                    bgcolor: 'rgba(99, 102, 241, 0.1)',
                  }
                }}
              >
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <MusicNote sx={{ color: '#6366f1', fontSize: '1.75rem' }} />
                  <Box>
                    <Typography variant="h6" fontWeight={600} sx={{ color: '#ffffff' }}>
                      Focus Music
                    </Typography>
                    <Typography variant="body2" sx={{ color: '#94a3b8', mt: 0.5 }}>
                      Lofi hip hop study beats to help you concentrate
                    </Typography>
                  </Box>
                </Box>
                <IconButton
                  sx={{
                    color: '#6366f1',
                    transition: 'transform 0.2s ease',
                    transform: showMusic ? 'rotate(180deg)' : 'rotate(0deg)',
                  }}
                >
                  {showMusic ? <ExpandLess /> : <ExpandMore />}
                </IconButton>
              </Box>

              <Collapse in={showMusic}>
                <Box sx={{ p: 0, pb: 2.5, px: 2.5 }}>
                  <Box
                    sx={{
                      position: 'relative',
                      width: '100%',
                      borderRadius: 2,
                      overflow: 'hidden',
                      bgcolor: 'rgba(0, 0, 0, 0.3)',
                      aspectRatio: '16/9',
                    }}
                  >
                    <iframe
                      width="100%"
                      height="100%"
                      src="https://www.youtube.com/embed/jfKfPfyJRdk?autoplay=0&rel=0&modestbranding=1"
                      title="Lofi Hip Hop Study Music"
                      frameBorder="0"
                      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                      allowFullScreen
                      style={{
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        width: '100%',
                        height: '100%',
                      }}
                    />
                  </Box>
                  <Typography 
                    variant="caption" 
                    sx={{ 
                      color: '#64748b', 
                      mt: 1.5, 
                      display: 'block',
                      fontStyle: 'italic'
                    }}
                  >
                    Tip: Use fullscreen mode on the timer for a distraction-free experience
                  </Typography>
                </Box>
              </Collapse>
            </Paper>
          </Box>
        </Container>
      </Box>
    </ProtectedRoute>
  );
}
