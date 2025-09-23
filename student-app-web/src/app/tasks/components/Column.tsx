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
      {(provided, snapshot) => (
        <Paper
          ref={provided.innerRef}
          {...provided.draggableProps}
          elevation={snapshot.isDragging ? 8 : 2}
          style={provided.draggableProps.style}
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
            transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            transform: snapshot.isDragging 
              ? `${provided.draggableProps.style?.transform || ''} rotate(2deg)`.trim()
              : 'rotate(0deg)',
            bgcolor: 'background.paper',
            border: '1px solid',
            borderColor: snapshot.isDragging ? 'primary.main' : 'rgba(0,0,0,0.08)',
            boxShadow: snapshot.isDragging 
              ? '0 12px 40px rgba(0,0,0,0.15)' 
              : '0 2px 12px rgba(0,0,0,0.08)',
            '&:hover': {
              boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
              borderColor: 'primary.light'
            }
          }}
        >
          {/* Column Header */}
          <Box
            {...provided.dragHandleProps}
            sx={{
              p: 2,
              background: `linear-gradient(135deg, ${headerColor}15, ${headerColor}08)`,
              borderBottom: `3px solid ${headerColor}`,
              borderRadius: '16px 16px 0 0',
              cursor: snapshot.isDragging ? 'grabbing' : 'grab',
              '&:hover': {
                background: `linear-gradient(135deg, ${headerColor}20, ${headerColor}10)`
              }
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
                    letterSpacing: '-0.5px'
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
                    fontWeight: 600,
                    fontSize: '0.75rem',
                    height: 24,
                    minWidth: 24
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
                  p: 2,
                  minHeight: 400,
                  maxHeight: 'calc(100vh - 300px)',
                  overflowY: 'auto',
                  bgcolor: snapshot.isDraggingOver 
                    ? `${headerColor}08` 
                    : 'transparent',
                  borderRadius: snapshot.isDraggingOver ? 2 : 0,
                  transition: 'all 0.2s ease',
                  border: snapshot.isDraggingOver 
                    ? `2px dashed ${headerColor}60` 
                    : '2px solid transparent',
                  '&::-webkit-scrollbar': {
                    width: 6
                  },
                  '&::-webkit-scrollbar-thumb': {
                    backgroundColor: `${headerColor}40`,
                    borderRadius: 3
                  },
                  '&::-webkit-scrollbar-track': {
                    backgroundColor: 'rgba(0,0,0,0.05)',
                    borderRadius: 3
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
                      py: 6,
                      color: 'text.disabled'
                    }}
                  >
                    <Typography variant="body2" sx={{ mb: 1, fontWeight: 500 }}>
                      No tasks yet
                    </Typography>
                    <Typography variant="caption" sx={{ textAlign: 'center', px: 2 }}>
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

          {/* Column Footer */}
          {taskCount > 0 && (
            <>
              <Divider sx={{ borderColor: `${headerColor}20` }} />
              <Box sx={{ p: 1.5, bgcolor: `${headerColor}05` }}>
                <Stack direction="row" justifyContent="center" spacing={2}>
                  <Typography variant="caption" sx={{ color: 'text.secondary', fontSize: '0.75rem' }}>
                    {taskCount} task{taskCount !== 1 ? 's' : ''}
                  </Typography>
                  {column.title && column.title.toLowerCase() !== 'done' && (
                    <Typography variant="caption" sx={{ color: headerColor, fontSize: '0.75rem', fontWeight: 600 }}>
                      In progress
                    </Typography>
                  )}
                </Stack>
              </Box>
            </>
          )}
        </Paper>
      )}
    </Draggable>
  );
}