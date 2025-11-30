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

export default function TaskCard({ task, index, onClick, onEdit }: TaskCardProps) {
  const isOverdue = task.dueDate ? isPast(new Date(task.dueDate)) : false;
  const isDueSoon = task.dueDate ?
    isAfter(new Date(task.dueDate), new Date()) &&
    isAfter(new Date(Date.now() + 3 * 24 * 60 * 60 * 1000), new Date(task.dueDate))
    : false;

  const handleEditClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    onEdit?.(task);
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
      {(provided, snapshot) => {
        // Ensure the drag preview follows the cursor correctly
        // Use the library's style directly to maintain proper cursor tracking
        const dragStyle = provided.draggableProps.style;

        return (
          <Card
            ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}
            onClick={(e) => {
              // Only trigger click if we're not currently dragging
              // The drag library will handle the drag, we just need to not interfere
              if (!snapshot.isDragging && !snapshot.isDropAnimating) {
                onClick?.(task);
              }
            }}
            style={dragStyle}
            sx={{
              mb: 2,
              cursor: snapshot.isDragging ? 'grabbing' : 'grab',
              transition: snapshot.isDragging ? 'none' : 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              boxShadow: snapshot.isDragging
                ? '0 20px 60px rgba(0,0,0,0.6), 0 0 0 1px rgba(59, 130, 246, 0.3)'
                : '0 2px 8px rgba(0,0,0,0.15), 0 1px 3px rgba(0,0,0,0.1)',
              border: '1px solid',
              borderColor: snapshot.isDragging
                ? 'rgba(59, 130, 246, 0.5)'
                : 'rgba(30, 41, 59, 0.6)',
              borderRadius: 3,
              bgcolor: snapshot.isDragging 
                ? 'rgba(30, 41, 59, 0.95)' 
                : 'rgba(15, 23, 42, 0.8)',
              backdropFilter: snapshot.isDragging ? 'blur(8px)' : 'blur(4px)',
              // Ensure proper z-index during drag
              zIndex: snapshot.isDragging ? 9999 : 'auto',
              '&:hover': !snapshot.isDragging ? {
                boxShadow: '0 8px 24px rgba(0,0,0,0.3), 0 2px 8px rgba(59, 130, 246, 0.2)',
                transform: 'translateY(-4px)',
                borderColor: 'rgba(59, 130, 246, 0.4)',
                bgcolor: 'rgba(15, 23, 42, 0.95)',
              } : {},
              '&:active': !snapshot.isDragging ? {
                transform: 'translateY(-2px)',
              } : {},
            // Priority indicator bar with gradient - make it more visible
            borderLeft: `5px solid ${priorityColors[task.priority]}`,
            position: 'relative',
            overflow: 'hidden',
            '&::before': {
              content: '""',
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              height: '3px',
              background: `linear-gradient(90deg, ${priorityColors[task.priority]}, ${priorityColors[task.priority]}80, transparent)`,
              opacity: 0.8,
            },
            }}
          >
          <CardContent sx={{ p: 2, '&:last-child': { pb: 2 } }}>
            {/* Header with title and menu */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1.5 }}>
              <Typography
                variant="body1"
                sx={{
                  fontWeight: 700,
                  color: '#ffffff',
                  flex: 1,
                  fontSize: '1rem',
                  lineHeight: 1.4,
                  pr: 1,
                  letterSpacing: '-0.2px',
                }}
              >
                {task.title}
              </Typography>
              <IconButton
                size="small"
                onClick={handleEditClick}
                onMouseDown={(e) => e.stopPropagation()}
                onPointerDown={(e) => e.stopPropagation()}
                sx={{
                  opacity: 0.5,
                  color: 'rgba(148, 163, 184, 0.8)',
                  '&:hover': { 
                    opacity: 1, 
                    color: '#ffffff',
                    bgcolor: 'rgba(255, 255, 255, 0.1)',
                  },
                  p: 0.5,
                  transition: 'all 0.2s ease',
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
                  color: 'rgba(148, 163, 184, 0.9)',
                  mb: 2,
                  fontSize: '0.875rem',
                  lineHeight: 1.5,
                  display: '-webkit-box',
                  WebkitLineClamp: 2,
                  WebkitBoxOrient: 'vertical',
                  overflow: 'hidden',
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
                        height: 24,
                        bgcolor: 'rgba(59, 130, 246, 0.15)',
                        color: '#60a5fa',
                        fontWeight: 600,
                        border: '1px solid rgba(59, 130, 246, 0.2)',
                        '& .MuiChip-label': {
                          px: 1.5
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
                        bgcolor: 'rgba(255,255,255,0.05)',
                        color: '#94a3b8'
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
                      height: 26,
                      fontWeight: 700,
                      boxShadow: `0 2px 8px ${priorityColors[task.priority]}60`,
                      border: `1px solid ${priorityColors[task.priority]}`,
                      '& .MuiChip-icon': {
                        color: 'white',
                        fontSize: '14px'
                      }
                    }}
                  />
                </Tooltip>

                {task.attachments && task.attachments.length > 0 && (
                  <Tooltip title={`${task.attachments.length} attachment${task.attachments.length > 1 ? 's' : ''}`}>
                    <Box sx={{ display: 'flex', alignItems: 'center', color: '#94a3b8' }}>
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
                            : 'rgba(255,255,255,0.05)',
                        color: isOverdue || isDueSoon ? 'white' : '#94a3b8',
                        fontSize: '0.7rem',
                        height: 22,
                        '& .MuiChip-icon': {
                          color: isOverdue || isDueSoon ? 'white' : '#94a3b8',
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
            <Box sx={{ mt: 1.5, display: 'flex', alignItems: 'center', color: 'rgba(100, 116, 139, 0.6)' }}>
              <AccessTimeIcon sx={{ fontSize: 12, mr: 0.5, opacity: 0.7 }} />
              <Typography variant="caption" sx={{ fontSize: '0.7rem', opacity: 0.8 }}>
                Created {formatDistanceToNow(new Date(task.createdAt), { addSuffix: true })}
              </Typography>
            </Box>
          </CardContent>
        </Card>
        );
      }}
    </Draggable>
  );
}