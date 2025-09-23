"use client";
import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  LOGIN: `${API_BASE_URL}/api/auth/login`,
  REGISTER: `${API_BASE_URL}/api/auth/signup`,
  
  // Tasks
  TASKS: `${API_BASE_URL}/api/tasks`,
  TASK_COLUMNS: `${API_BASE_URL}/api/tasks/columns`,
  
  // Calendar
  CALENDAR: `${API_BASE_URL}/api/calendar`,
  
  // Finance
  EXPENSES: `${API_BASE_URL}/api/finance/expenses`,
  EXPENSE_CATEGORIES: `${API_BASE_URL}/api/finance/categories`,
  BUDGET_LIMITS: `${API_BASE_URL}/api/finance/budgets`,
  FINANCIAL_GOALS: `${API_BASE_URL}/api/finance/goals`,
};

export function getApiClient() {
  const instance = axios.create({ 
    baseURL: API_BASE_URL,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    withCredentials: true, // Include cookies and credentials
  });
  
  instance.interceptors.request.use((config) => {
    if (typeof window !== "undefined") {
      const token = localStorage.getItem("token");
      const looksLikeJwt = token && token.split(".").length === 3;
      if (looksLikeJwt) {
        config.headers = config.headers || {};
        config.headers.Authorization = `Bearer ${token}`;
      }
      
      // Ensure proper headers for CORS
      if (config.headers) {
        config.headers['Accept'] = 'application/json';
        config.headers['Content-Type'] = 'application/json';
      }
    }
    return config;
  }, (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  });

  // Response interceptor for error handling
  instance.interceptors.response.use(
    (response) => response,
    (error) => {
      console.error('API Error:', {
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        config: {
          method: error.config?.method,
          url: error.config?.url,
          headers: error.config?.headers
        }
      });
      
      if (error.response?.status === 401) {
        // Clear token and redirect to login on unauthorized
        if (typeof window !== "undefined") {
          localStorage.removeItem("token");
          window.location.href = "/login";
        }
      }
      return Promise.reject(error);
    }
  );
  
  return instance;
}
