"use client";
import { useState, useCallback } from 'react';
import { DragDropContext, DropResult } from '@hello-pangea/dnd';
import { Task, TaskColumn } from '../types/task';
import { taskService } from '../lib/taskService';

export const useDragAndDrop = (
  initialColumns: TaskColumn[], 
  onTaskUpdate?: (task: Task) => void,
  onError?: (error: string) => void
) => {
  const [columns, setColumns] = useState<TaskColumn[]>(initialColumns);
  const [isDragging, setIsDragging] = useState(false);

  // Helper function to get all tasks organized by column
  const getTasksByColumn = useCallback((columns: TaskColumn[]): Record<string, Task[]> => {
    return columns.reduce((acc, column) => {
      acc[column.id] = column.tasks || [];
      return acc;
    }, {} as Record<string, Task[]>);
  }, []);

  // Handle drag start
  const handleDragStart = useCallback(() => {
    setIsDragging(true);
  }, []);

  // Handle drag end
  const handleDragEnd = useCallback(async (result: DropResult) => {
    setIsDragging(false);
    
    const { destination, source, draggableId, type } = result;

    // No destination means dropped outside
    if (!destination) return;

    // Same position, no change
    if (destination.droppableId === source.droppableId && destination.index === source.index) {
      return;
    }

    if (type === 'COLUMN') {
      // Handle column reordering
      const newColumns = Array.from(columns);
      const [movedColumn] = newColumns.splice(source.index, 1);
      newColumns.splice(destination.index, 0, movedColumn);

      // Update positions
      const updatedColumns = newColumns.map((column, index) => ({
        ...column,
        position: index,
      }));

      setColumns(updatedColumns);

      try {
        // Update column position in backend
        await taskService.updateColumnPosition(movedColumn.id, destination.index);
      } catch (error) {
        // Revert on error
        setColumns(columns);
        onError?.('Failed to update column position');
      }
      return;
    }

    // Handle task movement
    const sourceColumnId = source.droppableId;
    const destColumnId = destination.droppableId;
    
    const tasksByColumn = getTasksByColumn(columns);
    const sourceTasks = [...tasksByColumn[sourceColumnId]];
    const destTasks = sourceColumnId === destColumnId ? sourceTasks : [...tasksByColumn[destColumnId]];

    // Find the moved task
    const [movedTask] = sourceTasks.splice(source.index, 1);
    
    // Update task column if moved to different column
    const updatedTask = {
      ...movedTask,
      column: columns.find(col => col.id === destColumnId)!,
      position: destination.index,
    };

    // Add task to destination
    destTasks.splice(destination.index, 0, updatedTask);

    // Update columns with new task positions
    const newColumns = columns.map(column => {
      if (column.id === sourceColumnId) {
        return {
          ...column,
          tasks: sourceTasks.map((task, index) => ({ ...task, position: index }))
        };
      }
      if (column.id === destColumnId) {
        return {
          ...column,
          tasks: destTasks.map((task, index) => ({ ...task, position: index }))
        };
      }
      return column;
    });

    // Optimistic update
    setColumns(newColumns);

    try {
      // Update task position in backend
      const backendUpdatedTask = await taskService.updateTaskPosition(
        movedTask.id,
        destColumnId,
        destination.index
      );
      
      onTaskUpdate?.(backendUpdatedTask);
    } catch (error) {
      // Revert on error
      setColumns(columns);
      onError?.('Failed to move task');
      console.error('Failed to move task:', error);
    }
  }, [columns, getTasksByColumn, onTaskUpdate, onError]);

  // Update columns data
  const updateColumns = useCallback((newColumns: TaskColumn[]) => {
    setColumns(newColumns);
  }, []);

  // Add task to column
  const addTaskToColumn = useCallback((columnId: string, task: Task) => {
    setColumns(prevColumns => 
      prevColumns.map(column => 
        column.id === columnId 
          ? { ...column, tasks: [...(column.tasks || []), task] }
          : column
      )
    );
  }, []);

  // Remove task from columns
  const removeTaskFromColumn = useCallback((taskId: string) => {
    setColumns(prevColumns => 
      prevColumns.map(column => ({
        ...column,
        tasks: (column.tasks || []).filter(task => task.id !== taskId)
      }))
    );
  }, []);

  // Update task in columns
  const updateTaskInColumn = useCallback((updatedTask: Task) => {
    setColumns(prevColumns => 
      prevColumns.map(column => ({
        ...column,
        tasks: (column.tasks || []).map(task => 
          task.id === updatedTask.id ? updatedTask : task
        )
      }))
    );
  }, []);

  return {
    columns,
    isDragging,
    handleDragStart,
    handleDragEnd,
    updateColumns,
    addTaskToColumn,
    removeTaskFromColumn,
    updateTaskInColumn,
    DragDropContext,
  };
};