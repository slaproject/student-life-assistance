// Common model interfaces shared between frontend and backend

export interface User {
  id?: string;
  username: string;
  email: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Task {
  id?: string;
  title: string;
  description?: string;
  dueDate?: string;
  completed: boolean;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  columnId?: string;
  userId?: string;
  createdAt?: string;
  updatedAt?: string;
  attachments?: TaskAttachment[];
}

export interface TaskAttachment {
  id?: string;
  taskId: string;
  fileName: string;
  fileUrl: string;
  fileType: string;
  uploadedAt?: string;
}

export interface TaskColumn {
  id?: string;
  name: string;
  position: number;
  userId: string;
  tasks?: Task[];
}

export interface CalendarEvent {
  id?: string;
  eventName: string;
  description?: string;
  startTime: string;
  endTime: string;
  meetingLink?: string;
  eventType?: 'MEETING' | 'PERSONAL' | 'FINANCIAL' | 'APPOINTMENT' | 'OTHER';
  userId?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Expense {
  id?: string;
  amount: number;
  description?: string;
  date: string;
  categoryId: string;
  userId: string;
  category?: ExpenseCategory;
}

export interface ExpenseCategory {
  id?: string;
  name: string;
  color?: string;
  userId?: string;
}

export interface BudgetLimit {
  id?: string;
  amount: number;
  period: 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  startDate: string;
  endDate?: string;
  categoryId?: string;
  userId: string;
  category?: ExpenseCategory;
}

export interface FinancialGoal {
  id?: string;
  name: string;
  targetAmount: number;
  currentAmount: number;
  deadline?: string;
  userId: string;
  createdAt?: string;
  updatedAt?: string;
}

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  username: string;
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
}