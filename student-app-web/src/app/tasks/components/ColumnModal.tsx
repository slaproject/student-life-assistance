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
        sx: { borderRadius: 3, overflow: 'hidden' }
      }}
    >
      <DialogTitle
        sx={{
          bgcolor: 'primary.main',
          color: 'white',
          py: 2,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}
      >
        <Typography variant="subtitle1" fontWeight={600} sx={{ fontSize: '1.25rem' }}>
          {isEdit ? 'Edit Column' : 'Create New Column'}
        </Typography>
        <IconButton
          onClick={onClose}
          size="small"
          sx={{ color: 'rgba(255,255,255,0.8)' }}
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
          />

          <FormControl fullWidth>
            <InputLabel>Color</InputLabel>
            <Select
              value={color}
              onChange={(e) => setColor(e.target.value)}
              label="Color"
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
                        border: '1px solid rgba(0,0,0,0.1)'
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
          />

          {/* Preview */}
          <Box sx={{ p: 2, border: '1px dashed rgba(0,0,0,0.2)', borderRadius: 2 }}>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
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

      <DialogActions sx={{ p: 3, gap: 1 }}>
        {isEdit && onDelete && (
          <Button
            onClick={handleDelete}
            color="error"
            variant="outlined"
            startIcon={<DeleteIcon />}
            sx={{ mr: 'auto' }}
          >
            Delete Column
          </Button>
        )}
        
        <Button
          onClick={onClose}
          color="inherit"
          variant="outlined"
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