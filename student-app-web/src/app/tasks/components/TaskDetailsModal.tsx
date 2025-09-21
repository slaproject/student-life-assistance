"use client";
import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  Typography,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  Stack,
  IconButton,
  Divider,
  Alert,
  CircularProgress
} from '@mui/material';
import {
  Close as CloseIcon,
  Flag as FlagIcon,
  Schedule as ScheduleIcon,
  Description as DescriptionIcon,
  Label as LabelIcon,
  Save as SaveIcon,
  Delete as DeleteIcon
} from '@mui/icons-material';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';
import { Task, TaskColumn, CreateTaskRequest, Priority, priorityColors, priorityLabels } from '../../types/task';
import { taskService } from '../../lib/taskService';

interface TaskDetailsModalProps {
  open: boolean;
  onClose: () => void;
  task?: Task | null;
  columns: TaskColumn[];
  defaultColumnId?: string;
  onTaskSave?: (task: Task) => void;
  onTaskDelete?: (taskId: string) => void;
}

export default function TaskDetailsModal({
  open,
  onClose,
  task,
  columns,
  defaultColumnId,
  onTaskSave,
  onTaskDelete
}: TaskDetailsModalProps) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM' as Priority,
    columnId: '',
    dueDate: null as Dayjs | null,
    tags: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [tagInput, setTagInput] = useState('');

  const isEditing = !!task;
  const modalTitle = isEditing ? 'Edit Task' : 'Create New Task';

  // Initialize form data when modal opens
  useEffect(() => {
    if (open) {
      if (task) {
        // Editing existing task
        setFormData({
          title: task.title,
          description: task.description || '',
          priority: task.priority,
          columnId: task.column.id,
          dueDate: task.dueDate ? dayjs(task.dueDate) : null,
          tags: task.tags || ''
        });
        setTagInput(task.tags || '');
      } else {
        // Creating new task
        setFormData({
          title: '',
          description: '',
          priority: 'MEDIUM',
          columnId: defaultColumnId || (columns.length > 0 ? columns[0].id : ''),
          dueDate: null,
          tags: ''
        });
        setTagInput('');
      }
      setError('');
    }
  }, [open, task, defaultColumnId, columns]);

  const handleInputChange = (field: keyof typeof formData) => (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement> | { target: { value: unknown } }
  ) => {
    const value = event.target ? event.target.value : event;
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleDateChange = (newValue: Dayjs | null) => {
    setFormData(prev => ({ ...prev, dueDate: newValue }));
  };

  const handleTagsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setTagInput(value);
    setFormData(prev => ({ ...prev, tags: value }));
  };

  const parseTags = (tags: string): string[] => {
    return tags.split(',').map(tag => tag.trim()).filter(Boolean);
  };

  const handleSave = async () => {
    if (!formData.title.trim()) {
      setError('Task title is required');
      return;
    }

    if (!formData.columnId) {
      setError('Please select a column');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const selectedColumn = columns.find(col => col.id === formData.columnId);
      if (!selectedColumn) {
        throw new Error('Invalid column selected');
      }

      if (isEditing && task) {
        // Update existing task
        const updatedTask = await taskService.updateTask(task.id, {
          title: formData.title.trim(),
          description: formData.description.trim() || undefined,
          priority: formData.priority,
          column: selectedColumn,
          dueDate: formData.dueDate ? formData.dueDate.toDate() : undefined,
          tags: formData.tags.trim() || undefined,
        });
        onTaskSave?.(updatedTask);
      } else {
        // Create new task
        const createRequest: CreateTaskRequest = {
          title: formData.title.trim(),
          description: formData.description.trim() || undefined,
          priority: formData.priority,
          column: { id: formData.columnId },
          dueDate: formData.dueDate ? formData.dueDate.toDate() : undefined,
          tags: formData.tags.trim() || undefined,
        };
        
        const newTask = await taskService.createTask(createRequest);
        onTaskSave?.(newTask);
      }

      handleClose();
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to save task';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!task) return;
    
    if (window.confirm('Are you sure you want to delete this task?')) {
      setLoading(true);
      try {
        await taskService.deleteTask(task.id);
        onTaskDelete?.(task.id);
        handleClose();
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to delete task';
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    }
  };

  const handleClose = () => {
    setFormData({
      title: '',
      description: '',
      priority: 'MEDIUM',
      columnId: '',
      dueDate: null,
      tags: ''
    });
    setTagInput('');
    setError('');
    onClose();
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Dialog 
        open={open} 
        onClose={handleClose}
        maxWidth="md"
        fullWidth
        PaperProps={{
          sx: {
            borderRadius: 4,
            minHeight: 600
          }
        }}
      >
        <DialogTitle sx={{ p: 3, pb: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h5" sx={{ fontWeight: 700, color: 'text.primary' }}>
              {modalTitle}
            </Typography>
            <IconButton onClick={handleClose} size="small">
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>

        <DialogContent sx={{ p: 3 }}>
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          <Stack spacing={3}>
            {/* Title */}
            <TextField
              label="Task Title"
              value={formData.title}
              onChange={handleInputChange('title')}
              fullWidth
              required
              variant="outlined"
              InputProps={{
                startAdornment: <DescriptionIcon sx={{ mr: 1, color: 'text.secondary' }} />
              }}
            />

            {/* Description */}
            <TextField
              label="Description"
              value={formData.description}
              onChange={handleInputChange('description')}
              fullWidth
              multiline
              rows={3}
              variant="outlined"
              placeholder="Add a detailed description of the task..."
            />

            {/* Priority and Column Row */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <FormControl fullWidth>
                <InputLabel>Priority</InputLabel>
                <Select
                  value={formData.priority}
                  onChange={handleInputChange('priority')}
                  label="Priority"
                  startAdornment={
                    <FlagIcon sx={{ color: priorityColors[formData.priority], mr: 1 }} />
                  }
                >
                  {(['HIGH', 'MEDIUM', 'LOW'] as Priority[]).map(priority => (
                    <MenuItem key={priority} value={priority}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <FlagIcon sx={{ color: priorityColors[priority], mr: 1, fontSize: 20 }} />
                        {priorityLabels[priority]}
                      </Box>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <FormControl fullWidth>
                <InputLabel>Column</InputLabel>
                <Select
                  value={formData.columnId}
                  onChange={handleInputChange('columnId')}
                  label="Column"
                >
                  {columns.map(column => (
                    <MenuItem key={column.id} value={column.id}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Box
                          sx={{
                            width: 12,
                            height: 12,
                            borderRadius: '50%',
                            bgcolor: column.color || '#667eea',
                            mr: 1
                          }}
                        />
                        {column.title}
                      </Box>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Stack>

            {/* Due Date */}
            <DateTimePicker
              label="Due Date"
              value={formData.dueDate}
              onChange={handleDateChange}
              slotProps={{
                textField: {
                  fullWidth: true,
                  InputProps: {
                    startAdornment: <ScheduleIcon sx={{ mr: 1, color: 'text.secondary' }} />
                  }
                }
              }}
            />

            {/* Tags */}
            <Box>
              <TextField
                label="Tags"
                value={tagInput}
                onChange={handleTagsChange}
                fullWidth
                placeholder="Enter tags separated by commas (e.g., homework, urgent, math)"
                InputProps={{
                  startAdornment: <LabelIcon sx={{ mr: 1, color: 'text.secondary' }} />
                }}
              />
              {formData.tags && parseTags(formData.tags).length > 0 && (
                <Box sx={{ mt: 1 }}>
                  <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap>
                    {parseTags(formData.tags).map((tag, index) => (
                      <Chip
                        key={index}
                        label={tag}
                        size="small"
                        sx={{ 
                          bgcolor: 'primary.light',
                          color: 'primary.contrastText'
                        }}
                      />
                    ))}
                  </Stack>
                </Box>
              )}
            </Box>

            {/* Task Details for Editing */}
            {isEditing && task && (
              <>
                <Divider />
                <Box>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    <strong>Created:</strong> {new Date(task.createdAt).toLocaleDateString()} at {new Date(task.createdAt).toLocaleTimeString()}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Last Updated:</strong> {new Date(task.updatedAt).toLocaleDateString()} at {new Date(task.updatedAt).toLocaleTimeString()}
                  </Typography>
                </Box>
              </>
            )}
          </Stack>
        </DialogContent>

        <DialogActions sx={{ p: 3, pt: 1, justifyContent: 'space-between' }}>
          <Box>
            {isEditing && (
              <Button
                onClick={handleDelete}
                color="error"
                startIcon={<DeleteIcon />}
                disabled={loading}
              >
                Delete Task
              </Button>
            )}
          </Box>
          
          <Stack direction="row" spacing={2}>
            <Button onClick={handleClose} disabled={loading}>
              Cancel
            </Button>
            <Button
              onClick={handleSave}
              variant="contained"
              startIcon={loading ? <CircularProgress size={16} /> : <SaveIcon />}
              disabled={loading || !formData.title.trim()}
              sx={{ minWidth: 120 }}
            >
              {loading ? 'Saving...' : isEditing ? 'Update Task' : 'Create Task'}
            </Button>
          </Stack>
        </DialogActions>
      </Dialog>
    </LocalizationProvider>
  );
}