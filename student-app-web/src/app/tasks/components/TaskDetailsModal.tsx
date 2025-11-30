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

  // Helper function to get column color
  const getColumnHeaderColor = (title: string) => {
    if (!title) return '#667eea';
    switch (title.toLowerCase()) {
      case 'to do':
      case 'todo':
        return '#667eea';
      case 'in progress':
      case 'inprogress':
        return '#f093fb';
      case 'review':
        return '#4facfe';
      case 'done':
      case 'completed':
        return '#43e97b';
      default:
        return '#667eea';
    }
  };

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
            minHeight: 600,
            bgcolor: '#1e293b',
            color: '#ffffff',
            border: '1px solid #334155'
          }
        }}
      >
        <DialogTitle sx={{ p: 3, pb: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h5" sx={{ fontWeight: 700, color: '#ffffff' }}>
              {modalTitle}
            </Typography>
            <IconButton onClick={handleClose} size="small" sx={{ color: '#94a3b8', '&:hover': { color: '#ffffff', bgcolor: 'rgba(255,255,255,0.1)' } }}>
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
              InputProps={{
                startAdornment: <DescriptionIcon sx={{ mr: 1, color: '#94a3b8' }} />
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

            {/* Priority and Column Row */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <FormControl fullWidth>
                <InputLabel sx={{ color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }}>Priority</InputLabel>
                <Select
                  value={formData.priority}
                  onChange={handleInputChange('priority')}
                  label="Priority"
                  sx={{
                    color: '#ffffff',
                    bgcolor: 'rgba(255,255,255,0.05)',
                    '& .MuiOutlinedInput-notchedOutline': { borderColor: '#334155' },
                    '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#475569' },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' },
                    '& .MuiSvgIcon-root': { color: '#94a3b8' }
                  }}
                  renderValue={(value) => {
                    const priority = value as Priority;
                    return (
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                        <Box
                          sx={{
                            width: 16,
                            height: 16,
                            borderRadius: '50%',
                            bgcolor: priorityColors[priority],
                            boxShadow: `0 2px 8px ${priorityColors[priority]}60`,
                            border: `2px solid ${priorityColors[priority]}`,
                          }}
                        />
                        <Typography sx={{ fontWeight: 600, color: '#ffffff' }}>
                          {priorityLabels[priority]}
                        </Typography>
                      </Box>
                    );
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
                  {(['HIGH', 'MEDIUM', 'LOW'] as Priority[]).map(priority => (
                    <MenuItem 
                      key={priority} 
                      value={priority}
                      sx={{
                        '&.Mui-selected': {
                          bgcolor: `${priorityColors[priority]}20`,
                          '&:hover': {
                            bgcolor: `${priorityColors[priority]}30`,
                          }
                        }
                      }}
                    >
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                        <Box
                          sx={{
                            width: 16,
                            height: 16,
                            borderRadius: '50%',
                            bgcolor: priorityColors[priority],
                            boxShadow: `0 2px 8px ${priorityColors[priority]}60`,
                            border: `2px solid ${priorityColors[priority]}`,
                          }}
                        />
                        <Typography sx={{ fontWeight: 600, color: '#ffffff' }}>
                          {priorityLabels[priority]}
                        </Typography>
                      </Box>
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <FormControl fullWidth>
                <InputLabel sx={{ color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }}>Column</InputLabel>
                <Select
                  value={formData.columnId}
                  onChange={handleInputChange('columnId')}
                  label="Column"
                  sx={{
                    color: '#ffffff',
                    bgcolor: 'rgba(255,255,255,0.05)',
                    '& .MuiOutlinedInput-notchedOutline': { borderColor: '#334155' },
                    '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#475569' },
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' },
                    '& .MuiSvgIcon-root': { color: '#94a3b8' }
                  }}
                  renderValue={(value) => {
                    const selectedColumn = columns.find(col => col.id === value);
                    if (!selectedColumn) return '';
                    const columnColor = selectedColumn.color || getColumnHeaderColor(selectedColumn.title);
                    return (
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                        <Box
                          sx={{
                            width: 18,
                            height: 18,
                            borderRadius: '50%',
                            bgcolor: columnColor,
                            boxShadow: `0 2px 12px ${columnColor}80, 0 0 0 2px ${columnColor}40`,
                            border: `2px solid ${columnColor}`,
                          }}
                        />
                        <Typography sx={{ fontWeight: 600, color: '#ffffff' }}>
                          {selectedColumn.title}
                        </Typography>
                      </Box>
                    );
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
                  {columns.map(column => {
                    const columnColor = column.color || getColumnHeaderColor(column.title);
                    return (
                      <MenuItem 
                        key={column.id} 
                        value={column.id}
                        selected={formData.columnId === column.id}
                        sx={{
                          '&.Mui-selected': {
                            bgcolor: `${columnColor}20`,
                            '&:hover': {
                              bgcolor: `${columnColor}30`,
                            }
                          }
                        }}
                      >
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
                          <Box
                            sx={{
                              width: 18,
                              height: 18,
                              borderRadius: '50%',
                              bgcolor: columnColor,
                              boxShadow: `0 2px 12px ${columnColor}80, 0 0 0 2px ${columnColor}40`,
                              border: `2px solid ${columnColor}`,
                            }}
                          />
                          <Typography sx={{ fontWeight: 600, color: '#ffffff' }}>
                            {column.title}
                          </Typography>
                        </Box>
                      </MenuItem>
                    );
                  })}
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
                  sx: {
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& fieldset': { borderColor: '#334155' },
                      '&:hover fieldset': { borderColor: '#475569' },
                      '&.Mui-focused fieldset': { borderColor: '#3b82f6', borderWidth: '2px' }
                    },
                    '& .MuiInputLabel-root': { color: '#94a3b8' },
                    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' },
                    '& .MuiSvgIcon-root': { color: '#94a3b8' }
                  },
                  InputProps: {
                    startAdornment: <ScheduleIcon sx={{ mr: 1, color: '#3b82f6' }} />
                  }
                },
                popper: {
                  sx: {
                    '& .MuiPaper-root': {
                      bgcolor: '#0f172a',
                      color: '#ffffff',
                      border: '1px solid #334155',
                      borderRadius: 3,
                      boxShadow: '0 8px 32px rgba(0,0,0,0.4)',
                      backdropFilter: 'blur(12px)',
                      '& .MuiPickersCalendarHeader-root': {
                        color: '#ffffff',
                        '& .MuiPickersCalendarHeader-label': {
                          color: '#ffffff',
                          fontWeight: 600,
                        },
                        '& .MuiIconButton-root': {
                          color: '#94a3b8',
                          '&:hover': {
                            bgcolor: 'rgba(59, 130, 246, 0.2)',
                            color: '#3b82f6'
                          }
                        }
                      },
                      '& .MuiDayCalendar-weekContainer': {
                        '& .MuiTypography-root': {
                          color: '#94a3b8',
                          fontWeight: 500,
                        }
                      },
                      '& .MuiPickersDay-root': {
                        color: '#ffffff',
                        fontWeight: 500,
                        '&:hover': {
                          bgcolor: 'rgba(59, 130, 246, 0.2)',
                          color: '#3b82f6'
                        },
                        '&.Mui-selected': {
                          bgcolor: '#3b82f6',
                          color: '#ffffff',
                          fontWeight: 700,
                          '&:hover': {
                            bgcolor: '#2563eb'
                          }
                        },
                        '&.MuiPickersDay-today': {
                          border: '1px solid #3b82f6',
                          fontWeight: 700
                        }
                      },
                      '& .MuiPickersTimeClock-root': {
                        '& .MuiClock-root': {
                          backgroundColor: 'rgba(15, 23, 42, 0.8)',
                        },
                        '& .MuiClockNumber-root': {
                          color: '#ffffff',
                          '&.Mui-selected': {
                            color: '#3b82f6',
                            fontWeight: 700
                          }
                        },
                        '& .MuiClockPointer-root': {
                          backgroundColor: '#3b82f6',
                        },
                        '& .MuiClock-pin': {
                          backgroundColor: '#3b82f6',
                        }
                      },
                      '& .MuiTimeClock-root': {
                        '& .MuiClock-root': {
                          backgroundColor: 'rgba(15, 23, 42, 0.8)',
                        }
                      },
                      '& .MuiPickersTimeClockTime-root': {
                        '& .MuiTypography-root': {
                          color: '#ffffff',
                          '&.Mui-selected': {
                            color: '#3b82f6',
                            fontWeight: 700,
                            bgcolor: 'rgba(59, 130, 246, 0.2)'
                          }
                        }
                      },
                      '& .MuiPickersArrowSwitcher-root': {
                        '& .MuiIconButton-root': {
                          color: '#94a3b8',
                          '&:hover': {
                            bgcolor: 'rgba(59, 130, 246, 0.2)',
                            color: '#3b82f6'
                          }
                        }
                      }
                    }
                  }
                },
                layout: {
                  sx: {
                    '& .MuiPickersLayout-root': {
                      bgcolor: '#0f172a',
                    }
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
                InputProps={{
                  startAdornment: <LabelIcon sx={{ mr: 1, color: '#94a3b8' }} />
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
                          bgcolor: 'rgba(59, 130, 246, 0.1)',
                          color: '#60a5fa'
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
                <Divider sx={{ borderColor: '#334155' }} />
                <Box>
                  <Typography variant="body2" sx={{ color: '#94a3b8' }} gutterBottom>
                    <strong>Created:</strong> {new Date(task.createdAt).toLocaleDateString()} at {new Date(task.createdAt).toLocaleTimeString()}
                  </Typography>
                  <Typography variant="body2" sx={{ color: '#94a3b8' }}>
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
            <Button onClick={handleClose} disabled={loading} sx={{ color: '#94a3b8', '&:hover': { color: '#ffffff', bgcolor: 'rgba(255,255,255,0.05)' } }}>
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