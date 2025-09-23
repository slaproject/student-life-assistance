"use client";
import React, { useState, useEffect, useCallback } from 'react';
import { Droppable } from '@hello-pangea/dnd';
import {
  Box,
  Typography,
  Button,
  CircularProgress,
  Alert,
  Fab,
  Stack,
  TextField,
  InputAdornment,
  Tooltip,
  Paper
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Refresh as RefreshIcon
} from '@mui/icons-material';
import { Task, TaskColumn } from '../../types/task';
import { taskService } from '../../lib/taskService';
import { useDragAndDrop } from '../../hooks/useDragAndDrop';
import Column from './Column';
import TaskDetailsModal from './TaskDetailsModal';
import ColumnModal from './ColumnModal';

export default function TaskBoard() {
  const [columns, setColumns] = useState<TaskColumn[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [taskModalOpen, setTaskModalOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [defaultColumnId, setDefaultColumnId] = useState<string>('');
  const [columnModalOpen, setColumnModalOpen] = useState(false);
  const [selectedColumn, setSelectedColumn] = useState<TaskColumn | null>(null);

  // Load data from API
  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      
      // Fetch data from API with added error handling
      let [columnsData, tasksData] = await Promise.all([
        taskService.getColumns(),
        taskService.getTasks()
      ]);
      
      // Add defensive checks to ensure data is in expected format
      if (!Array.isArray(columnsData)) {
        console.error('Invalid columnsData format:', columnsData);
        columnsData = [];
        setError('Invalid task column data received. Please try again.');
      }
      
      if (!Array.isArray(tasksData)) {
        console.error('Invalid tasksData format:', tasksData);
        tasksData = [];
        setError(prev => prev ? `${prev} Invalid task data received.` : 'Invalid task data received. Please try again.');
      }
      
      // Organize tasks by column with added safety checks
      const columnsWithTasks = columnsData.map(column => ({
        ...column,
        tasks: tasksData.filter(task => task?.column?.id === column.id)
          .sort((a, b) => (a.position || 0) - (b.position || 0))
      }));

      setColumns(columnsWithTasks);
    } catch (err) {
      console.error('Failed to load data:', err);
      setError('Failed to load tasks. Please try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  // Initialize data on component mount
  useEffect(() => {
    loadData();
  }, [loadData]);

  // Drag and drop integration
  const {
    columns: dndColumns,
    isDragging,
    handleDragStart,
    handleDragEnd,
    updateColumns,
    addTaskToColumn,
    removeTaskFromColumn,
    updateTaskInColumn,
    DragDropContext
  } = useDragAndDrop(
    columns,
    (task: Task) => {
      updateTaskInColumn(task);
      // Refresh data to ensure consistency
      loadData();
    },
    (error: string) => {
      setError(error);
    }
  );

  // Update drag and drop columns when columns change
  useEffect(() => {
    updateColumns(columns);
  }, [columns, updateColumns]);

  // Get tasks for a specific column
  const getTasksForColumn = useCallback((columnId: string): Task[] => {
    const column = dndColumns.find(col => col.id === columnId);
    return column?.tasks || [];
  }, [dndColumns]);

  // Filter tasks based on search query
  const filterTasks = useCallback((tasks: Task[]): Task[] => {
    if (!searchQuery.trim()) return tasks;
    
    const query = searchQuery.toLowerCase();
    return tasks.filter(task => 
      (task.title && task.title.toLowerCase().includes(query)) ||
      (task.description && task.description.toLowerCase().includes(query)) ||
      (task.tags && task.tags.toLowerCase().includes(query))
    );
  }, [searchQuery]);

  // Handle task actions
  const handleAddTask = useCallback((columnId?: string) => {
    setDefaultColumnId(columnId || (columns.length > 0 ? columns[0].id : ''));
    setSelectedTask(null);
    setTaskModalOpen(true);
  }, [columns]);

  const handleTaskClick = useCallback((task: Task) => {
    setSelectedTask(task);
    setTaskModalOpen(true);
  }, []);

  const handleTaskEdit = useCallback((task: Task) => {
    setSelectedTask(task);
    setTaskModalOpen(true);
  }, []);

  const handleTaskDelete = useCallback(async (taskId: string) => {
    try {
      await taskService.deleteTask(taskId);
      removeTaskFromColumn(taskId);
      await loadData(); // Refresh data
    } catch {
      setError('Failed to delete task');
    }
  }, [removeTaskFromColumn, loadData]);

  // Handle column actions
  const handleAddColumn = useCallback(() => {
    setSelectedColumn(null);
    setColumnModalOpen(true);
  }, []);

  const handleColumnEdit = useCallback((column: TaskColumn) => {
    setSelectedColumn(column);
    setColumnModalOpen(true);
  }, []);

  const handleColumnSave = useCallback(async (column: TaskColumn) => {
    try {
      if (selectedColumn) {
        // Update existing column
        await taskService.updateColumn(column.id, column);
      } else {
        // Create new column  
        await taskService.createColumn(column);
      }
      await loadData(); // Refresh data
      setColumnModalOpen(false);
      setSelectedColumn(null);
    } catch (err) {
      console.error('Error saving column:', err);
      setError('Failed to save column');
    }
  }, [selectedColumn, loadData]);

  const handleColumnDelete = useCallback(async (columnId: string) => {
    try {
      await taskService.deleteColumn(columnId);
      await loadData(); // Refresh data
      setColumnModalOpen(false);
      setSelectedColumn(null);
    } catch (err) {
      console.error('Error deleting column:', err);
      setError('Failed to delete column');
    }
  }, [loadData]);

  const handleTaskSave = useCallback(async (task: Task) => {
    try {
      if (selectedTask) {
        // Update existing task
        updateTaskInColumn(task);
      } else {
        // Add new task
        addTaskToColumn(task.column.id, task);
      }
      await loadData(); // Refresh data to ensure consistency
    } catch (err) {
      console.error('Error saving task:', err);
    }
  }, [selectedTask, updateTaskInColumn, addTaskToColumn, loadData]);

  const handleSearch = async (query: string) => {
    if (query.trim()) {
      try {
        const searchResults = await taskService.searchTasks(query);
        // Update columns with filtered tasks
        const filteredColumns = columns.map(column => ({
          ...column,
          tasks: searchResults.filter(task => task?.column?.id === column.id)
        }));
        updateColumns(filteredColumns);
      } catch (err) {
        console.error('Search failed:', err);
      }
    } else {
      // Reset to original data
      loadData();
    }
  };

  if (loading) {
    return (
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center',
        minHeight: 400,
        flexDirection: 'column',
        gap: 2
      }}>
        <CircularProgress size={48} />
        <Typography variant="body1" color="text.secondary">
          Loading your tasks...
        </Typography>
      </Box>
    );
  }

  return (
    <Box sx={{ 
      height: 'calc(100vh - 120px)', 
      bgcolor: '#f8fafc',
      display: 'flex',
      flexDirection: 'column'
    }}>
      {/* Header */}
      <Box sx={{ 
        p: 3, 
        bgcolor: 'background.paper',
        borderBottom: '1px solid',
        borderColor: 'divider',
        boxShadow: '0 2px 8px rgba(0,0,0,0.04)'
      }}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={2}>
          <Box>
            <Typography variant="h4" sx={{ fontWeight: 700, color: 'text.primary', mb: 1 }}>
              My Tasks
            </Typography>
          </Box>
          
          <Stack direction="row" spacing={2} alignItems="center">
            {/* Search */}
            <TextField
              placeholder="Search tasks..."
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                handleSearch(e.target.value);
              }}
              size="small"
              sx={{ minWidth: 250 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon sx={{ color: 'text.secondary' }} />
                  </InputAdornment>
                ),
              }}
            />

            {/* Action Buttons */}
            <Tooltip title="Refresh">
              <Button
                variant="outlined"
                onClick={loadData}
                startIcon={<RefreshIcon />}
                sx={{ minWidth: 'auto', px: 2 }}
              >
                Refresh
              </Button>
            </Tooltip>

            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => handleAddTask()}
              sx={{
                bgcolor: 'primary.main',
                '&:hover': { bgcolor: 'primary.dark' },
                borderRadius: 2,
                textTransform: 'none',
                fontWeight: 600
              }}
            >
              Add Task
            </Button>

            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => handleAddColumn()}
              sx={{
                bgcolor: 'primary.main',
                '&:hover': { bgcolor: 'primary.dark' },
                borderRadius: 2,
                textTransform: 'none',
                fontWeight: 600
              }}
            >
              Add Column
            </Button>
          </Stack>
        </Stack>
      </Box>

      {/* Error Alert */}
      {error && (
        <Alert 
          severity="error" 
          sx={{ m: 2, mb: 0 }}
          onClose={() => setError('')}
        >
          {error}
        </Alert>
      )}

      {/* Board */}
      <Box sx={{ flex: 1, overflow: 'hidden', p: 3 }}>
        <DragDropContext 
          onDragStart={handleDragStart} 
          onDragEnd={handleDragEnd}
        >
          <Droppable droppableId="board" type="COLUMN" direction="horizontal">
            {(provided) => (
              <Box
                ref={provided.innerRef}
                {...provided.droppableProps}
                sx={{
                  display: 'flex',
                  gap: 3,
                  height: '100%',
                  overflowX: 'auto',
                  overflowY: 'hidden',
                  pb: 2,
                  '&::-webkit-scrollbar': {
                    height: 8
                  },
                  '&::-webkit-scrollbar-thumb': {
                    backgroundColor: 'rgba(0,0,0,0.2)',
                    borderRadius: 4
                  },
                  '&::-webkit-scrollbar-track': {
                    backgroundColor: 'rgba(0,0,0,0.05)',
                    borderRadius: 4
                  }
                }}
              >
                {dndColumns.map((column, index) => (
                  <Column
                    key={column.id}
                    column={column}
                    index={index}
                    tasks={filterTasks(getTasksForColumn(column.id))}
                    onAddTask={handleAddTask}
                    onTaskClick={handleTaskClick}
                    onTaskEdit={handleTaskEdit}
                    onTaskDelete={handleTaskDelete}
                    onColumnEdit={handleColumnEdit}
                    isDragging={isDragging}
                  />
                ))}
                
                {/* Add Column Button */}
                <Paper
                  sx={{
                    width: 320,
                    minWidth: 320,
                    maxWidth: 320,
                    height: 200,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    borderRadius: 4,
                    border: '2px dashed',
                    borderColor: 'primary.light',
                    bgcolor: 'rgba(63, 81, 181, 0.05)',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                    '&:hover': {
                      borderColor: 'primary.main',
                      bgcolor: 'rgba(63, 81, 181, 0.1)',
                      transform: 'translateY(-2px)',
                      boxShadow: '0 4px 20px rgba(63, 81, 181, 0.15)'
                    }
                  }}
                  onClick={handleAddColumn}
                >
                  <Box sx={{ textAlign: 'center', color: 'primary.main' }}>
                    <AddIcon sx={{ fontSize: 48, mb: 1, opacity: 0.7 }} />
                    <Typography variant="h6" sx={{ fontWeight: 600 }}>
                      Add Column
                    </Typography>
                    <Typography variant="body2" sx={{ opacity: 0.7, mt: 0.5 }}>
                      Create new column
                    </Typography>
                  </Box>
                </Paper>
                
                {provided.placeholder}
              </Box>
            )}
          </Droppable>
        </DragDropContext>
      </Box>

      {/* Floating Action Button */}
      <Fab
        color="primary"
        aria-label="add task"
        onClick={() => handleAddTask()}
        sx={{
          position: 'fixed',
          bottom: 32,
          right: 32,
          bgcolor: 'primary.main',
          '&:hover': { bgcolor: 'primary.dark' }
        }}
      >
        <AddIcon />
      </Fab>

      {/* Task Details Modal */}
      <TaskDetailsModal
        open={taskModalOpen}
        onClose={() => {
          setTaskModalOpen(false);
          setSelectedTask(null);
          setDefaultColumnId('');
        }}
        task={selectedTask}
        columns={columns}
        defaultColumnId={defaultColumnId}
        onTaskSave={handleTaskSave}
        onTaskDelete={handleTaskDelete}
      />

      {/* Column Management Modal */}
      <ColumnModal
        open={columnModalOpen}
        onClose={() => {
          setColumnModalOpen(false);
          setSelectedColumn(null);
        }}
        column={selectedColumn}
        onSave={handleColumnSave}
        onDelete={handleColumnDelete}
      />
    </Box>
  );
}