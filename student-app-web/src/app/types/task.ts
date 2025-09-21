export interface Task {
  id: string;
  userId: string;
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  column: TaskColumn;
  assignedTo?: string;
  dueDate?: Date;
  position: number;
  tags?: string;
  projectId?: string;
  createdAt: Date;
  updatedAt: Date;
  attachments?: TaskAttachment[];
}

export interface TaskColumn {
  id: string;
  userId: string;
  title: string;
  color?: string;
  position: number;
  createdAt: Date;
  updatedAt: Date;
  tasks?: Task[];
}

export interface TaskAttachment {
  id: string;
  task: Task;
  fileName: string;
  fileUrl: string;
  fileSize?: number;
  contentType?: string;
  uploadedAt: Date;
}

export interface TaskStats {
  totalTasks: number;
  completedTasks: number;
  overdueTasks: number;
  tasksByPriority: Record<string, number>;
  tasksByStatus: Record<string, number>;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  column: { id: string };
  dueDate?: Date;
  tags?: string;
}

export interface UpdateTaskPositionRequest {
  columnId: string;
  position: number;
}

export interface BulkTaskOperation {
  taskIds: string[];
  targetColumnId?: string;
  priority?: 'LOW' | 'MEDIUM' | 'HIGH';
}

export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';

export const priorityColors = {
  LOW: '#4CAF50',
  MEDIUM: '#FF9800',
  HIGH: '#F44336'
} as const;

export const priorityLabels = {
  LOW: 'Low',
  MEDIUM: 'Medium', 
  HIGH: 'High'
} as const;