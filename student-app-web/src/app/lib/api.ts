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

  // Summary
  SUMMARY_GENERATE: `${API_BASE_URL}/api/summary/generate`,
  SUMMARY_USAGE: `${API_BASE_URL}/api/summary/usage`,
  SUMMARY_HISTORY: `${API_BASE_URL}/api/summary/history`,

  // Tax Agent
  TAX_AGENT_GENERATE: `${API_BASE_URL}/api/tax-agent/generate`,
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
        if (config.data instanceof FormData) {
          delete config.headers['Content-Type'];
        } else {
          config.headers['Content-Type'] = 'application/json';
        }
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

      if (error.response?.status === 401 || error.response?.status === 403) {
        // Clear token and redirect to login on unauthorized or forbidden
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

export const summaryService = {
  generateSummary: async (text: string, sourceType: 'TEXT' | 'YOUTUBE' = 'TEXT', videoUrl?: string) => {
    const client = getApiClient();
    const response = await client.post(API_ENDPOINTS.SUMMARY_GENERATE, { text, sourceType, videoUrl });
    return response.data;
  },
  getUsage: async () => {
    const client = getApiClient();
    const response = await client.get(API_ENDPOINTS.SUMMARY_USAGE);
    return response.data;
  },
  getHistory: async () => {
    const client = getApiClient();
    const response = await client.get(API_ENDPOINTS.SUMMARY_HISTORY);
    return response.data;
  }
};

export const taxAgentService = {
  generateTaxGuidance: async (request: {
    hasOnCampusJob: boolean;
    hasWorkStudy: boolean;
    residentState: string;
    workState: string;
  }) => {
    const client = getApiClient();
    // Tax Agent needs longer timeout for LLM processing (90 seconds)
    const response = await client.post(API_ENDPOINTS.TAX_AGENT_GENERATE, request, {
      timeout: 90000
    });
    return response.data;
  }
};
