"use client";
import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  // Grid,
  Button,
  TextField,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Card,
  CardContent,
  CircularProgress,
  Alert,
  // Divider,
  useTheme,
  useMediaQuery
} from "@mui/material";
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Edit as EditIcon,
  Savings as SavingsIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Receipt as ReceiptIcon,
  Close as CloseIcon,
  Save as SaveIcon
} from "@mui/icons-material";
import { financeService, Expense, ExpenseCategory } from "../../lib/financeService";

// Interface definitions for UI compatibility
interface Transaction {
  id: string;
  date: string;
  description: string;
  amount: number;
  category: string;
  categoryId?: string;
}

// Sample data for fallback
const SAMPLE_TRANSACTIONS: Transaction[] = [
  { id: "1", date: "2025-09-01", description: "Textbooks", amount: -120.50, category: "Education" },
  { id: "2", date: "2025-09-02", description: "Part-time Job", amount: 250.00, category: "Income" },
  { id: "3", date: "2025-09-03", description: "Groceries", amount: -45.75, category: "Food & Dining" },
  { id: "4", date: "2025-08-28", description: "Scholarship", amount: 1000.00, category: "Income" },
  { id: "5", date: "2025-08-25", description: "Rent", amount: -650.00, category: "Housing" },
  { id: "6", date: "2025-08-20", description: "Bus Pass", amount: -75.00, category: "Transportation" }
];

export default function ExpensesTab() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<ExpenseCategory[]>([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null);
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0],
    description: '',
    amount: '',
    category: '',
    categoryId: ''
  });
  const [loading, setLoading] = useState(true);
  const [loadingCategories, setLoadingCategories] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  // Fetch expenses data from API (categories loaded lazily when needed)
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        // Only fetch expenses - categories will be loaded when dialog opens
        const expensesData = await financeService.getExpenses();

        console.log('Fetched expenses:', expensesData);
        console.log('Expenses type:', typeof expensesData);
        console.log('Expenses is array:', Array.isArray(expensesData));
        console.log('Expenses length:', expensesData?.length);

        // Debug individual expense data
        if (Array.isArray(expensesData) && expensesData.length > 0) {
          console.log('First expense data:', expensesData[0]);
          console.log('First expense date:', expensesData[0]?.expenseDate);
          console.log('First expense amount:', expensesData[0]?.amount);
          console.log('First expense amount type:', typeof expensesData[0]?.amount);
          console.log('First expense category:', expensesData[0]?.category);
          console.log('First expense title:', expensesData[0]?.title);
        }

        const safeExpenses = Array.isArray(expensesData) ? expensesData : [];

        // If we have no expenses but we can get analytics data, let's try that
        if (safeExpenses.length === 0) {
          console.log('No individual expenses found, trying to get analytics data...');
          try {
            const currentDate = new Date();
            const currentMonth = currentDate.getMonth() + 1;
            const currentYear = currentDate.getFullYear();

            const categoryWiseExpenses = await financeService.getCategoryWiseExpenses(currentMonth, currentYear);
            console.log('Category-wise expenses from analytics:', categoryWiseExpenses);

            // Convert analytics data to transaction format
            const analyticsTransactions: Transaction[] = Object.entries(categoryWiseExpenses).map(([categoryName, amount], index) => {
              const isIncome = categoryName.toLowerCase().includes('income');

              return {
                id: `analytics-${index}`,
                date: new Date().toISOString().split('T')[0],
                description: `${categoryName} expenses`,
                amount: isIncome ? Math.abs(amount) : -Math.abs(amount),
                category: categoryName,
                categoryId: undefined // Category ID not available from analytics
              };
            });

            if (analyticsTransactions.length > 0) {
              console.log('Using analytics data for transactions:', analyticsTransactions);
              setTransactions(analyticsTransactions);
              return;
            }
          } catch (analyticsError) {
            console.error('Error fetching analytics data:', analyticsError);
          }
        }

        // Convert expenses to transactions format for UI display
        const transactionsFromExpenses: Transaction[] = safeExpenses.map((expense: Expense) => {
          const categoryName = expense.category ? expense.category.name : 'Unknown';
          const isIncome = categoryName.toLowerCase().includes('income');

          // Better amount handling
          let amount = 0;
          if (expense.amount !== undefined && expense.amount !== null) {
            const numAmount = Number(expense.amount);
            if (!isNaN(numAmount)) {
              amount = isIncome ? Math.abs(numAmount) : -Math.abs(numAmount);
            }
          }

          console.log('Processing expense:', {
            id: expense.id,
            title: expense.title,
            amount: expense.amount,
            convertedAmount: amount,
            isIncome,
            categoryName
          });

          // Ensure date is properly formatted
          let formattedDate = expense.expenseDate;
          if (expense.expenseDate) {
            try {
              // Handle different date formats
              let date: Date;
              if (typeof expense.expenseDate === 'string') {
                // If it's already in YYYY-MM-DD format, use it directly
                if (/^\d{4}-\d{2}-\d{2}$/.test(expense.expenseDate)) {
                  formattedDate = expense.expenseDate;
                } else {
                  date = new Date(expense.expenseDate);
                  if (!isNaN(date.getTime())) {
                    formattedDate = date.toISOString().split('T')[0];
                  }
                }
              } else {
                date = new Date(expense.expenseDate);
                if (!isNaN(date.getTime())) {
                  formattedDate = date.toISOString().split('T')[0];
                } else {
                  const dateStr = String(expense.expenseDate);
                  if (dateStr.includes(',')) {
                    // Handle comma separated date string "yyyy,mm,dd"
                    const parts = dateStr.split(',');
                    if (parts.length === 3) {
                      const year = parts[0].trim();
                      const month = parts[1].trim().padStart(2, '0');
                      const day = parts[2].trim().padStart(2, '0');
                      formattedDate = `${year}-${month}-${day}`;
                    }
                  }
                }
              }
            } catch (error) {
              console.error('Error formatting date:', error);
              formattedDate = new Date().toISOString().split('T')[0];
            }
          } else {
            formattedDate = new Date().toISOString().split('T')[0];
          }

          console.log('Date processing:', {
            originalDate: expense.expenseDate,
            formattedDate: formattedDate
          });

          return {
            id: expense.id,
            date: formattedDate,
            description: expense.title || 'No description',
            amount: amount,
            category: categoryName,
            categoryId: expense.category?.id
          };
        });

        setTransactions(transactionsFromExpenses);
      } catch (err) {
        console.error('Error fetching finance data:', err);
        setError('Using sample data - API connection failed');
        setTransactions(SAMPLE_TRANSACTIONS);

        // Create default categories from sample data
        const uniqueCategories = [...new Set(SAMPLE_TRANSACTIONS.map(t => t.category))];
        setCategories(uniqueCategories.map((name, index) => ({
          id: (index + 1).toString(),
          name,
          userId: "1",
          isActive: true
        })));
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Calculate summary statistics
  const totalBalance = transactions.reduce((sum, transaction) => sum + transaction.amount, 0);
  const totalIncome = transactions
    .filter(t => t.amount > 0)
    .reduce((sum, transaction) => sum + transaction.amount, 0);
  const totalExpenses = transactions
    .filter(t => t.amount < 0)
    .reduce((sum, transaction) => sum + Math.abs(transaction.amount), 0);

  const handleOpenDialog = async (transaction: Transaction | null = null) => {
    // Lazy load categories when dialog opens (if not already loaded)
    if (categories.length === 0 && !loadingCategories) {
      setLoadingCategories(true);
      try {
        console.log('Lazy loading categories for dialog...');
        let fetchedCategories = await financeService.getCategories();

        // If no categories exist, try to create default ones
        if (!fetchedCategories || fetchedCategories.length === 0) {
          console.log('No categories found, initializing defaults...');
          const defaultCategories = await financeService.createDefaultCategories();
          if (defaultCategories && defaultCategories.length > 0) {
            fetchedCategories = defaultCategories;
            console.log('Initialized default categories:', fetchedCategories);
          } else {
            // Try fetching again in case they were created
            fetchedCategories = await financeService.getCategories();
          }
        }

        if (fetchedCategories && fetchedCategories.length > 0) {
          setCategories(fetchedCategories);
        } else {
          console.warn('No categories available after initialization attempt');
        }
      } catch (err) {
        console.error('Error loading categories:', err);
      } finally {
        setLoadingCategories(false);
      }
    }

    if (transaction) {
      setEditingTransaction(transaction);
      setFormData({
        date: transaction.date,
        description: transaction.description,
        amount: Math.abs(transaction.amount).toString(),
        category: transaction.category,
        categoryId: transaction.categoryId || ''
      });
    } else {
      setEditingTransaction(null);
      setFormData({
        date: new Date().toISOString().split('T')[0],
        description: '',
        amount: '',
        category: Array.isArray(categories) && categories.length > 0 ? categories[0].name : '',
        categoryId: Array.isArray(categories) && categories.length > 0 ? categories[0].id : ''
      });
    }
    setDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
    setEditingTransaction(null);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSaveTransaction = async () => {
    if (!formData.description || !formData.amount || !formData.date || !formData.categoryId) {
      alert("Please fill all required fields");
      return;
    }

    try {
      const selectedCategory = Array.isArray(categories) ? categories.find(cat => cat.id === formData.categoryId) : null;
      if (!selectedCategory) {
        alert("Please select a valid category");
        return;
      }

      const expenseData = {
        title: formData.description,
        amount: parseFloat(formData.amount),
        expenseDate: formData.date,
        category: selectedCategory,
        paymentMethod: "card", // Default payment method
        description: formData.description
      };

      if (editingTransaction) {
        // Update existing expense
        await financeService.updateExpense(editingTransaction.id, expenseData);

        // Update local state
        const updatedExpense = await financeService.getExpenses();
        const updatedTransactions: Transaction[] = updatedExpense.map((expense: Expense) => {
          const categoryName = expense.category ? expense.category.name : 'Unknown';
          const isIncome = categoryName.toLowerCase().includes('income');
          const amount = isIncome ? Math.abs(expense.amount) : -Math.abs(expense.amount);

          return {
            id: expense.id,
            date: expense.expenseDate,
            description: expense.title,
            amount: amount,
            category: categoryName,
            categoryId: expense.category?.id
          };
        });
        setTransactions(updatedTransactions);
      } else {
        // Create new expense
        await financeService.createExpense(expenseData);

        // Refresh data
        const [updatedCategories, updatedExpenses] = await Promise.all([
          financeService.getCategories(),
          financeService.getExpenses()
        ]);

        setCategories(updatedCategories);

        const updatedTransactions: Transaction[] = updatedExpenses.map((expense: Expense) => {
          const categoryName = expense.category ? expense.category.name : 'Unknown';
          const isIncome = categoryName.toLowerCase().includes('income');
          const amount = isIncome ? Math.abs(expense.amount) : -Math.abs(expense.amount);

          return {
            id: expense.id,
            date: expense.expenseDate,
            description: expense.title,
            amount: amount,
            category: categoryName,
            categoryId: expense.category?.id
          };
        });
        setTransactions(updatedTransactions);
      }

      handleCloseDialog();
    } catch (err) {
      console.error('Error saving transaction:', err);
      alert('Failed to save transaction');
    }
  };

  const handleDeleteTransaction = async (id: string) => {
    if (confirm("Are you sure you want to delete this transaction?")) {
      try {
        await financeService.deleteExpense(id);

        // Refresh data
        const updatedExpenses = await financeService.getExpenses();

        const updatedTransactions: Transaction[] = updatedExpenses.map((expense: Expense) => {
          const categoryName = expense.category ? expense.category.name : 'Unknown';
          const isIncome = categoryName.toLowerCase().includes('income');
          const amount = isIncome ? Math.abs(expense.amount) : -Math.abs(expense.amount);

          return {
            id: expense.id,
            date: expense.expenseDate,
            description: expense.title,
            amount: amount,
            category: categoryName,
            categoryId: expense.category?.id
          };
        });
        setTransactions(updatedTransactions);
      } catch (err) {
        console.error('Error deleting transaction:', err);
        alert('Failed to delete transaction');
      }
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      {error && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}



      {/* Summary Cards */}
      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 3, mb: 4 }}>
        <Box sx={{ flex: '1 1 300px', minWidth: 250 }}>
          <Card elevation={0} sx={{ borderRadius: 3, height: '100%', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <SavingsIcon sx={{ color: '#3b82f6', mr: 1 }} />
                <Typography variant="subtitle1" fontWeight={600} sx={{ color: '#94a3b8' }}>
                  Current Balance
                </Typography>
              </Box>
              <Typography
                variant="h4"
                sx={{
                  color: totalBalance >= 0 ? '#38b2ac' : '#e53e3e',
                  fontWeight: 700,
                  fontSize: { xs: '1.5rem', sm: '2rem' }
                }}
              >
                {formatCurrency(totalBalance)}
              </Typography>
            </CardContent>
          </Card>
        </Box>

        <Box sx={{ flex: '1 1 300px', minWidth: 250 }}>
          <Card elevation={0} sx={{ borderRadius: 3, height: '100%', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <TrendingUpIcon sx={{ color: '#10b981', mr: 1 }} />
                <Typography variant="subtitle1" fontWeight={600} sx={{ color: '#94a3b8' }}>
                  Total Income
                </Typography>
              </Box>
              <Typography
                variant="h4"
                sx={{
                  color: '#10b981',
                  fontWeight: 700,
                  fontSize: { xs: '1.5rem', sm: '2rem' }
                }}
              >
                {formatCurrency(totalIncome)}
              </Typography>
            </CardContent>
          </Card>
        </Box>

        <Box sx={{ flex: '1 1 300px', minWidth: 250 }}>
          <Card elevation={0} sx={{ borderRadius: 3, height: '100%', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <TrendingDownIcon sx={{ color: '#ef4444', mr: 1 }} />
                <Typography variant="subtitle1" fontWeight={600} sx={{ color: '#94a3b8' }}>
                  Total Expenses
                </Typography>
              </Box>
              <Typography
                variant="h4"
                sx={{
                  color: '#ef4444',
                  fontWeight: 700,
                  fontSize: { xs: '1.5rem', sm: '2rem' }
                }}
              >
                {formatCurrency(totalExpenses)}
              </Typography>
            </CardContent>
          </Card>
        </Box>
      </Box>

      {/* Transactions Table */}
      <Paper elevation={0} sx={{ borderRadius: 3, overflow: 'hidden', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
        <Box sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          p: 3,
          borderBottom: '1px solid #1e293b'
        }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <ReceiptIcon sx={{ mr: 1, color: '#3b82f6' }} />
            <Typography variant="h6" fontWeight={600} sx={{ color: '#ffffff' }}>
              Recent Transactions
            </Typography>
          </Box>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => handleOpenDialog()}
            sx={{
              bgcolor: '#3b82f6',
              borderRadius: 2,
              textTransform: 'none',
              '&:hover': { bgcolor: '#2563eb' }
            }}
          >
            Add Transaction
          </Button>
        </Box>

        <TableContainer sx={{ maxHeight: isMobile ? 400 : 600 }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 600, color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>Date</TableCell>
                <TableCell sx={{ fontWeight: 600, color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>Description</TableCell>
                {!isMobile && <TableCell sx={{ fontWeight: 600, color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>Category</TableCell>}
                <TableCell align="right" sx={{ fontWeight: 600, color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>Amount</TableCell>
                <TableCell align="center" sx={{ fontWeight: 600, color: '#94a3b8', borderBottom: '1px solid #1e293b' }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions
                .sort((a, b) => {
                  const dateA = new Date(a.date);
                  const dateB = new Date(b.date);

                  // Handle invalid dates by putting them at the end
                  if (isNaN(dateA.getTime()) && isNaN(dateB.getTime())) return 0;
                  if (isNaN(dateA.getTime())) return 1;
                  if (isNaN(dateB.getTime())) return -1;

                  return dateB.getTime() - dateA.getTime();
                })
                .map((transaction) => (
                  <TableRow key={transaction.id} hover sx={{ '&:hover': { bgcolor: 'rgba(255,255,255,0.05) !important' } }}>
                    <TableCell sx={{ color: '#ffffff', borderBottom: '1px solid #1e293b' }}>
                      {(() => {
                        try {
                          const date = new Date(transaction.date);
                          if (isNaN(date.getTime())) {
                            return 'Invalid Date';
                          }
                          return date.toLocaleDateString();
                        } catch (error) {
                          return 'Invalid Date';
                        }
                      })()}
                    </TableCell>
                    <TableCell sx={{ color: '#ffffff', borderBottom: '1px solid #1e293b' }}>
                      <Box>
                        <Typography variant="body2" fontWeight={500}>
                          {transaction.description}
                        </Typography>
                        {isMobile && (
                          <Chip
                            label={transaction.category}
                            size="small"
                            sx={{
                              mt: 0.5,
                              bgcolor: transaction.amount > 0 ? 'rgba(56, 178, 172, 0.1)' : 'rgba(229, 62, 62, 0.1)',
                              color: transaction.amount > 0 ? '#38b2ac' : '#e53e3e',
                              fontSize: '0.75rem'
                            }}
                          />
                        )}
                      </Box>
                    </TableCell>
                    {!isMobile && (
                      <TableCell sx={{ borderBottom: '1px solid #1e293b' }}>
                        <Chip
                          label={transaction.category}
                          size="small"
                          sx={{
                            bgcolor: transaction.amount > 0 ? 'rgba(56, 178, 172, 0.1)' : 'rgba(229, 62, 62, 0.1)',
                            color: transaction.amount > 0 ? '#38b2ac' : '#e53e3e',
                            fontWeight: 500
                          }}
                        />
                      </TableCell>
                    )}
                    <TableCell align="right" sx={{ borderBottom: '1px solid #1e293b' }}>
                      <Typography
                        variant="body1"
                        fontWeight={600}
                        sx={{
                          color: transaction.amount >= 0 ? '#10b981' : '#ef4444'
                        }}
                      >
                        {formatCurrency(transaction.amount)}
                      </Typography>
                    </TableCell>
                    <TableCell align="center" sx={{ borderBottom: '1px solid #1e293b' }}>
                      <IconButton
                        size="small"
                        onClick={() => handleOpenDialog(transaction)}
                        sx={{ color: '#3b82f6', mr: 0.5 }}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDeleteTransaction(transaction.id)}
                        sx={{ color: '#ef4444' }}
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>

      {/* Add/Edit Transaction Dialog */}
      <Dialog
        open={dialogOpen}
        onClose={handleCloseDialog}
        maxWidth="sm"
        fullWidth
        PaperProps={{
          sx: {
            borderRadius: 4,
            bgcolor: '#1e293b',
            color: '#ffffff',
            border: '1px solid #334155'
          }
        }}
      >
        <DialogTitle sx={{ p: 3, pb: 1 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <ReceiptIcon sx={{ mr: 1, color: '#3b82f6' }} />
              <Typography variant="h6" fontWeight={700} sx={{ color: '#ffffff' }}>
                {editingTransaction ? "Edit Transaction" : "Add New Transaction"}
              </Typography>
            </Box>
            <IconButton onClick={handleCloseDialog} size="small" sx={{ color: '#94a3b8', '&:hover': { color: '#ffffff', bgcolor: 'rgba(255,255,255,0.1)' } }}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent sx={{ p: 3 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, pt: 1 }}>
            <Box>
              <TextField
                name="date"
                label="Date"
                type="date"
                fullWidth
                value={formData.date}
                onChange={handleInputChange}
                InputLabelProps={{ shrink: true }}
                sx={{
                  '& .MuiOutlinedInput-root': {
                    color: '#ffffff',
                    bgcolor: 'rgba(255,255,255,0.05)',
                    '& fieldset': { borderColor: '#334155' },
                    '&:hover fieldset': { borderColor: '#475569' },
                    '&.Mui-focused fieldset': { borderColor: '#3b82f6' },
                    '& input::-webkit-calendar-picker-indicator': {
                      filter: 'invert(1)',
                      cursor: 'pointer'
                    }
                  },
                  '& .MuiInputLabel-root': { color: '#94a3b8' },
                  '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
                }}
              />
            </Box>

            <Box>
              <TextField
                name="description"
                label="Description"
                fullWidth
                value={formData.description}
                onChange={handleInputChange}
                placeholder="e.g., Lunch at cafeteria"
                sx={{
                  '& .MuiOutlinedInput-root': {
                    color: '#ffffff',
                    bgcolor: 'rgba(255,255,255,0.05)',
                    '& fieldset': { borderColor: '#334155' },
                    '&:hover fieldset': { borderColor: '#475569' },
                    '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                  },
                  '& .MuiInputLabel-root': { color: '#94a3b8' },
                  '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
                }}
              />
            </Box>

            <Box sx={{ display: 'flex', gap: 2, flexDirection: { xs: 'column', sm: 'row' } }}>
              <Box sx={{ flex: 1 }}>
                <TextField
                  name="amount"
                  label="Amount"
                  type="number"
                  fullWidth
                  value={formData.amount}
                  onChange={handleInputChange}
                  inputProps={{ step: "0.01", min: "0" }}
                  placeholder="0.00"
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& fieldset': { borderColor: '#334155' },
                      '&:hover fieldset': { borderColor: '#475569' },
                      '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                    },
                    '& .MuiInputLabel-root': { color: '#94a3b8' },
                    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
                  }}
                />
              </Box>

              <Box sx={{ flex: 1 }}>
                <FormControl fullWidth>
                  <InputLabel sx={{ color: '#94a3b8', '&.Mui-focused': { color: '#3b82f6' } }}>Category</InputLabel>
                  <Select
                    name="categoryId"
                    value={formData.categoryId}
                    onChange={(e) => {
                      const selectedCategory = categories.find(cat => cat.id === e.target.value);
                      setFormData(prev => ({
                        ...prev,
                        categoryId: e.target.value,
                        category: selectedCategory?.name || ''
                      }));
                    }}
                    label="Category"
                    sx={{
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& .MuiOutlinedInput-notchedOutline': { borderColor: '#334155' },
                      '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#475569' },
                      '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' },
                      '& .MuiSvgIcon-root': { color: '#94a3b8' }
                    }}
                    MenuProps={{
                      PaperProps: {
                        sx: {
                          bgcolor: '#1e293b',
                          border: '1px solid #334155',
                          '& .MuiMenuItem-root': {
                            color: '#ffffff',
                            '&:hover': { bgcolor: 'rgba(255,255,255,0.05)' },
                            '&.Mui-selected': { bgcolor: 'rgba(59, 130, 246, 0.2)' }
                          }
                        }
                      }
                    }}
                  >
                    {Array.isArray(categories) && categories.length > 0 ? (
                      categories.map(category => (
                        <MenuItem key={category.id} value={category.id}>
                          {category.name}
                        </MenuItem>
                      ))
                    ) : loadingCategories ? (
                      <MenuItem disabled>Loading categories...</MenuItem>
                    ) : (
                      <MenuItem disabled>No categories available</MenuItem>
                    )}
                  </Select>
                </FormControl>
              </Box>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 3, pt: 1, justifyContent: 'flex-end', gap: 1 }}>
          <Button
            onClick={handleCloseDialog}
            sx={{ color: '#94a3b8', '&:hover': { color: '#ffffff', bgcolor: 'rgba(255,255,255,0.05)' } }}
          >
            Cancel
          </Button>
          <Button
            onClick={handleSaveTransaction}
            variant="contained"
            startIcon={loading ? <CircularProgress size={16} color="inherit" /> : <SaveIcon />}
            disabled={loading}
            sx={{
              bgcolor: '#3b82f6',
              '&:hover': { bgcolor: '#2563eb' },
              minWidth: 100
            }}
          >
            {loading ? 'Saving...' : editingTransaction ? 'Update' : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
