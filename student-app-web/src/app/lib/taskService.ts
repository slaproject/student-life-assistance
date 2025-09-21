import { getApiClient } from './api';
import { Task, TaskColumn, TaskStats, CreateTaskRequest, Priority } from '../types/task';

// Interface for task data from API responses
interface TaskApiResponse {
  id: string;
  title: string;
  description?: string;
  priority: Priority;
  columnId: string;
  position: number;
  userId: string;
  createdAt: string;
  updatedAt: string;
  dueDate?: string;
}

class TaskService {
  private api = getApiClient();

  // Task Column operations
  async getColumns(): Promise<TaskColumn[]> {
    try {
      const response = await this.api.get('/api/tasks/columns');
      
      // Add defensive validation for the response
      if (!response.data) {
        console.error('Empty response received from getColumns API call');
        return [];
      }
      
      // Handle case where response is a JSON string instead of a parsed object
      if (typeof response.data === 'string') {
        try {
          console.log('Response data is a string, attempting to parse:', response.data.substring(0, 100) + '...');
          
          // Try to clean up malformed JSON
          let cleanedJsonString = response.data.trim();
          
          // Check for common JSON malformation patterns
          if (cleanedJsonString.includes('"}]}}]}}]}}]}}"')) {
            console.warn('Detected malformed JSON with extra closing braces, attempting to clean...');
            // Try to find the last valid JSON structure
            const lastValidBrace = cleanedJsonString.lastIndexOf('"}]');
            if (lastValidBrace !== -1) {
              cleanedJsonString = cleanedJsonString.substring(0, lastValidBrace + 3);
              console.log('Cleaned JSON string:', cleanedJsonString.substring(0, 100) + '...');
            }
          }
          
          const parsedData = JSON.parse(cleanedJsonString);
          
          // Check if the parsed result is an array
          if (Array.isArray(parsedData)) {
            console.log('Successfully parsed JSON string as array with', parsedData.length, 'items');
            return parsedData;
          }
          
          // Try to extract columns from nested structure
          if (Array.isArray(parsedData?.columns)) {
            console.log('Successfully extracted columns from nested structure with', parsedData.columns.length, 'items');
            return parsedData.columns;
          }
          
          console.error('Successfully parsed string but result is not an array:', parsedData);
          return [];
        } catch (parseError) {
          console.error('Failed to parse column data string:', parseError);
          console.error('Original malformed JSON (first 500 chars):', response.data.substring(0, 500));
          console.error('Parse error details:', parseError instanceof Error ? parseError.message : String(parseError));
          
          // Last resort: try to extract array-like content using regex
          try {
            const arrayMatch = response.data.match(/\[.*?\]/);
            if (arrayMatch) {
              console.log('Attempting regex-based JSON extraction...');
              const extractedData = JSON.parse(arrayMatch[0]);
              if (Array.isArray(extractedData)) {
                console.log('Successfully extracted array using regex with', extractedData.length, 'items');
                return extractedData;
              }
            }
          } catch (regexError) {
            console.error('Regex extraction also failed:', regexError);
          }
          
          return [];
        }
      }
      
      // Handle regular object (non-array) response
      if (!Array.isArray(response.data)) {
        console.error('Invalid column data format received:', typeof response.data);
        
        // Try to extract from a possible nested structure
        if (Array.isArray(response.data?.columns)) {
          return response.data.columns;
        }
        
        return [];
      }
      
      return response.data;
    } catch (error) {
      console.error('Error fetching columns:', error);
      throw error;
    }
  }

  async createColumn(column: Omit<TaskColumn, 'id' | 'userId' | 'createdAt' | 'updatedAt'>): Promise<TaskColumn> {
    const response = await this.api.post('/api/tasks/columns', column);
    return response.data;
  }

  async updateColumn(id: string, column: Partial<TaskColumn>): Promise<TaskColumn> {
    const response = await this.api.put(`/api/tasks/columns/${id}`, column);
    return response.data;
  }

  async updateColumnPosition(id: string, position: number): Promise<TaskColumn> {
    const response = await this.api.put(`/api/tasks/columns/${id}/position`, { position });
    return response.data;
  }

  async deleteColumn(id: string): Promise<void> {
    await this.api.delete(`/api/tasks/columns/${id}`);
  }

  // Task operations
  async getTasks(): Promise<Task[]> {
    try {
      const response = await this.api.get('/api/tasks');
      
      // Add defensive validation for the response
      if (!response.data) {
        console.error('Empty response received from getTasks API call');
        return [];
      }
      
      // Handle case where response is a JSON string instead of a parsed object
      if (typeof response.data === 'string') {
        try {
          console.log('Tasks response data is a string, attempting to parse:', response.data.substring(0, 100) + '...');
          
          // Try to clean up malformed JSON
          let cleanedJsonString = response.data.trim();
          
          // Check for common JSON malformation patterns
          if (cleanedJsonString.includes('"}]}}]}}]}}]}}"')) {
            console.warn('Detected malformed tasks JSON with extra closing braces, attempting to clean...');
            // Try to find the last valid JSON structure
            const lastValidBrace = cleanedJsonString.lastIndexOf('"}]');
            if (lastValidBrace !== -1) {
              cleanedJsonString = cleanedJsonString.substring(0, lastValidBrace + 3);
              console.log('Cleaned tasks JSON string:', cleanedJsonString.substring(0, 100) + '...');
            }
          }
          
          const parsedData = JSON.parse(cleanedJsonString);
          
          // Check if the parsed result is an array
          if (Array.isArray(parsedData)) {
            console.log('Successfully parsed tasks JSON string as array with', parsedData.length, 'items');
            return this.processTasksResponse(parsedData);
          }
          
          // Try to extract tasks from nested structure
          if (Array.isArray(parsedData?.tasks)) {
            console.log('Successfully extracted tasks from nested structure with', parsedData.tasks.length, 'items');
            return this.processTasksResponse(parsedData.tasks);
          }
          
          console.error('Successfully parsed tasks string but result is not an array:', parsedData);
          return [];
        } catch (parseError) {
          console.error('Failed to parse tasks data string:', parseError);
          console.error('Original malformed tasks JSON (first 500 chars):', response.data.substring(0, 500));
          console.error('Tasks parse error details:', parseError instanceof Error ? parseError.message : String(parseError));
          
          // Last resort: try to extract array-like content using regex
          try {
            const arrayMatch = response.data.match(/\[.*?\]/);
            if (arrayMatch) {
              console.log('Attempting regex-based tasks JSON extraction...');
              const extractedData = JSON.parse(arrayMatch[0]);
              if (Array.isArray(extractedData)) {
                console.log('Successfully extracted tasks array using regex with', extractedData.length, 'items');
                return this.processTasksResponse(extractedData);
              }
            }
          } catch (regexError) {
            console.error('Tasks regex extraction also failed:', regexError);
          }
          
          return [];
        }
      }
      
      if (!Array.isArray(response.data)) {
        console.error('Invalid task data format received:', typeof response.data, response.data);
        // Try to extract from a possible nested structure
        const taskData = Array.isArray(response.data?.tasks) ? response.data.tasks : [];
        return this.processTasksResponse(taskData);
      }
      
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error('Error fetching tasks:', error);
      throw error;
    }
  }
  
  // Helper method to process task response and ensure it has the correct format
  private processTasksResponse(tasks: unknown[]): Task[] {
    return tasks.map((taskData) => {
      // Ensure each task has the necessary properties
      if (!taskData || typeof taskData !== 'object') return null;
      
      const task = taskData as Partial<TaskApiResponse & { column?: Partial<TaskColumn> }>;
      
      // Make sure column property exists and has an id
      let columnData: TaskColumn;
      
      if (task.column && typeof task.column === 'object') {
        // Use existing column data if available
        columnData = {
          id: task.column.id || task.columnId || '',
          userId: task.column.userId || task.userId || '',
          title: task.column.title || 'Unknown',
          position: task.column.position || 0,
          createdAt: task.column.createdAt ? new Date(task.column.createdAt) : new Date(),
          updatedAt: task.column.updatedAt ? new Date(task.column.updatedAt) : new Date()
        };
      } else if (task.columnId) {
        // Create minimal column object if only columnId is available
        columnData = {
          id: task.columnId,
          userId: task.userId || '',
          title: 'Unknown',
          position: 0,
          createdAt: new Date(),
          updatedAt: new Date()
        };
      } else {
        // Fallback for completely missing column data
        columnData = {
          id: '',
          userId: task.userId || '',
          title: 'Unknown',
          position: 0,
          createdAt: new Date(),
          updatedAt: new Date()
        };
      }
      
      return {
        id: task.id || '',
        title: task.title || 'Untitled Task',
        description: task.description || '',
        priority: task.priority || 'MEDIUM',
        column: columnData,
        position: task.position || 0,
        userId: task.userId || '',
        createdAt: task.createdAt ? new Date(task.createdAt) : new Date(),
        updatedAt: task.updatedAt ? new Date(task.updatedAt) : new Date(),
        dueDate: task.dueDate ? new Date(task.dueDate) : undefined,
      } as Task;
    }).filter(Boolean) as Task[]; // Filter out any null values
  }

  async getTaskById(id: string): Promise<Task> {
    try {
      const response = await this.api.get(`/api/tasks/${id}`);
      
      // Handle string response (JSON string)
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          return {
            ...parsedData,
            createdAt: parsedData.createdAt ? new Date(parsedData.createdAt) : new Date(),
            updatedAt: parsedData.updatedAt ? new Date(parsedData.updatedAt) : new Date(),
            dueDate: parsedData.dueDate ? new Date(parsedData.dueDate) : undefined,
          };
        } catch (parseError) {
          console.error(`Failed to parse task data for ID ${id}:`, parseError);
          throw new Error(`Invalid task data format for ID ${id}`);
        }
      }
      
      // Normal flow
      return {
        ...response.data,
        createdAt: new Date(response.data.createdAt),
        updatedAt: new Date(response.data.updatedAt),
        dueDate: response.data.dueDate ? new Date(response.data.dueDate) : undefined,
      };
    } catch (error) {
      console.error(`Error fetching task with ID ${id}:`, error);
      throw error;
    }
  }

  async getTasksByColumn(columnId: string): Promise<Task[]> {
    try {
      const response = await this.api.get(`/api/tasks/column/${columnId}`);
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          if (Array.isArray(parsedData)) {
            return this.processTasksResponse(parsedData);
          }
          console.error('Parsed column tasks data is not an array:', parsedData);
          return [];
        } catch (parseError) {
          console.error(`Failed to parse column tasks for column ID ${columnId}:`, parseError);
          return [];
        }
      }
      
      // Handle non-array response
      if (!Array.isArray(response.data)) {
        console.error(`Invalid column tasks format for column ID ${columnId}:`, response.data);
        return [];
      }
      
      // Use the processTasksResponse helper to ensure consistent formatting
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error(`Error fetching tasks for column ID ${columnId}:`, error);
      return [];
    }
  }

  async getTasksByPriority(priority: Priority): Promise<Task[]> {
    try {
      const response = await this.api.get(`/api/tasks/priority/${priority}`);
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          if (Array.isArray(parsedData)) {
            return this.processTasksResponse(parsedData);
          }
          console.error(`Parsed priority tasks data is not an array for priority ${priority}:`, parsedData);
          return [];
        } catch (parseError) {
          console.error(`Failed to parse priority tasks for priority ${priority}:`, parseError);
          return [];
        }
      }
      
      // Handle non-array response
      if (!Array.isArray(response.data)) {
        console.error(`Invalid priority tasks format for priority ${priority}:`, response.data);
        return [];
      }
      
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error(`Error fetching tasks for priority ${priority}:`, error);
      return [];
    }
  }

  async getUpcomingTasks(days: number = 7): Promise<Task[]> {
    try {
      const response = await this.api.get(`/api/tasks/upcoming?days=${days}`);
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          if (Array.isArray(parsedData)) {
            return this.processTasksResponse(parsedData);
          }
          console.error(`Parsed upcoming tasks data is not an array:`, parsedData);
          return [];
        } catch (parseError) {
          console.error(`Failed to parse upcoming tasks:`, parseError);
          return [];
        }
      }
      
      // Handle non-array response
      if (!Array.isArray(response.data)) {
        console.error(`Invalid upcoming tasks format:`, response.data);
        return [];
      }
      
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error(`Error fetching upcoming tasks:`, error);
      return [];
    }
  }

  async getOverdueTasks(): Promise<Task[]> {
    try {
      const response = await this.api.get('/api/tasks/overdue');
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          if (Array.isArray(parsedData)) {
            return this.processTasksResponse(parsedData);
          }
          console.error(`Parsed overdue tasks data is not an array:`, parsedData);
          return [];
        } catch (parseError) {
          console.error(`Failed to parse overdue tasks:`, parseError);
          return [];
        }
      }
      
      // Handle non-array response
      if (!Array.isArray(response.data)) {
        console.error(`Invalid overdue tasks format:`, response.data);
        return [];
      }
      
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error(`Error fetching overdue tasks:`, error);
      return [];
    }
  }

  async searchTasks(query: string): Promise<Task[]> {
    try {
      const response = await this.api.get(`/api/tasks/search?query=${encodeURIComponent(query)}`);
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          if (Array.isArray(parsedData)) {
            return this.processTasksResponse(parsedData);
          }
          console.error(`Parsed search tasks data is not an array:`, parsedData);
          return [];
        } catch (parseError) {
          console.error(`Failed to parse search tasks:`, parseError);
          return [];
        }
      }
      
      // Handle non-array response
      if (!Array.isArray(response.data)) {
        console.error(`Invalid search tasks format:`, response.data);
        return [];
      }
      
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error(`Error searching tasks:`, error);
      return [];
    }
  }

  async createTask(task: CreateTaskRequest): Promise<Task> {
    try {
      const response = await this.api.post('/api/tasks', {
        ...task,
        dueDate: task.dueDate?.toISOString(),
      });
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          return {
            ...parsedData,
            createdAt: parsedData.createdAt ? new Date(parsedData.createdAt) : new Date(),
            updatedAt: parsedData.updatedAt ? new Date(parsedData.updatedAt) : new Date(),
            dueDate: parsedData.dueDate ? new Date(parsedData.dueDate) : undefined,
          };
        } catch (parseError) {
          console.error(`Failed to parse created task response:`, parseError);
          throw new Error('Failed to parse created task response');
        }
      }
      
      return {
        ...response.data,
        createdAt: new Date(response.data.createdAt),
        updatedAt: new Date(response.data.updatedAt),
        dueDate: response.data.dueDate ? new Date(response.data.dueDate) : undefined,
      };
    } catch (error) {
      console.error('Error creating task:', error);
      throw error;
    }
  }

  async updateTask(id: string, task: Partial<Task>): Promise<Task> {
    try {
      const response = await this.api.put(`/api/tasks/${id}`, {
        ...task,
        dueDate: task.dueDate?.toISOString(),
      });
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          return {
            ...parsedData,
            createdAt: parsedData.createdAt ? new Date(parsedData.createdAt) : new Date(),
            updatedAt: parsedData.updatedAt ? new Date(parsedData.updatedAt) : new Date(),
            dueDate: parsedData.dueDate ? new Date(parsedData.dueDate) : undefined,
          };
        } catch (parseError) {
          console.error(`Failed to parse updated task response for ID ${id}:`, parseError);
          throw new Error(`Failed to parse updated task response for ID ${id}`);
        }
      }
      
      return {
        ...response.data,
        createdAt: new Date(response.data.createdAt),
        updatedAt: new Date(response.data.updatedAt),
        dueDate: response.data.dueDate ? new Date(response.data.dueDate) : undefined,
      };
    } catch (error) {
      console.error(`Error updating task ${id}:`, error);
      throw error;
    }
  }

  async updateTaskPosition(id: string, columnId: string, position: number): Promise<Task> {
    try {
      const response = await this.api.put(`/api/tasks/${id}/position`, { columnId, position });
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          return {
            ...parsedData,
            createdAt: parsedData.createdAt ? new Date(parsedData.createdAt) : new Date(),
            updatedAt: parsedData.updatedAt ? new Date(parsedData.updatedAt) : new Date(),
            dueDate: parsedData.dueDate ? new Date(parsedData.dueDate) : undefined,
          };
        } catch (parseError) {
          console.error(`Failed to parse task position update response for ID ${id}:`, parseError);
          throw new Error(`Failed to parse task position update response for ID ${id}`);
        }
      }
      
      return {
        ...response.data,
        createdAt: new Date(response.data.createdAt),
        updatedAt: new Date(response.data.updatedAt),
        dueDate: response.data.dueDate ? new Date(response.data.dueDate) : undefined,
      };
    } catch (error) {
      console.error(`Error updating position for task ${id}:`, error);
      throw error;
    }
  }

  async moveTaskToColumn(id: string, columnId: string): Promise<Task> {
    try {
      const response = await this.api.put(`/api/tasks/${id}/move/${columnId}`);
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          return {
            ...parsedData,
            createdAt: parsedData.createdAt ? new Date(parsedData.createdAt) : new Date(),
            updatedAt: parsedData.updatedAt ? new Date(parsedData.updatedAt) : new Date(),
            dueDate: parsedData.dueDate ? new Date(parsedData.dueDate) : undefined,
          };
        } catch (parseError) {
          console.error(`Failed to parse task move response for ID ${id}:`, parseError);
          throw new Error(`Failed to parse task move response for ID ${id}`);
        }
      }
      
      return {
        ...response.data,
        createdAt: new Date(response.data.createdAt),
        updatedAt: new Date(response.data.updatedAt),
        dueDate: response.data.dueDate ? new Date(response.data.dueDate) : undefined,
      };
    } catch (error) {
      console.error(`Error moving task ${id} to column ${columnId}:`, error);
      throw error;
    }
  }

  async deleteTask(id: string): Promise<void> {
    await this.api.delete(`/api/tasks/${id}`);
  }

  // Bulk operations
  async moveMultipleTasks(taskIds: string[], targetColumnId: string): Promise<void> {
    await this.api.post('/api/tasks/bulk/move', { taskIds, targetColumnId });
  }

  async deleteMultipleTasks(taskIds: string[]): Promise<void> {
    await this.api.post('/api/tasks/bulk/delete', { taskIds });
  }

  async updateMultipleTasksPriority(taskIds: string[], priority: Priority): Promise<void> {
    await this.api.post('/api/tasks/bulk/priority', { taskIds, priority });
  }

  // Analytics
  async getTaskStats(): Promise<TaskStats> {
    const response = await this.api.get('/api/tasks/stats');
    return response.data;
  }

  async getTaskCountByPriority(): Promise<Record<Priority, number>> {
    const response = await this.api.get('/api/tasks/stats/priority');
    return response.data;
  }

  async getTaskCountByStatus(): Promise<Record<string, number>> {
    const response = await this.api.get('/api/tasks/stats/status');
    return response.data;
  }

  async getTasksDueSoon(days: number = 3): Promise<Task[]> {
    try {
      const response = await this.api.get(`/api/tasks/due-soon?days=${days}`);
      
      // Handle string response
      if (typeof response.data === 'string') {
        try {
          const parsedData = JSON.parse(response.data);
          if (Array.isArray(parsedData)) {
            return this.processTasksResponse(parsedData);
          }
          console.error(`Parsed due-soon tasks data is not an array:`, parsedData);
          return [];
        } catch (parseError) {
          console.error(`Failed to parse due-soon tasks:`, parseError);
          return [];
        }
      }
      
      // Handle non-array response
      if (!Array.isArray(response.data)) {
        console.error(`Invalid due-soon tasks format:`, response.data);
        return [];
      }
      
      return this.processTasksResponse(response.data);
    } catch (error) {
      console.error(`Error fetching due-soon tasks:`, error);
      return [];
    }
  }
}

export const taskService = new TaskService();