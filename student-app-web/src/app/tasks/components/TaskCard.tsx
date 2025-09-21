"use client";
import React from 'react';
import { Draggable } from '@hello-pangea/dnd';
import {
  Card,
  CardContent,
  Typography,
  Chip,
  Box,
  IconButton,
  Avatar,
  Tooltip,
  Stack
} from '@mui/material';
import {
  Flag as FlagIcon,
  Schedule as ScheduleIcon,
  Attachment as AttachmentIcon,
  MoreVert as MoreVertIcon,
  Person as PersonIcon,
  AccessTime as AccessTimeIcon
} from '@mui/icons-material';
import { Task, priorityColors, priorityLabels } from '../../types/task';
import { formatDistanceToNow, isAfter, isPast, format } from 'date-fns';

interface TaskCardProps {
  task: Task;
  index: number;
  onClick?: (task: Task) => void;
  onEdit?: (task: Task) => void;
  onDelete?: (taskId: string) => void;
}

export default function TaskCard({ task, index, onClick, onEdit, onDelete }: TaskCardProps) {
  const isOverdue = task.dueDate ? isPast(new Date(task.dueDate)) : false;
  const isDueSoon = task.dueDate ? 
    isAfter(new Date(task.dueDate), new Date()) && 
    isAfter(new Date(Date.now() + 3 * 24 * 60 * 60 * 1000), new Date(task.dueDate)) 
    : false;

  const handleCardClick = (e: React.MouseEvent) => {
    e.preventDefault();
    onClick?.(task);
  };

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(task);
  };

  const handleDeleteClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onDelete?.(task.id);
  };

  const parseTags = (tags?: string): string[] => {
    if (!tags) return [];
    return tags.split(',').map(tag => tag.trim()).filter(Boolean);
  };

  const formatDueDate = (date: Date): string => {
    const now = new Date();
    const diffInHours = Math.abs(date.getTime() - now.getTime()) / (1000 * 60 * 60);
    
    if (diffInHours < 24) {
      return `Due ${formatDistanceToNow(date, { addSuffix: true })}`;
    }
    return `Due ${format(date, 'MMM d, yyyy')}`;
  };

  return (
    <Draggable draggableId={task.id} index={index}>
      {(provided, snapshot) => (
        <Card
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          onClick={handleCardClick}
          style={provided.draggableProps.style}
          sx={{
            mb: 2,
            cursor: 'pointer',
            transition: snapshot.isDragging ? 'none' : 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
            transform: snapshot.isDragging 
              ? `${provided.draggableProps.style?.transform || ''} rotate(5deg)`.trim()
              : 'rotate(0deg)',
            boxShadow: snapshot.isDragging 
              ? '0 8px 32px rgba(0,0,0,0.15)' 
              : '0 1px 3px rgba(0,0,0,0.08)',
            border: '1px solid',
            borderColor: snapshot.isDragging 
              ? 'primary.main' 
              : 'rgba(0,0,0,0.05)',
            borderRadius: 3,
            bgcolor: snapshot.isDragging ? 'background.paper' : 'background.default',
            '&:hover': !snapshot.isDragging ? {
              boxShadow: '0 4px 20px rgba(0,0,0,0.12)',
              transform: 'translateY(-2px)',
              borderColor: 'primary.light',
            } : {},
            '&:active': !snapshot.isDragging ? {
              transform: 'translateY(0px)',
            } : {},
            // Priority indicator bar
            borderLeft: `4px solid ${priorityColors[task.priority]}`,
          }}
        >
          <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
            {/* Header with title and menu */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
              <Typography 
                variant="body1" 
                sx={{ 
                  fontWeight: 600, 
                  color: 'text.primary',
                  flex: 1,
                  fontSize: '0.95rem',
                  lineHeight: 1.3,
                  pr: 1
                }}
              >
                {task.title}
              </Typography>
              <IconButton 
                size="small" 
                onClick={handleEditClick}
                sx={{ 
                  opacity: 0.6, 
                  '&:hover': { opacity: 1 },
                  p: 0.5 
                }}
              >
                <MoreVertIcon fontSize="small" />
              </IconButton>
            </Box>

            {/* Description */}
            {task.description && (
              <Typography 
                variant="body2" 
                sx={{ 
                  color: 'text.secondary', 
                  mb: 2,
                  fontSize: '0.85rem',
                  lineHeight: 1.4,
                  display: '-webkit-box',
                  WebkitLineClamp: 2,
                  WebkitBoxOrient: 'vertical',
                  overflow: 'hidden'
                }}
              >
                {task.description}
              </Typography>
            )}

            {/* Tags */}
            {task.tags && parseTags(task.tags).length > 0 && (
              <Box sx={{ mb: 2 }}>
                <Stack direction="row" spacing={0.5} flexWrap="wrap" useFlexGap>
                  {parseTags(task.tags).slice(0, 3).map((tag, index) => (
                    <Chip
                      key={index}
                      label={tag}
                      size="small"
                      sx={{ 
                        fontSize: '0.7rem', 
                        height: 22,
                        bgcolor: 'rgba(102, 126, 234, 0.1)',
                        color: 'primary.main',
                        fontWeight: 500,
                        '& .MuiChip-label': {
                          px: 1
                        }
                      }}
                    />
                  ))}
                  {parseTags(task.tags).length > 3 && (
                    <Chip
                      label={`+${parseTags(task.tags).length - 3}`}
                      size="small"
                      sx={{ 
                        fontSize: '0.7rem', 
                        height: 22,
                        bgcolor: 'rgba(0,0,0,0.08)',
                        color: 'text.secondary'
                      }}
                    />
                  )}
                </Stack>
              </Box>
            )}

            {/* Footer with priority, due date, and metadata */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2 }}>
              {/* Left side - Priority and attachments */}
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Tooltip title={`${priorityLabels[task.priority]} Priority`}>
                  <Chip
                    icon={<FlagIcon sx={{ fontSize: '14px !important' }} />}
                    label={priorityLabels[task.priority]}
                    size="small"
                    sx={{
                      bgcolor: priorityColors[task.priority],
                      color: 'white',
                      fontSize: '0.7rem',
                      height: 24,
                      fontWeight: 600,
                      '& .MuiChip-icon': {
                        color: 'white',
                        fontSize: '14px'
                      }
                    }}
                  />
                </Tooltip>
                
                {task.attachments && task.attachments.length > 0 && (
                  <Tooltip title={`${task.attachments.length} attachment${task.attachments.length > 1 ? 's' : ''}`}>
                    <Box sx={{ display: 'flex', alignItems: 'center', color: 'text.secondary' }}>
                      <AttachmentIcon sx={{ fontSize: 16, mr: 0.5 }} />
                      <Typography variant="caption">{task.attachments.length}</Typography>
                    </Box>
                  </Tooltip>
                )}
              </Box>

              {/* Right side - Due date and assignee */}
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                {task.dueDate && (
                  <Tooltip title={formatDueDate(new Date(task.dueDate))}>
                    <Chip
                      icon={<ScheduleIcon sx={{ fontSize: '12px !important' }} />}
                      label={format(new Date(task.dueDate), 'MMM d')}
                      size="small"
                      sx={{
                        bgcolor: isOverdue 
                          ? 'error.main' 
                          : isDueSoon 
                          ? 'warning.main' 
                          : 'rgba(0,0,0,0.08)',
                        color: isOverdue || isDueSoon ? 'white' : 'text.secondary',
                        fontSize: '0.7rem',
                        height: 22,
                        '& .MuiChip-icon': {
                          color: isOverdue || isDueSoon ? 'white' : 'text.secondary',
                          fontSize: '12px'
                        }
                      }}
                    />
                  </Tooltip>
                )}

                {task.assignedTo && (
                  <Tooltip title="Assigned user">
                    <Avatar sx={{ width: 24, height: 24, bgcolor: 'primary.main' }}>
                      <PersonIcon sx={{ fontSize: 14 }} />
                    </Avatar>
                  </Tooltip>
                )}
              </Box>
            </Box>

            {/* Created time */}
            <Box sx={{ mt: 1.5, display: 'flex', alignItems: 'center', color: 'text.disabled' }}>
              <AccessTimeIcon sx={{ fontSize: 12, mr: 0.5 }} />
              <Typography variant="caption" sx={{ fontSize: '0.7rem' }}>
                Created {formatDistanceToNow(new Date(task.createdAt), { addSuffix: true })}
              </Typography>
            </Box>
          </CardContent>
        </Card>
      )}
    </Draggable>
  );
}