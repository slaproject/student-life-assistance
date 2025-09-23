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
  Tooltip
} from '@mui/material';
import { 
  PlayArrow,
  Pause,
  Refresh,
  AvTimer,
  Notifications,
  NotificationsOff
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
    if (isCompleted) return '#27ae60'; // Green
    if (isRunning) return '#e74c3c';   // Red
    if (timeLeft < totalTime) return '#f39c12'; // Orange
    return '#667eea';                  // Default blue
  };
  
  return (
    <Paper
      elevation={3}
      sx={{
        borderRadius: 4,
        overflow: 'hidden',
        width: '100%',
        maxWidth: { xs: '100%', sm: 500 },
        mx: 'auto',
        p: 0,
      }}
    >
      {/* Timer Header */}
      <Box
        sx={{
          p: 2,
          background: 'linear-gradient(90deg, #667eea, #764ba2)',
          color: 'white',
          textAlign: 'center',
          position: 'relative',
        }}
      >
        <Typography variant="h5" fontWeight="bold">
          Pomodoro Timer
        </Typography>
        <Typography variant="body2" sx={{ opacity: 0.9, textTransform: 'capitalize' }}>
          {timerMode.replace(/([A-Z])/g, ' $1').trim()} Mode
        </Typography>
        
        <IconButton 
          size="small" 
          onClick={toggleNotifications}
          sx={{ position: 'absolute', right: 8, top: 8, color: 'white' }}
        >
          {notificationsEnabled ? <Notifications /> : <NotificationsOff />}
        </IconButton>
      </Box>
      
      {/* Timer Display */}
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          py: 4,
          px: 2,
          position: 'relative',
        }}
      >
        <Box
          sx={{
            position: 'relative',
            width: 200,
            height: 200,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            mb: 3,
          }}
        >
          <CircularProgress
            variant="determinate"
            value={calculateProgress()}
            size={200}
            thickness={4}
            sx={{
              color: getTimerColor(),
              position: 'absolute',
              top: 0,
              left: 0,
              '& .MuiCircularProgress-circle': {
                strokeLinecap: 'round',
              },
            }}
          />
          <Typography 
            variant="h2" 
            className={`timer-display ${getTimerStatusClass()}`}
            sx={{
              fontWeight: 300,
              color: getTimerColor(),
              fontSize: { xs: '2rem', sm: '3rem' },
              transition: 'color 0.3s ease',
            }}
          >
            {formatTime(timeLeft)}
          </Typography>
        </Box>
        
        {/* Timer Controls */}
        <Stack direction="row" spacing={2} sx={{ mb: 3 }}>
          {isRunning ? (
            <Button
              variant="contained"
              startIcon={<Pause />}
              onClick={pauseTimer}
              color="warning"
              sx={{ borderRadius: 2 }}
            >
              Pause
            </Button>
          ) : (
            <Button
              variant="contained"
              startIcon={<PlayArrow />}
              onClick={startTimer}
              color="success"
              sx={{ borderRadius: 2 }}
            >
              Start
            </Button>
          )}
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={resetTimer}
            sx={{ borderRadius: 2 }}
          >
            Reset
          </Button>
          <Tooltip title="Custom Timer">
            <IconButton
              color="primary"
              onClick={() => {
                const mins = prompt("Enter custom time in minutes:", "25");
                if (mins && !isNaN(Number(mins)) && Number(mins) > 0) {
                  setTimerDuration('custom', parseInt(mins) * 60);
                }
              }}
            >
              <AvTimer />
            </IconButton>
          </Tooltip>
        </Stack>

        {/* Timer Preset Buttons */}
        <ButtonGroup
          variant="outlined"
          sx={{
            flexWrap: 'wrap',
            justifyContent: 'center',
            '& .MuiButton-root': {
              borderRadius: 1,
              m: 0.5,
              minWidth: { xs: 80, sm: 100 }
            }
          }}
        >
          <Button
            color={timerMode === 'pomodoro' ? 'primary' : 'inherit'}
            variant={timerMode === 'pomodoro' ? 'contained' : 'outlined'}
            onClick={() => setTimerDuration('pomodoro', POMODORO_PRESETS.pomodoro)}
          >
            25 min
          </Button>
          <Button
            color={timerMode === 'shortBreak' ? 'primary' : 'inherit'}
            variant={timerMode === 'shortBreak' ? 'contained' : 'outlined'}
            onClick={() => setTimerDuration('shortBreak', POMODORO_PRESETS.shortBreak)}
          >
            5 min
          </Button>
          <Button
            color={timerMode === 'longBreak' ? 'primary' : 'inherit'}
            variant={timerMode === 'longBreak' ? 'contained' : 'outlined'}
            onClick={() => setTimerDuration('longBreak', POMODORO_PRESETS.longBreak)}
          >
            15 min
          </Button>
          <Button
            color={timerMode === 'custom45' ? 'primary' : 'inherit'}
            variant={timerMode === 'custom45' ? 'contained' : 'outlined'}
            onClick={() => setTimerDuration('custom45', POMODORO_PRESETS.custom45)}
          >
            45 min
          </Button>
          <Button
            color={timerMode === 'custom60' ? 'primary' : 'inherit'}
            variant={timerMode === 'custom60' ? 'contained' : 'outlined'}
            onClick={() => setTimerDuration('custom60', POMODORO_PRESETS.custom60)}
          >
            60 min
          </Button>
        </ButtonGroup>
      </Box>

      {/* Audio for timer completion */}
      <audio ref={audioRef} preload="auto">
        <source src="/notification-sound.mp3" type="audio/mpeg" />
        Your browser does not support the audio element.
      </audio>
    </Paper>
  );
};

export default PomodoroTimer;
