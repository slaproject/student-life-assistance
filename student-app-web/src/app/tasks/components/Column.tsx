"use client";
import React from 'react';
import { Droppable, Draggable } from '@hello-pangea/dnd';
import {
  Paper,
  Typography,
  Box,
  Chip,
  IconButton,
  Divider,
  Tooltip,
  Stack
} from '@mui/material';
import {
  Add as AddIcon,
  MoreHoriz as MoreHorizIcon,
  DragIndicator as DragIndicatorIcon
} from '@mui/icons-material';
import { TaskColumn, Task } from '../../types/task';
import TaskCard from './TaskCard';

interface ColumnProps {
  column: TaskColumn;
  index: number;
  tasks: Task[];
  onAddTask?: (columnId: string) => void;
  onTaskClick?: (task: Task) => void;
  onTaskEdit?: (task: Task) => void;
  onTaskDelete?: (taskId: string) => void;
  onColumnEdit?: (column: TaskColumn) => void;
  isDragging?: boolean;
}

export default function Column({
  column,
  index,
  tasks,
  onAddTask,
  onTaskClick,
  onTaskEdit,
  onTaskDelete,
  onColumnEdit
}: ColumnProps) {
  const taskCount = tasks.length;

  const getColumnHeaderColor = (title: string) => {
    if (!title) return column.color || '#667eea';

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
        return column.color || '#667eea';
    }
  };

  const headerColor = getColumnHeaderColor(column.title);

  return (
    <Draggable draggableId={`column-${column.id}`} index={index}>
      {(provided, snapshot) => {
        // Use the library's style directly to maintain proper cursor tracking
        const dragStyle = provided.draggableProps.style;

        return (
          <Paper
            ref={provided.innerRef}
            {...provided.draggableProps}
            elevation={0}
            style={dragStyle}
            sx={{
              width: 320,
              minWidth: 320,
              maxWidth: 320,
              height: 'fit-content',
              minHeight: 500,
              maxHeight: 'calc(100vh - 200px)',
              display: 'flex',
              flexDirection: 'column',
              borderRadius: 4,
              transition: snapshot.isDragging ? 'none' : 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              bgcolor: 'rgba(10, 10, 10, 0.85)',
              backdropFilter: 'blur(12px)',
              border: '1px solid',
              borderColor: snapshot.isDragging 
                ? `rgba(59, 130, 246, 0.5)` 
                : 'rgba(30, 41, 59, 0.4)',
              boxShadow: snapshot.isDragging
                ? '0 20px 60px rgba(0,0,0,0.6), 0 0 0 1px rgba(59, 130, 246, 0.3)'
                : '0 4px 16px rgba(0,0,0,0.2), 0 1px 4px rgba(0,0,0,0.1)',
              '&:hover': !snapshot.isDragging ? {
                boxShadow: '0 8px 32px rgba(0,0,0,0.3), 0 2px 8px rgba(59, 130, 246, 0.15)',
                borderColor: 'rgba(59, 130, 246, 0.4)',
                transform: 'translateY(-2px)',
              } : {},
              position: 'relative',
              overflow: 'hidden',
              // Ensure proper z-index during drag
              zIndex: snapshot.isDragging ? 9999 : 'auto',
            }}
          >
          {/* Column Header */}
          <Box
            {...provided.dragHandleProps}
            sx={{
              p: 2.5,
              background: `linear-gradient(135deg, ${headerColor}30, ${headerColor}15)`,
              borderBottom: `3px solid ${headerColor}`,
              borderRadius: '16px 16px 0 0',
              cursor: snapshot.isDragging ? 'grabbing' : 'grab',
              position: 'relative',
              '&::before': {
                content: '""',
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                height: '4px',
                background: `linear-gradient(90deg, ${headerColor}, ${headerColor}90, ${headerColor}60, transparent)`,
                borderRadius: '16px 16px 0 0',
              },
              '&:hover': {
                background: `linear-gradient(135deg, ${headerColor}40, ${headerColor}20)`,
                borderBottomColor: `${headerColor}`,
              },
              transition: 'all 0.2s ease',
            }}
          >
            <Stack direction="row" alignItems="center" justifyContent="space-between">
              <Stack direction="row" alignItems="center" spacing={1}>
                <DragIndicatorIcon
                  sx={{
                    color: headerColor,
                    opacity: 0.7,
                    fontSize: 20
                  }}
                />
                <Typography
                  variant="h6"
                  sx={{
                    fontWeight: 700,
                    color: headerColor,
                    fontSize: '1.1rem',
                    letterSpacing: '-0.5px',
                    textShadow: `0 0 12px ${headerColor}60, 0 2px 4px rgba(0,0,0,0.3)`,
                  }}
                >
                  {column.title}
                </Typography>
                <Chip
                  label={taskCount}
                  size="small"
                  sx={{
                    bgcolor: headerColor,
                    color: 'white',
                    fontWeight: 700,
                    fontSize: '0.75rem',
                    height: 26,
                    minWidth: 28,
                    boxShadow: `0 2px 12px ${headerColor}80, 0 0 0 1px ${headerColor}40`,
                    border: `1px solid ${headerColor}`,
                  }}
                />
              </Stack>

              <Stack direction="row" spacing={0.5}>
                <Tooltip title="Add new task">
                  <IconButton
                    size="small"
                    onClick={() => onAddTask?.(column.id)}
                    sx={{
                      color: headerColor,
                      '&:hover': {
                        bgcolor: `${headerColor}20`
                      }
                    }}
                  >
                    <AddIcon fontSize="small" />
                  </IconButton>
                </Tooltip>

                <Tooltip title="Column options">
                  <IconButton
                    size="small"
                    onClick={() => onColumnEdit?.(column)}
                    sx={{
                      color: headerColor,
                      opacity: 0.7,
                      '&:hover': {
                        bgcolor: `${headerColor}20`,
                        opacity: 1
                      }
                    }}
                  >
                    <MoreHorizIcon fontSize="small" />
                  </IconButton>
                </Tooltip>
              </Stack>
            </Stack>
          </Box>

          {/* Tasks Container */}
          <Droppable droppableId={column.id} type="TASK">
            {(provided, snapshot) => (
              <Box
                ref={provided.innerRef}
                {...provided.droppableProps}
                sx={{
                  flex: 1,
                  p: 2.5,
                  minHeight: 400,
                  maxHeight: 'calc(100vh - 300px)',
                  overflowY: 'auto',
                  overflowX: 'hidden',
                  bgcolor: snapshot.isDraggingOver
                    ? `${headerColor}12`
                    : 'transparent',
                  borderRadius: snapshot.isDraggingOver ? 2 : 0,
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  border: snapshot.isDraggingOver
                    ? `2px dashed ${headerColor}80`
                    : '2px solid transparent',
                  position: 'relative',
                  '&::before': snapshot.isDraggingOver ? {
                    content: '""',
                    position: 'absolute',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    background: `radial-gradient(circle at center, ${headerColor}10, transparent)`,
                    pointerEvents: 'none',
                  } : {},
                  '&::-webkit-scrollbar': {
                    width: 8,
                  },
                  '&::-webkit-scrollbar-thumb': {
                    backgroundColor: 'rgba(59, 130, 246, 0.3)',
                    borderRadius: 4,
                    border: '2px solid transparent',
                    backgroundClip: 'padding-box',
                    '&:hover': {
                      backgroundColor: 'rgba(59, 130, 246, 0.5)',
                    },
                  },
                  '&::-webkit-scrollbar-track': {
                    backgroundColor: 'rgba(255,255,255,0.02)',
                    borderRadius: 4,
                  }
                }}
              >
                {tasks.length === 0 ? (
                  <Box
                    sx={{
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center',
                      justifyContent: 'center',
                      py: 8,
                      color: 'rgba(148, 163, 184, 0.5)',
                      border: '2px dashed rgba(148, 163, 184, 0.2)',
                      borderRadius: 2,
                      mx: 1,
                    }}
                  >
                    <Typography variant="body2" sx={{ mb: 1, fontWeight: 600, color: 'rgba(148, 163, 184, 0.7)' }}>
                      No tasks yet
                    </Typography>
                    <Typography variant="caption" sx={{ textAlign: 'center', px: 2, color: 'rgba(148, 163, 184, 0.5)' }}>
                      Drag tasks here or click the + button to add new tasks
                    </Typography>
                  </Box>
                ) : (
                  tasks.map((task, index) => (
                    <TaskCard
                      key={task.id}
                      task={task}
                      index={index}
                      onClick={onTaskClick}
                      onEdit={onTaskEdit}
                      onDelete={onTaskDelete}
                    />
                  ))
                )}
                {provided.placeholder}
              </Box>
            )}
          </Droppable>
        </Paper>
        );
      }}
    </Draggable>
  );
}