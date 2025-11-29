"use client";
import { getApiClient } from "./api";

// Type definitions for finance data
export interface ExpenseCategory {
  id: string;
  name: string;
  description?: string;
  color?: string;
  icon?: string;
  isActive: boolean;
  userId: string;
}

export interface Expense {
  id: string;
  title: string;
  amount: number;
  expenseDate: string;
  category: ExpenseCategory;
  paymentMethod?: string;
  description?: string;
  userId: string;
}

export interface BudgetLimit {
  id: string;
  category: ExpenseCategory;
  budgetMonth: number;
  budgetYear: number;
  limitAmount: number;
  alertThreshold: number;
  userId: string;
}

export interface FinancialGoal {
  id: string;
  goalName: string;
  description?: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string;
  goalType: string;
  isActive: boolean;
  userId: string;
}

export interface BudgetAnalysis {
  totalBudget: number;
  totalSpent: number;
  remaining: number;
  percentageUsed: number;
}

export interface SpendingTrend {
  month: string;
  total: number;
}

export interface BudgetAlert {
  categoryName: string;
  budgetLimit: number;
  spent: number;
  percentageUsed: number;
  alertThreshold: number;
}

export interface CategoryWiseExpenses {
  [categoryName: string]: number;
}

class FinanceService {
  private api = getApiClient();

  // Expense Category methods
  async getCategories(): Promise<ExpenseCategory[]> {
    try {
      console.log('Fetching categories from API...');
      const response = await this.api.get('/api/finance/categories');
      console.log('Categories API response:', response);
      console.log('Categories response data:', response.data);

      const data = response.data;

      // Ensure we have an array and convert types
      if (Array.isArray(data)) {
        console.log('Categories data is array, length:', data.length);
        const convertedCategories = data.map(category => {
          console.log('Converting category:', category);
          return {
            id: category.id?.toString() || '',
            name: category.name || '',
            description: category.description || '',
            color: category.color || '',
            icon: category.icon || '',
            isActive: Boolean(category.isActive),
            userId: category.userId?.toString() || ''
          };
        });
        console.log('Converted categories:', convertedCategories);
        return convertedCategories;
      }

      console.log('Categories data is not an array:', typeof data, data);
      return [];
    } catch (error: unknown) {
      console.error('Error fetching categories:', error);
      const err = error as { response?: { status?: number; data?: unknown } };
      console.error('Error details:', err.response?.data);
      console.error('Error status:', err.response?.status);
      // Return empty array instead of throwing to prevent component crashes
      return [];
    }
  }

  async getCategoryById(id: string): Promise<ExpenseCategory> {
    try {
      const response = await this.api.get(`/api/finance/categories/${id}`);
      const category = response.data;
      return {
        id: category.id?.toString() || '',
        name: category.name || '',
        description: category.description || '',
        color: category.color || '',
        icon: category.icon || '',
        isActive: Boolean(category.isActive),
        userId: category.userId?.toString() || ''
      };
    } catch (error) {
      console.error('Error fetching category:', error);
      throw error;
    }
  }

  async createCategory(category: Omit<ExpenseCategory, 'id' | 'userId'>): Promise<ExpenseCategory> {
    try {
      const response = await this.api.post('/api/finance/categories', category);
      const createdCategory = response.data;
      return {
        id: createdCategory.id?.toString() || '',
        name: createdCategory.name || '',
        description: createdCategory.description || '',
        color: createdCategory.color || '',
        icon: createdCategory.icon || '',
        isActive: Boolean(createdCategory.isActive),
        userId: createdCategory.userId?.toString() || ''
      };
    } catch (error) {
      console.error('Error creating category:', error);
      throw error;
    }
  }

  // Helper method to create default categories
  async createDefaultCategories(): Promise<ExpenseCategory[]> {
    const defaultCategories = [
      {
        name: 'Food & Dining',
        description: 'Restaurants, groceries, takeout',
        color: '#FF6B6B',
        icon: 'restaurant',
        isActive: true
      },
      {
        name: 'Transportation',
        description: 'Gas, public transport, parking',
        color: '#4ECDC4',
        icon: 'car',
        isActive: true
      },
      {
        name: 'Education',
        description: 'Books, courses, supplies',
        color: '#96CEB4',
        icon: 'school',
        isActive: true
      },
      {
        name: 'Entertainment',
        description: 'Movies, games, hobbies',
        color: '#45B7D1',
        icon: 'entertainment',
        isActive: true
      },
      {
        name: 'Income',
        description: 'Salary, freelance, gifts',
        color: '#4ECDC4',
        icon: 'attach_money',
        isActive: true
      }
    ];

    const createdCategories: ExpenseCategory[] = [];

    for (const categoryData of defaultCategories) {
      try {
        const createdCategory = await this.createCategory(categoryData);
        createdCategories.push(createdCategory);
      } catch (error) {
        console.error('Error creating default category:', categoryData.name, error);
      }
    }

    return createdCategories;
  }

  async updateCategory(id: string, category: Partial<ExpenseCategory>): Promise<ExpenseCategory> {
    try {
      const response = await this.api.put(`/api/finance/categories/${id}`, category);
      const updatedCategory = response.data;
      return {
        id: updatedCategory.id?.toString() || '',
        name: updatedCategory.name || '',
        description: updatedCategory.description || '',
        color: updatedCategory.color || '',
        icon: updatedCategory.icon || '',
        isActive: Boolean(updatedCategory.isActive),
        userId: updatedCategory.userId?.toString() || ''
      };
    } catch (error) {
      console.error('Error updating category:', error);
      throw error;
    }
  }

  async deleteCategory(id: string): Promise<void> {
    try {
      await this.api.delete(`/api/finance/categories/${id}`);
    } catch (error) {
      console.error('Error deleting category:', error);
      throw error;
    }
  }

  // Expense methods
  async getExpenses(): Promise<Expense[]> {
    try {
      console.log('Fetching expenses from API...');
      const response = await this.api.get('/api/finance/expenses');
      console.log('Expenses API response:', response);
      console.log('Expenses response data:', response.data);
      const data = response.data;

      // Ensure we have an array and convert types
      if (Array.isArray(data)) {
        console.log('Expenses data is array, length:', data.length);
        return data.map((expense: Expense) => {
          console.log('Converting expense:', expense);

          // Handle date formats (string or array [yyyy, mm, dd])
          let expenseDate = '';
          const rawDate = expense.expenseDate;

          if (Array.isArray(rawDate)) {
            // Handle [yyyy, mm, dd] format from Java LocalDate
            const year = rawDate[0];
            const month = rawDate[1].toString().padStart(2, '0');
            const day = rawDate[2].toString().padStart(2, '0');
            expenseDate = `${year}-${month}-${day}`;
          } else if (rawDate) {
            expenseDate = rawDate.toString();
          }

          // Handle amount
          let amount = 0;
          const rawAmount = expense.amount;
          if (rawAmount !== undefined && rawAmount !== null) {
            const numAmount = Number(rawAmount);
            if (!isNaN(numAmount)) {
              amount = numAmount;
            }
          }

          // Handle title/description
          const title = expense.title || expense.description || 'Untitled Expense';
          const description = expense.description || expense.title || '';

          return {
            id: (expense.id || '').toString(),
            title: title,
            amount: amount,
            expenseDate: expenseDate,
            category: {
              id: (expense.category?.id || '').toString(),
              name: expense.category?.name || 'Uncategorized',
              description: expense.category?.description || '',
              color: expense.category?.color || '#808080',
              icon: expense.category?.icon || 'help',
              isActive: Boolean(expense.category?.isActive),
              userId: (expense.category?.userId || '').toString()
            },
            paymentMethod: expense.paymentMethod || '',
            description: description,
            userId: (expense.userId || '').toString()
          };
        });
      }

      return [];
    } catch (error) {
      console.error('Error fetching expenses:', error);
      // Return empty array instead of throwing to prevent component crashes
      return [];
    }
  }

  async getExpensesByMonth(year: number, month: number): Promise<Expense[]> {
    try {
      const response = await this.api.get(`/api/finance/expenses/month/${year}/${month}`);
      const data = response.data;

      if (Array.isArray(data)) {
        return data.map(expense => ({
          id: expense.id?.toString() || '',
          title: expense.title || '',
          amount: Number(expense.amount) || 0,
          expenseDate: expense.expenseDate?.toString() || '',
          category: {
            id: expense.category?.id?.toString() || '',
            name: expense.category?.name || '',
            description: expense.category?.description || '',
            color: expense.category?.color || '',
            icon: expense.category?.icon || '',
            isActive: Boolean(expense.category?.isActive),
            userId: expense.category?.userId?.toString() || ''
          },
          paymentMethod: expense.paymentMethod || '',
          description: expense.description || '',
          userId: expense.userId?.toString() || ''
        }));
      }

      return [];
    } catch (error) {
      console.error('Error fetching expenses by month:', error);
      return [];
    }
  }

  async getExpensesByCategory(categoryId: string): Promise<Expense[]> {
    try {
      const response = await this.api.get(`/api/finance/expenses/category/${categoryId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching expenses by category:', error);
      throw error;
    }
  }

  async getExpensesByDateRange(startDate: string, endDate: string): Promise<Expense[]> {
    try {
      const response = await this.api.get('/api/finance/expenses/daterange', {
        params: { startDate, endDate }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching expenses by date range:', error);
      throw error;
    }
  }

  async createExpense(expense: Omit<Expense, 'id' | 'userId'>): Promise<Expense> {
    try {
      const response = await this.api.post('/api/finance/expenses', expense);
      const createdExpense = response.data;
      return {
        id: createdExpense.id?.toString() || '',
        title: createdExpense.title || '',
        amount: Number(createdExpense.amount) || 0,
        expenseDate: createdExpense.expenseDate?.toString() || '',
        category: {
          id: createdExpense.category?.id?.toString() || '',
          name: createdExpense.category?.name || '',
          description: createdExpense.category?.description || '',
          color: createdExpense.category?.color || '',
          icon: createdExpense.category?.icon || '',
          isActive: Boolean(createdExpense.category?.isActive),
          userId: createdExpense.category?.userId?.toString() || ''
        },
        paymentMethod: createdExpense.paymentMethod || '',
        description: createdExpense.description || '',
        userId: createdExpense.userId?.toString() || ''
      };
    } catch (error) {
      console.error('Error creating expense:', error);
      throw error;
    }
  }

  async updateExpense(id: string, expense: Partial<Expense>): Promise<Expense> {
    try {
      const response = await this.api.put(`/api/finance/expenses/${id}`, expense);
      return response.data;
    } catch (error) {
      console.error('Error updating expense:', error);
      throw error;
    }
  }

  async deleteExpense(id: string): Promise<void> {
    try {
      await this.api.delete(`/api/finance/expenses/${id}`);
    } catch (error) {
      console.error('Error deleting expense:', error);
      throw error;
    }
  }

  async uploadBill(file: File): Promise<Expense> {
    try {
      const formData = new FormData();
      formData.append('file', file);
      const response = await this.api.post('/api/bills/upload', formData);

      const createdExpense = response.data;
      return {
        id: createdExpense.id?.toString() || '',
        title: createdExpense.title || '',
        amount: Number(createdExpense.amount) || 0,
        expenseDate: createdExpense.expenseDate?.toString() || '',
        category: {
          id: createdExpense.category?.id?.toString() || '',
          name: createdExpense.category?.name || '',
          description: createdExpense.category?.description || '',
          color: createdExpense.category?.color || '',
          icon: createdExpense.category?.icon || '',
          isActive: Boolean(createdExpense.category?.isActive),
          userId: createdExpense.category?.userId?.toString() || ''
        },
        paymentMethod: createdExpense.paymentMethod || '',
        description: createdExpense.description || '',
        userId: createdExpense.userId?.toString() || ''
      };
    } catch (error) {
      console.error('Error uploading bill:', error);
      throw error;
    }
  }

  // Budget Limit methods
  async getBudgetLimits(month: number, year: number): Promise<BudgetLimit[]> {
    try {
      const response = await this.api.get('/api/finance/budget-limits', {
        params: { month, year }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching budget limits:', error);
      throw error;
    }
  }

  async createBudgetLimit(budgetLimit: Omit<BudgetLimit, 'id' | 'userId'>): Promise<BudgetLimit> {
    try {
      const response = await this.api.post('/api/finance/budget-limits', budgetLimit);
      return response.data;
    } catch (error) {
      console.error('Error creating budget limit:', error);
      throw error;
    }
  }

  async updateBudgetLimit(id: string, budgetLimit: Partial<BudgetLimit>): Promise<BudgetLimit> {
    try {
      const response = await this.api.put(`/api/finance/budget-limits/${id}`, budgetLimit);
      return response.data;
    } catch (error) {
      console.error('Error updating budget limit:', error);
      throw error;
    }
  }

  async deleteBudgetLimit(id: string): Promise<void> {
    try {
      await this.api.delete(`/api/finance/budget-limits/${id}`);
    } catch (error) {
      console.error('Error deleting budget limit:', error);
      throw error;
    }
  }

  // Financial Goal methods
  async getFinancialGoals(): Promise<FinancialGoal[]> {
    try {
      const response = await this.api.get('/api/finance/goals');
      return response.data;
    } catch (error) {
      console.error('Error fetching financial goals:', error);
      throw error;
    }
  }

  async createFinancialGoal(goal: Omit<FinancialGoal, 'id' | 'userId'>): Promise<FinancialGoal> {
    try {
      const response = await this.api.post('/api/finance/goals', goal);
      return response.data;
    } catch (error) {
      console.error('Error creating financial goal:', error);
      throw error;
    }
  }

  async updateFinancialGoal(id: string, goal: Partial<FinancialGoal>): Promise<FinancialGoal> {
    try {
      const response = await this.api.put(`/api/finance/goals/${id}`, goal);
      return response.data;
    } catch (error) {
      console.error('Error updating financial goal:', error);
      throw error;
    }
  }

  async deleteFinancialGoal(id: string): Promise<void> {
    try {
      await this.api.delete(`/api/finance/goals/${id}`);
    } catch (error) {
      console.error('Error deleting financial goal:', error);
      throw error;
    }
  }

  // Analytics methods
  async getMonthlyTotal(month: number, year: number): Promise<number> {
    try {
      const response = await this.api.get('/api/finance/analytics/monthly-total', {
        params: { month, year }
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching monthly total:', error);
      throw error;
    }
  }

  async getCategoryWiseExpenses(month: number, year: number): Promise<CategoryWiseExpenses> {
    try {
      const response = await this.api.get('/api/finance/analytics/category-wise', {
        params: { month, year }
      });
      const data = response.data;

      // Convert BigDecimal values to numbers
      const convertedData: CategoryWiseExpenses = {};
      if (data && typeof data === 'object') {
        Object.entries(data).forEach(([key, value]) => {
          convertedData[key] = Number(value) || 0;
        });
      }

      return convertedData;
    } catch (error) {
      console.error('Error fetching category-wise expenses:', error);
      return {};
    }
  }

  async getBudgetAnalysis(month: number, year: number): Promise<BudgetAnalysis> {
    try {
      const response = await this.api.get('/api/finance/analytics/budget-analysis', {
        params: { month, year }
      });
      const data = response.data;
      return {
        totalBudget: Number(data.totalBudget) || 0,
        totalSpent: Number(data.totalSpent) || 0,
        remaining: Number(data.remaining) || 0,
        percentageUsed: Number(data.percentageUsed) || 0
      };
    } catch (error) {
      console.error('Error fetching budget analysis:', error);
      return {
        totalBudget: 0,
        totalSpent: 0,
        remaining: 0,
        percentageUsed: 0
      };
    }
  }

  async getSpendingTrends(months: number = 6): Promise<{ monthlyTotals: SpendingTrend[] }> {
    try {
      const response = await this.api.get('/api/finance/analytics/spending-trends', {
        params: { months }
      });
      const data = response.data;

      if (data && data.monthlyTotals && Array.isArray(data.monthlyTotals)) {
        return {
          monthlyTotals: data.monthlyTotals.map((trend: { month?: string; total?: number }) => ({
            month: trend.month || '',
            total: Number(trend.total) || 0
          }))
        };
      }

      return { monthlyTotals: [] };
    } catch (error) {
      console.error('Error fetching spending trends:', error);
      return { monthlyTotals: [] };
    }
  }

  async getBudgetAlerts(): Promise<BudgetAlert[]> {
    try {
      const response = await this.api.get('/api/finance/analytics/budget-alerts');
      const data = response.data;

      if (Array.isArray(data)) {
        return data.map((alert: { categoryName?: string; budgetLimit?: number; spent?: number; percentageUsed?: number; alertThreshold?: number }) => ({
          categoryName: alert.categoryName || '',
          budgetLimit: Number(alert.budgetLimit) || 0,
          spent: Number(alert.spent) || 0,
          percentageUsed: Number(alert.percentageUsed) || 0,
          alertThreshold: Number(alert.alertThreshold) || 0
        }));
      }

      return [];
    } catch (error) {
      console.error('Error fetching budget alerts:', error);
      return [];
    }
  }
}

export const financeService = new FinanceService();
