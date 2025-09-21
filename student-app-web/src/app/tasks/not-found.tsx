import { Box, Typography, Button } from '@mui/material';
import Link from 'next/link';
import { Assignment as AssignmentIcon } from '@mui/icons-material';

export default function TasksNotFound() {
  return (
    <Box sx={{
      minHeight: '60vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      textAlign: 'center',
      px: 3
    }}>
      <AssignmentIcon sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
      <Typography variant="h4" gutterBottom>
        Page Not Found
      </Typography>
      <Typography variant="body1" color="text.secondary" sx={{ mb: 3, maxWidth: 400 }}>
        The task page you&apos;re looking for doesn&apos;t exist.
      </Typography>
      <Button
        component={Link}
        href="/tasks"
        variant="contained"
        size="large"
      >
        Go to Tasks
      </Button>
    </Box>
  );
}