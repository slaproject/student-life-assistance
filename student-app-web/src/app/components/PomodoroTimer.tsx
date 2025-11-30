import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  Typography,
  Button,
  ButtonGroup,
  Paper,
  Stack,
  IconButton,
  CircularProgress,
  Tooltip,
  Fade,
  Zoom,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField
} from '@mui/material';
import {
  PlayArrow,
  Pause,
  Refresh,
  AvTimer,
  Notifications,
  NotificationsOff,
  Close as CloseIcon,
  Fullscreen,
  FullscreenExit
} from '@mui/icons-material';

// Default Pomodoro durations in seconds
const POMODORO_PRESETS = {
  pomodoro: 25 * 60, // 25 minutes
  shortBreak: 5 * 60, // 5 minutes
  longBreak: 15 * 60, // 15 minutes
  custom45: 45 * 60, // 45 minutes
  custom60: 60 * 60  // 60 minutes
};

const PomodoroTimer = () => {
  // Timer state
  const [timeLeft, setTimeLeft] = useState(POMODORO_PRESETS.pomodoro);
  const [totalTime, setTotalTime] = useState(POMODORO_PRESETS.pomodoro);
  const [isRunning, setIsRunning] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [timerMode, setTimerMode] = useState('pomodoro');
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [customTimerOpen, setCustomTimerOpen] = useState(false);
  const [customMinutes, setCustomMinutes] = useState('');
  const [customTimerError, setCustomTimerError] = useState('');
  const [isFullscreen, setIsFullscreen] = useState(false);
  const timerRef = useRef<HTMLDivElement>(null);

  // Timer interval ref
  const timerIntervalRef = useRef<NodeJS.Timeout | null>(null);

  // Audio for timer completion
  const audioRef = useRef<HTMLAudioElement>(null);

  // Format time as HH:MM:SS or MM:SS depending on duration
  const formatTime = (seconds: number) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (hours > 0) {
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }

    return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  // Calculate progress percentage
  const calculateProgress = () => {
    if (totalTime === 0) return 0;
    return ((totalTime - timeLeft) / totalTime) * 100;
  };

  // Handle timer completion
  const handleTimerComplete = () => {
    setIsRunning(false);
    setIsCompleted(true);

    if (notificationsEnabled) {
      // Play sound
      if (audioRef.current) {
        audioRef.current.play().catch(error => console.error('Error playing audio:', error));
      }

      // Show browser notification if permission granted
      if (Notification.permission === 'granted') {
        new Notification('Pomodoro Timer Completed', {
          body: `Your ${timerMode} session is complete!`,
          icon: '/favicon.ico'
        });
      }
    }
  };

  // Start timer
  const startTimer = () => {
    if (isCompleted) {
      // If completed, reset timer before starting
      resetTimer();
    }

    setIsRunning(true);
    setIsCompleted(false);

    // Automatically enter fullscreen when starting timer (if not already in fullscreen)
    // Only enter fullscreen if timer is at full duration (fresh start, not resuming)
    if (timeLeft === totalTime && !document.fullscreenElement) {
      enterFullscreen();
    }

    // Clear any existing interval
    if (timerIntervalRef.current) {
      clearInterval(timerIntervalRef.current);
    }

    // Start a new interval
    timerIntervalRef.current = setInterval(() => {
      setTimeLeft(prevTime => {
        if (prevTime <= 1) {
          if (timerIntervalRef.current) {
            clearInterval(timerIntervalRef.current);
          }
          handleTimerComplete();
          return 0;
        }
        return prevTime - 1;
      });
    }, 1000);
  };

  // Pause timer
  const pauseTimer = () => {
    setIsRunning(false);
    if (timerIntervalRef.current) {
      clearInterval(timerIntervalRef.current);
    }
  };

  // Reset timer
  const resetTimer = () => {
    pauseTimer();
    setTimeLeft(totalTime);
    setIsCompleted(false);
  };

  // Set timer mode
  const setTimerDuration = (mode: string, duration: number) => {
    pauseTimer();
    setTimerMode(mode);
    setTotalTime(duration);
    setTimeLeft(duration);
    setIsCompleted(false);
  };

  // Toggle notifications
  const toggleNotifications = async () => {
    if (notificationsEnabled) {
      setNotificationsEnabled(false);
    } else {
      // Request notification permission if not granted
      if (Notification.permission !== 'granted') {
        const permission = await Notification.requestPermission();
        if (permission !== 'granted') {
          // If permission denied, keep notifications disabled
          return;
        }
      }
      setNotificationsEnabled(true);
    }
  };

  // Cleanup timer on unmount
  useEffect(() => {
    return () => {
      if (timerIntervalRef.current) {
        clearInterval(timerIntervalRef.current);
      }
    };
  }, []);

  // Get timer status classes
  const getTimerStatusClass = () => {
    if (isCompleted) return "completed";
    if (isRunning) return "running";
    if (timeLeft < totalTime) return "paused";
    return "";
  };

  // Get color for timer circle based on status
  const getTimerColor = () => {
    if (isCompleted) return '#10b981'; // Emerald green
    if (isRunning) return '#ef4444';   // Red
    if (timeLeft < totalTime) return '#f59e0b'; // Amber
    return '#6366f1';                  // Indigo
  };

  // Get gradient for timer circle based on status
  const getTimerGradient = () => {
    if (isCompleted) return 'linear-gradient(135deg, #10b981 0%, #059669 100%)';
    if (isRunning) return 'linear-gradient(135deg, #ef4444 0%, #dc2626 100%)';
    if (timeLeft < totalTime) return 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)';
    return 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)';
  };

  // Handle custom timer submission
  const handleCustomTimerSubmit = () => {
    const minutes = Number(customMinutes);
    if (minutes > 0 && minutes <= 1440 && !isNaN(minutes)) {
      setTimerDuration('custom', minutes * 60);
      setCustomTimerOpen(false);
      setCustomMinutes('');
      setCustomTimerError('');
    }
  };

  // Enter fullscreen
  const enterFullscreen = async () => {
    if (!timerRef.current || document.fullscreenElement) return;

    try {
      await timerRef.current.requestFullscreen();
      setIsFullscreen(true);
    } catch (error) {
      // Silently fail if user denies fullscreen or browser doesn't support it
      console.log('Fullscreen not available:', error);
    }
  };

  // Handle fullscreen toggle
  const toggleFullscreen = async () => {
    if (!timerRef.current) return;

    try {
      if (!document.fullscreenElement) {
        await timerRef.current.requestFullscreen();
        setIsFullscreen(true);
      } else {
        await document.exitFullscreen();
        setIsFullscreen(false);
      }
    } catch (error) {
      console.error('Error toggling fullscreen:', error);
    }
  };

  // Listen for fullscreen changes
  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullscreen(!!document.fullscreenElement);
    };

    document.addEventListener('fullscreenchange', handleFullscreenChange);
    return () => {
      document.removeEventListener('fullscreenchange', handleFullscreenChange);
    };
  }, []);

  return (
    <Fade in={true} timeout={800}>
      <Paper
        ref={timerRef}
        elevation={0}
        sx={{
          borderRadius: isFullscreen ? 0 : 6,
          overflow: 'hidden',
          width: '100%',
          maxWidth: isFullscreen ? '100%' : { xs: '100%', sm: 520 },
          height: isFullscreen ? '100vh' : 'auto',
          mx: 'auto',
          p: 0,
          bgcolor: 'rgba(15, 23, 42, 0.8)',
          backdropFilter: 'blur(20px)',
          border: '1px solid rgba(148, 163, 184, 0.1)',
          color: '#ffffff',
          boxShadow: '0 20px 60px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(255, 255, 255, 0.05) inset',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          display: 'flex',
          flexDirection: 'column',
          '&:hover': {
            boxShadow: '0 25px 70px rgba(0, 0, 0, 0.6), 0 0 0 1px rgba(255, 255, 255, 0.08) inset',
          }
        }}
      >
        {/* Timer Header */}
        <Box
          sx={{
            p: 3,
            background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(79, 70, 229, 0.15) 100%)',
            borderBottom: '1px solid rgba(148, 163, 184, 0.1)',
            textAlign: 'center',
            position: 'relative',
            backdropFilter: 'blur(10px)',
          }}
        >
          <Typography 
            variant="h5" 
            fontWeight={600}
            sx={{
              background: 'linear-gradient(135deg, #ffffff 0%, #cbd5e1 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              letterSpacing: '0.5px',
              mb: 0.5,
            }}
          >
            Pomodoro Timer
          </Typography>
          <Typography 
            variant="body2" 
            sx={{ 
              color: '#94a3b8',
              textTransform: 'capitalize',
              fontWeight: 500,
              letterSpacing: '0.3px',
            }}
          >
            {timerMode.replace(/([A-Z])/g, ' $1').trim()} Mode
          </Typography>

          <Box sx={{ position: 'absolute', right: 12, top: 12, display: 'flex', gap: 1 }}>
            <Tooltip title={notificationsEnabled ? "Disable Notifications" : "Enable Notifications"} arrow>
              <IconButton
                size="small"
                onClick={toggleNotifications}
                sx={{ 
                  color: notificationsEnabled ? '#6366f1' : '#64748b',
                  transition: 'all 0.2s ease',
                  '&:hover': {
                    bgcolor: 'rgba(99, 102, 241, 0.1)',
                    transform: 'scale(1.1)',
                  }
                }}
              >
                {notificationsEnabled ? <Notifications /> : <NotificationsOff />}
              </IconButton>
            </Tooltip>
            <Tooltip title={isFullscreen ? "Exit Fullscreen" : "Enter Fullscreen"} arrow>
              <IconButton
                size="small"
                onClick={toggleFullscreen}
                sx={{ 
                  color: '#6366f1',
                  transition: 'all 0.2s ease',
                  '&:hover': {
                    bgcolor: 'rgba(99, 102, 241, 0.1)',
                    transform: 'scale(1.1)',
                  }
                }}
              >
                {isFullscreen ? <FullscreenExit /> : <Fullscreen />}
              </IconButton>
            </Tooltip>
          </Box>
        </Box>

        {/* Timer Display */}
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            py: isFullscreen ? 8 : 6,
            px: 3,
            position: 'relative',
            background: 'radial-gradient(circle at center, rgba(99, 102, 241, 0.05) 0%, transparent 70%)',
            flex: 1,
            minHeight: isFullscreen ? 'calc(100vh - 120px)' : 'auto',
          }}
        >
          <Zoom in={true} timeout={600}>
            <Box
              sx={{
                position: 'relative',
                width: isFullscreen ? 320 : 240,
                height: isFullscreen ? 320 : 240,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mb: isFullscreen ? 6 : 4,
                transition: 'all 0.3s ease',
              }}
            >
              {/* Outer glow effect */}
              <Box
                sx={{
                  position: 'absolute',
                  width: isFullscreen ? 340 : 260,
                  height: isFullscreen ? 340 : 260,
                  borderRadius: '50%',
                  background: `radial-gradient(circle, ${getTimerColor()}20 0%, transparent 70%)`,
                  filter: 'blur(20px)',
                  transition: 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)',
                  opacity: isRunning ? 0.8 : 0.4,
                }}
              />
              
              {/* Circular Progress */}
              <CircularProgress
                variant="determinate"
                value={calculateProgress()}
                size={isFullscreen ? 320 : 240}
                thickness={3}
                sx={{
                  position: 'absolute',
                  top: 0,
                  left: 0,
                  transform: 'rotate(-90deg)',
                  filter: 'drop-shadow(0 0 8px rgba(99, 102, 241, 0.4))',
                  '& .MuiCircularProgress-circle': {
                    strokeLinecap: 'round',
                    transition: 'stroke-dashoffset 0.5s cubic-bezier(0.4, 0, 0.2, 1)',
                  },
                  '& .MuiCircularProgress-root': {
                    color: getTimerColor(),
                  },
                }}
              />
              
              {/* Inner circle background */}
              <Box
                sx={{
                  position: 'absolute',
                  width: isFullscreen ? 280 : 200,
                  height: isFullscreen ? 280 : 200,
                  borderRadius: '50%',
                  background: 'radial-gradient(circle, rgba(15, 23, 42, 0.9) 0%, rgba(15, 23, 42, 0.95) 100%)',
                  border: '1px solid rgba(148, 163, 184, 0.1)',
                  boxShadow: 'inset 0 0 30px rgba(0, 0, 0, 0.5)',
                  transition: 'all 0.3s ease',
                }}
              />
              
              {/* Time Display */}
              <Typography
                variant="h2"
                className={`timer-display ${getTimerStatusClass()}`}
                sx={{
                  fontWeight: 300,
                  fontSize: isFullscreen 
                    ? { xs: '4rem', sm: '5rem' }
                    : { xs: '2.5rem', sm: '3.5rem' },
                  background: getTimerGradient(),
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                  letterSpacing: '2px',
                  fontVariantNumeric: 'tabular-nums',
                  transition: 'all 0.5s cubic-bezier(0.4, 0, 0.2, 1)',
                  position: 'relative',
                  zIndex: 1,
                  textShadow: `0 0 20px ${getTimerColor()}40`,
                }}
              >
                {formatTime(timeLeft)}
              </Typography>
            </Box>
          </Zoom>

          {/* Timer Controls */}
          <Stack direction="row" spacing={2} sx={{ mb: 4, width: '100%', justifyContent: 'center' }}>
            {isRunning ? (
              <Button
                variant="contained"
                startIcon={<Pause />}
                onClick={pauseTimer}
                sx={{ 
                  borderRadius: 3,
                  px: 4,
                  py: 1.5,
                  background: 'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
                  color: '#ffffff',
                  fontWeight: 600,
                  textTransform: 'none',
                  fontSize: '0.95rem',
                  letterSpacing: '0.5px',
                  boxShadow: '0 4px 15px rgba(245, 158, 11, 0.4)',
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    background: 'linear-gradient(135deg, #d97706 0%, #b45309 100%)',
                    boxShadow: '0 6px 20px rgba(245, 158, 11, 0.5)',
                    transform: 'translateY(-2px)',
                  },
                  '&:active': {
                    transform: 'translateY(0)',
                  }
                }}
              >
                Pause
              </Button>
            ) : (
              <Button
                variant="contained"
                startIcon={<PlayArrow />}
                onClick={startTimer}
                sx={{ 
                  borderRadius: 3,
                  px: 4,
                  py: 1.5,
                  background: isCompleted 
                    ? 'linear-gradient(135deg, #10b981 0%, #059669 100%)'
                    : 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)',
                  color: '#ffffff',
                  fontWeight: 600,
                  textTransform: 'none',
                  fontSize: '0.95rem',
                  letterSpacing: '0.5px',
                  boxShadow: isCompleted
                    ? '0 4px 15px rgba(16, 185, 129, 0.4)'
                    : '0 4px 15px rgba(99, 102, 241, 0.4)',
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    background: isCompleted
                      ? 'linear-gradient(135deg, #059669 0%, #047857 100%)'
                      : 'linear-gradient(135deg, #4f46e5 0%, #4338ca 100%)',
                    boxShadow: isCompleted
                      ? '0 6px 20px rgba(16, 185, 129, 0.5)'
                      : '0 6px 20px rgba(99, 102, 241, 0.5)',
                    transform: 'translateY(-2px)',
                  },
                  '&:active': {
                    transform: 'translateY(0)',
                  }
                }}
              >
                Start
              </Button>
            )}
            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={resetTimer}
              sx={{ 
                borderRadius: 3,
                px: 3,
                py: 1.5,
                borderColor: 'rgba(148, 163, 184, 0.3)',
                color: '#cbd5e1',
                fontWeight: 500,
                textTransform: 'none',
                fontSize: '0.95rem',
                letterSpacing: '0.3px',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                '&:hover': {
                  borderColor: 'rgba(148, 163, 184, 0.5)',
                  bgcolor: 'rgba(148, 163, 184, 0.1)',
                  transform: 'translateY(-2px)',
                }
              }}
            >
              Reset
            </Button>
            <Tooltip title="Custom Timer" arrow placement="top">
              <IconButton
                sx={{ 
                  color: '#6366f1',
                  border: '1px solid rgba(99, 102, 241, 0.3)',
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': {
                    bgcolor: 'rgba(99, 102, 241, 0.1)',
                    borderColor: 'rgba(99, 102, 241, 0.5)',
                    transform: 'translateY(-2px) scale(1.05)',
                  }
                }}
                onClick={() => {
                  setCustomMinutes('');
                  setCustomTimerError('');
                  setCustomTimerOpen(true);
                }}
              >
                <AvTimer />
              </IconButton>
            </Tooltip>
          </Stack>

          {/* Timer Preset Buttons */}
          <Box
            sx={{
              display: 'flex',
              flexWrap: 'wrap',
              gap: 1.5,
              justifyContent: 'center',
              width: '100%',
              px: 2,
            }}
          >
            {[
              { mode: 'pomodoro', label: '25 min', duration: POMODORO_PRESETS.pomodoro },
              { mode: 'shortBreak', label: '5 min', duration: POMODORO_PRESETS.shortBreak },
              { mode: 'longBreak', label: '15 min', duration: POMODORO_PRESETS.longBreak },
              { mode: 'custom45', label: '45 min', duration: POMODORO_PRESETS.custom45 },
              { mode: 'custom60', label: '60 min', duration: POMODORO_PRESETS.custom60 },
            ].map((preset) => {
              const isActive = timerMode === preset.mode;
              return (
                <Button
                  key={preset.mode}
                  onClick={() => setTimerDuration(preset.mode, preset.duration)}
                  sx={{
                    borderRadius: 2.5,
                    px: 3,
                    py: 1,
                    minWidth: { xs: 70, sm: 90 },
                    fontSize: '0.875rem',
                    fontWeight: 500,
                    letterSpacing: '0.3px',
                    textTransform: 'none',
                    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                    ...(isActive
                      ? {
                          background: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)',
                          color: '#ffffff',
                          boxShadow: '0 4px 12px rgba(99, 102, 241, 0.4)',
                          border: '1px solid rgba(99, 102, 241, 0.5)',
                          '&:hover': {
                            background: 'linear-gradient(135deg, #4f46e5 0%, #4338ca 100%)',
                            boxShadow: '0 6px 16px rgba(99, 102, 241, 0.5)',
                            transform: 'translateY(-2px)',
                          }
                        }
                      : {
                          borderColor: 'rgba(148, 163, 184, 0.2)',
                          color: '#94a3b8',
                          bgcolor: 'rgba(15, 23, 42, 0.5)',
                          border: '1px solid rgba(148, 163, 184, 0.2)',
                          '&:hover': {
                            borderColor: 'rgba(148, 163, 184, 0.4)',
                            bgcolor: 'rgba(148, 163, 184, 0.1)',
                            color: '#cbd5e1',
                            transform: 'translateY(-2px)',
                          }
                        }),
                  }}
                >
                  {preset.label}
                </Button>
              );
            })}
          </Box>
        </Box>

        {/* Audio for timer completion */}
        <audio ref={audioRef} preload="auto">
          <source src="/notification-sound.mp3" type="audio/mpeg" />
          Your browser does not support the audio element.
        </audio>

        {/* Custom Timer Dialog */}
        <Dialog
          open={customTimerOpen}
          onClose={() => {
            setCustomTimerOpen(false);
            setCustomMinutes('');
            setCustomTimerError('');
          }}
          maxWidth="sm"
          fullWidth
          PaperProps={{
            sx: {
              borderRadius: 4,
              overflow: 'hidden',
              bgcolor: 'rgba(15, 23, 42, 0.95)',
              backdropFilter: 'blur(20px)',
              color: '#ffffff',
              border: '1px solid rgba(148, 163, 184, 0.1)',
              boxShadow: '0 20px 60px rgba(0, 0, 0, 0.5)',
            }
          }}
        >
          <DialogTitle
            sx={{
              p: 3,
              pb: 2,
              background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(79, 70, 229, 0.15) 100%)',
              borderBottom: '1px solid rgba(148, 163, 184, 0.1)',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
              <AvTimer sx={{ color: '#6366f1', fontSize: '1.75rem' }} />
              <Typography
                variant="h6"
                fontWeight={600}
                sx={{
                  background: 'linear-gradient(135deg, #ffffff 0%, #cbd5e1 100%)',
                  WebkitBackgroundClip: 'text',
                  WebkitTextFillColor: 'transparent',
                  backgroundClip: 'text',
                  letterSpacing: '0.3px',
                }}
              >
                Set Custom Timer
              </Typography>
            </Box>
            <IconButton
              onClick={() => {
                setCustomTimerOpen(false);
                setCustomMinutes('');
                setCustomTimerError('');
              }}
              size="small"
              sx={{
                color: '#94a3b8',
                transition: 'all 0.2s ease',
                '&:hover': {
                  color: '#ffffff',
                  bgcolor: 'rgba(255, 255, 255, 0.1)',
                  transform: 'rotate(90deg)',
                }
              }}
            >
              <CloseIcon />
            </IconButton>
          </DialogTitle>

          <DialogContent sx={{ p: 3 }}>
            <Box sx={{ mb: 2 }}>
              <Typography
                variant="body2"
                sx={{
                  color: '#94a3b8',
                  mb: 2,
                  lineHeight: 1.6,
                }}
              >
                Enter the duration in minutes for your custom timer session.
              </Typography>
              <TextField
                autoFocus
                fullWidth
                type="number"
                label="Duration (minutes)"
                value={customMinutes}
                onChange={(e) => {
                  const value = e.target.value;
                  setCustomMinutes(value);
                  setCustomTimerError('');
                  
                  // Validate input
                  if (value && (isNaN(Number(value)) || Number(value) <= 0)) {
                    setCustomTimerError('Please enter a valid positive number');
                  } else if (value && Number(value) > 1440) {
                    setCustomTimerError('Maximum duration is 1440 minutes (24 hours)');
                  } else {
                    setCustomTimerError('');
                  }
                }}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && !customTimerError && customMinutes) {
                    handleCustomTimerSubmit();
                  }
                }}
                error={!!customTimerError}
                helperText={customTimerError || 'Enter a value between 1 and 1440 minutes'}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    color: '#ffffff',
                    bgcolor: 'rgba(15, 23, 42, 0.5)',
                    borderRadius: 2,
                    '& fieldset': {
                      borderColor: 'rgba(148, 163, 184, 0.2)',
                    },
                    '&:hover fieldset': {
                      borderColor: 'rgba(148, 163, 184, 0.4)',
                    },
                    '&.Mui-focused fieldset': {
                      borderColor: '#6366f1',
                      borderWidth: '2px',
                    },
                  },
                  '& .MuiInputLabel-root': {
                    color: '#94a3b8',
                    '&.Mui-focused': {
                      color: '#6366f1',
                    },
                  },
                  '& .MuiFormHelperText-root': {
                    color: customTimerError ? '#ef4444' : '#64748b',
                  },
                }}
                inputProps={{
                  min: 1,
                  max: 1440,
                  step: 1,
                }}
              />
            </Box>

            {/* Quick Preset Buttons */}
            <Box sx={{ mt: 3 }}>
              <Typography
                variant="caption"
                sx={{
                  color: '#64748b',
                  mb: 1.5,
                  display: 'block',
                  fontWeight: 500,
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px',
                  fontSize: '0.75rem',
                }}
              >
                Quick Select
              </Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                {[15, 30, 45, 60, 90, 120].map((mins) => (
                  <Button
                    key={mins}
                    onClick={() => {
                      setCustomMinutes(mins.toString());
                      setCustomTimerError('');
                    }}
                    sx={{
                      borderRadius: 2,
                      px: 2,
                      py: 0.75,
                      minWidth: 60,
                      fontSize: '0.875rem',
                      fontWeight: 500,
                      textTransform: 'none',
                      border: '1px solid rgba(148, 163, 184, 0.2)',
                      color: customMinutes === mins.toString() ? '#ffffff' : '#94a3b8',
                      bgcolor: customMinutes === mins.toString()
                        ? 'rgba(99, 102, 241, 0.2)'
                        : 'rgba(15, 23, 42, 0.5)',
                      transition: 'all 0.2s ease',
                      '&:hover': {
                        borderColor: 'rgba(99, 102, 241, 0.5)',
                        bgcolor: 'rgba(99, 102, 241, 0.15)',
                        color: '#ffffff',
                        transform: 'translateY(-1px)',
                      },
                    }}
                  >
                    {mins} min
                  </Button>
                ))}
              </Box>
            </Box>
          </DialogContent>

          <DialogActions
            sx={{
              p: 3,
              pt: 2,
              gap: 1.5,
              borderTop: '1px solid rgba(148, 163, 184, 0.1)',
              background: 'rgba(15, 23, 42, 0.5)',
            }}
          >
            <Button
              onClick={() => {
                setCustomTimerOpen(false);
                setCustomMinutes('');
                setCustomTimerError('');
              }}
              sx={{
                borderRadius: 2.5,
                px: 3,
                py: 1,
                textTransform: 'none',
                fontWeight: 500,
                borderColor: 'rgba(148, 163, 184, 0.3)',
                color: '#cbd5e1',
                transition: 'all 0.2s ease',
                '&:hover': {
                  borderColor: 'rgba(148, 163, 184, 0.5)',
                  bgcolor: 'rgba(148, 163, 184, 0.1)',
                },
              }}
              variant="outlined"
            >
              Cancel
            </Button>
            <Button
              onClick={handleCustomTimerSubmit}
              disabled={!!customTimerError || !customMinutes || Number(customMinutes) <= 0}
              sx={{
                borderRadius: 2.5,
                px: 4,
                py: 1,
                textTransform: 'none',
                fontWeight: 600,
                background: 'linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)',
                color: '#ffffff',
                boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)',
                transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #4f46e5 0%, #4338ca 100%)',
                  boxShadow: '0 6px 16px rgba(99, 102, 241, 0.4)',
                  transform: 'translateY(-2px)',
                },
                '&:disabled': {
                  background: 'rgba(148, 163, 184, 0.1)',
                  color: 'rgba(148, 163, 184, 0.5)',
                  boxShadow: 'none',
                },
              }}
              variant="contained"
            >
              Set Timer
            </Button>
          </DialogActions>
        </Dialog>
      </Paper>
    </Fade>
  );
};

export default PomodoroTimer;
