"use client";
import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  Stack,
  IconButton
} from '@mui/material';
import { Close as CloseIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { TaskColumn } from '../../types/task';

interface ColumnModalProps {
  open: boolean;
  onClose: () => void;
  column: TaskColumn | null;
  onSave: (column: TaskColumn) => void;
  onDelete?: (columnId: string) => void;
}

const colorOptions = [
  { label: 'Blue', value: '#667eea' },
  { label: 'Purple', value: '#764ba2' },
  { label: 'Green', value: '#43e97b' },
  { label: 'Orange', value: '#fa709a' },
  { label: 'Red', value: '#ff6b6b' },
  { label: 'Teal', value: '#4facfe' },
  { label: 'Pink', value: '#f093fb' },
  { label: 'Cyan', value: '#00d4ff' },
];

export default function ColumnModal({
  open,
  onClose,
  column,
  onSave,
  onDelete
}: ColumnModalProps) {
  const [title, setTitle] = useState('');
  const [color, setColor] = useState('#667eea');
  const [position, setPosition] = useState(0);
  const [error, setError] = useState('');

  const isEdit = column !== null;

  useEffect(() => {
    if (column) {
      setTitle(column.title);
      setColor(column.color || '#667eea');
      setPosition(column.position);
    } else {
      setTitle('');
      setColor('#667eea');
      setPosition(0);
    }
    setError('');
  }, [column, open]);

  const handleSave = () => {
    if (!title.trim()) {
      setError('Column title is required');
      return;
    }

    const columnData: TaskColumn = {
      id: column?.id || '',
      userId: column?.userId || '',
      title: title.trim(),
      color,
      position,
      createdAt: column?.createdAt || new Date(),
      updatedAt: new Date(),
    };

    onSave(columnData);
    onClose();
  };

  const handleDelete = () => {
    if (column && onDelete) {
      onDelete(column.id);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3,
          overflow: 'hidden',
          bgcolor: '#1e293b',
          color: '#ffffff',
          border: '1px solid #334155'
        }
      }}
    >
      <DialogTitle
        sx={{
          bgcolor: '#0f172a',
          color: 'white',
          py: 2,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          borderBottom: '1px solid #334155'
        }}
      >
        <Typography variant="subtitle1" fontWeight={600} sx={{ fontSize: '1.25rem' }}>
          {isEdit ? 'Edit Column' : 'Create New Column'}
        </Typography>
        <IconButton
          onClick={onClose}
          size="small"
          sx={{ color: 'rgba(255,255,255,0.5)', '&:hover': { color: '#ffffff' } }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent sx={{ p: 3 }}>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Stack spacing={3}>
          <TextField
            label="Column Title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            fullWidth
            required
            placeholder="e.g., To Do, In Progress, Done"
            variant="outlined"
            sx={{
              '& .MuiOutlinedInput-root': {
                color: '#ffffff',
                bgcolor: 'rgba(255,255,255,0.05)',
                '& fieldset': { borderColor: '#334155' },
                '&:hover fieldset': { borderColor: '#475569' },
                '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
              },
              '& .MuiInputLabel-root': { color: '#94a3b8' },
              '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
            }}
          />

          <FormControl fullWidth>
            <InputLabel sx={{ color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }}>Color</InputLabel>
            <Select
              value={color}
              onChange={(e) => setColor(e.target.value)}
              label="Color"
              sx={{
                color: '#ffffff',
                bgcolor: 'rgba(255,255,255,0.05)',
                '& .MuiOutlinedInput-notchedOutline': { borderColor: '#334155' },
                '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#475569' },
                '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' },
                '& .MuiSvgIcon-root': { color: '#94a3b8' }
              }}
              MenuProps={{
                PaperProps: {
                  sx: {
                    bgcolor: '#1e293b',
                    border: '1px solid #334155',
                    '& .MuiMenuItem-root': {
                      color: '#ffffff',
                      '&:hover': { bgcolor: 'rgba(255,255,255,0.05)' },
                      '&.Mui-selected': { bgcolor: 'rgba(59, 130, 246, 0.2)' }
                    }
                  }
                }
              }}
            >
              {colorOptions.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Box
                      sx={{
                        width: 20,
                        height: 20,
                        borderRadius: '50%',
                        bgcolor: option.value,
                        border: '1px solid rgba(255,255,255,0.2)'
                      }}
                    />
                    {option.label}
                  </Box>
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="Position"
            type="number"
            value={position}
            onChange={(e) => setPosition(parseInt(e.target.value) || 0)}
            fullWidth
            helperText="Position where this column will be inserted. Existing columns will shift right automatically."
            variant="outlined"
            sx={{
              '& .MuiOutlinedInput-root': {
                color: '#ffffff',
                bgcolor: 'rgba(255,255,255,0.05)',
                '& fieldset': { borderColor: '#334155' },
                '&:hover fieldset': { borderColor: '#475569' },
                '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
              },
              '& .MuiInputLabel-root': { color: '#94a3b8' },
              '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' },
              '& .MuiFormHelperText-root': { color: '#64748b' }
            }}
          />

          {/* Preview */}
          <Box sx={{ p: 2, border: '1px dashed #334155', borderRadius: 2, bgcolor: 'rgba(0,0,0,0.2)' }}>
            <Typography variant="body2" sx={{ mb: 1, color: '#94a3b8' }}>
              Preview:
            </Typography>
            <Box
              sx={{
                p: 2,
                borderRadius: 2,
                background: `linear-gradient(135deg, ${color}15, ${color}08)`,
                border: `2px solid ${color}`,
                display: 'flex',
                alignItems: 'center',
                gap: 1
              }}
            >
              <Typography
                variant="subtitle1"
                sx={{ color: color, fontWeight: 700, fontSize: '1.1rem' }}
              >
                {title || 'Column Title'}
              </Typography>
            </Box>
          </Box>
        </Stack>
      </DialogContent>

      <DialogActions sx={{ p: 3, gap: 1, borderTop: '1px solid #334155' }}>
        {isEdit && onDelete && (
          <Button
            onClick={handleDelete}
            color="error"
            variant="outlined"
            startIcon={<DeleteIcon />}
            sx={{ mr: 'auto', borderColor: 'error.main', color: 'error.main' }}
          >
            Delete Column
          </Button>
        )}

        <Button
          onClick={onClose}
          variant="outlined"
          sx={{ color: '#94a3b8', borderColor: '#334155', '&:hover': { borderColor: '#94a3b8', color: '#ffffff' } }}
        >
          Cancel
        </Button>

        <Button
          onClick={handleSave}
          color="primary"
          variant="contained"
        >
          {isEdit ? 'Update Column' : 'Create Column'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}