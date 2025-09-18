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
  Receipt as ReceiptIcon
} from "@mui/icons-material";
import { getApiClient } from "../../lib/api";

// Interface definitions
interface Transaction {
  id: number;
  date: string;
  description: string;
  amount: number;
  category: string;
}

interface Category {
  id: number;
  name: string;
  userId: number;
  color?: string;
  icon?: string;
}

interface Expense {
  id: number;
  userId: number;
  amount: number;
  description: string;
  date: string;
  categoryId: number;
}

// Sample data for fallback
const SAMPLE_TRANSACTIONS: Transaction[] = [
  { id: 1, date: "2025-09-01", description: "Textbooks", amount: -120.50, category: "Education" },
  { id: 2, date: "2025-09-02", description: "Part-time Job", amount: 250.00, category: "Income" },
  { id: 3, date: "2025-09-03", description: "Groceries", amount: -45.75, category: "Food & Dining" },
  { id: 4, date: "2025-08-28", description: "Scholarship", amount: 1000.00, category: "Income" },
  { id: 5, date: "2025-08-25", description: "Rent", amount: -650.00, category: "Housing" },
  { id: 6, date: "2025-08-20", description: "Bus Pass", amount: -75.00, category: "Transportation" }
];

export default function ExpensesTab() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  // const [expenses, setExpenses] = useState<Expense[]>([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null);
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0],
    description: '',
    amount: '',
    category: ''
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  // Fetch data from API
  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const apiClient = getApiClient();

        // Fetch categories and expenses
        const [categoriesResponse, expensesResponse] = await Promise.all([
          apiClient.get('/api/finance/categories'),
          apiClient.get('/api/finance/expenses')
        ]);

        const categoriesData = categoriesResponse.data;
        const expensesData = expensesResponse.data;

  setCategories(categoriesData);

        // Convert expenses to transactions format for UI display
        const transactionsFromExpenses = expensesData.map((expense: Expense) => {
          const category = categoriesData.find((cat: Category) => cat.id === expense.categoryId);
          const categoryName = category ? category.name : 'Unknown';
          const isIncome = categoryName.toLowerCase().includes('income');
          const amount = isIncome ? Math.abs(expense.amount) : -Math.abs(expense.amount);

          return {
            id: expense.id,
            date: expense.date,
            description: expense.description,
            amount: amount,
            category: categoryName
          };
        });

        setTransactions(transactionsFromExpenses);

        if (categoriesData.length > 0) {
          setFormData(prev => ({
            ...prev,
            category: categoriesData[0].name
          }));
        }
      } catch (err) {
        console.error('Error fetching finance data:', err);
        setError('Using sample data - API connection failed');
        setTransactions(SAMPLE_TRANSACTIONS);

        // Create default categories from sample data
        const uniqueCategories = [...new Set(SAMPLE_TRANSACTIONS.map(t => t.category))];
        setCategories(uniqueCategories.map((name, index) => ({
          id: index + 1,
          name,
          userId: 1
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

  const handleOpenDialog = (transaction: Transaction | null = null) => {
    if (transaction) {
      setEditingTransaction(transaction);
      setFormData({
        date: transaction.date,
        description: transaction.description,
        amount: Math.abs(transaction.amount).toString(),
        category: transaction.category
      });
    } else {
      setEditingTransaction(null);
      setFormData({
        date: new Date().toISOString().split('T')[0],
        description: '',
        amount: '',
        category: categories.length > 0 ? categories[0].name : ''
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
    if (!formData.description || !formData.amount || !formData.date || !formData.category) {
      alert("Please fill all required fields");
      return;
    }

    const isExpense = !formData.category.toLowerCase().includes('income');
    const amount = parseFloat(formData.amount) * (isExpense ? -1 : 1);

    const newTransaction: Transaction = {
      id: editingTransaction ? editingTransaction.id : Date.now(),
      date: formData.date,
      description: formData.description,
      amount: amount,
      category: formData.category
    };

    try {
      if (editingTransaction) {
        // Update existing transaction
        setTransactions(prev => 
          prev.map(t => t.id === editingTransaction.id ? newTransaction : t)
        );
      } else {
        // Add new transaction
        setTransactions(prev => [...prev, newTransaction]);
      }

      handleCloseDialog();
    } catch (err) {
      console.error('Error saving transaction:', err);
      alert('Failed to save transaction');
    }
  };

  const handleDeleteTransaction = (id: number) => {
    if (confirm("Are you sure you want to delete this transaction?")) {
      setTransactions(prev => prev.filter(t => t.id !== id));
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
          <Card elevation={2} sx={{ borderRadius: 3, height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <SavingsIcon sx={{ color: '#667eea', mr: 1 }} />
                <Typography variant="subtitle1" fontWeight={600}>
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
          <Card elevation={2} sx={{ borderRadius: 3, height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <TrendingUpIcon sx={{ color: '#38b2ac', mr: 1 }} />
                <Typography variant="subtitle1" fontWeight={600}>
                  Total Income
                </Typography>
              </Box>
              <Typography 
                variant="h4" 
                sx={{
                  color: '#38b2ac',
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
          <Card elevation={2} sx={{ borderRadius: 3, height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <TrendingDownIcon sx={{ color: '#e53e3e', mr: 1 }} />
                <Typography variant="subtitle1" fontWeight={600}>
                  Total Expenses
                </Typography>
              </Box>
              <Typography 
                variant="h4" 
                sx={{
                  color: '#e53e3e',
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
      <Paper elevation={2} sx={{ borderRadius: 3, overflow: 'hidden' }}>
        <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center', 
          p: 3,
          borderBottom: '1px solid rgba(0,0,0,0.1)'
        }}>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <ReceiptIcon sx={{ mr: 1, color: '#667eea' }} />
            <Typography variant="h6" fontWeight={600}>
              Recent Transactions
            </Typography>
          </Box>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => handleOpenDialog()}
            sx={{
              bgcolor: '#667eea',
              borderRadius: 2,
              textTransform: 'none',
              '&:hover': { bgcolor: '#5a6edb' }
            }}
          >
            Add Transaction
          </Button>
        </Box>

        <TableContainer sx={{ maxHeight: isMobile ? 400 : 600 }}>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 600 }}>Date</TableCell>
                <TableCell sx={{ fontWeight: 600 }}>Description</TableCell>
                {!isMobile && <TableCell sx={{ fontWeight: 600 }}>Category</TableCell>}
                <TableCell align="right" sx={{ fontWeight: 600 }}>Amount</TableCell>
                <TableCell align="center" sx={{ fontWeight: 600 }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions
                .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
                .map((transaction) => (
                <TableRow key={transaction.id} hover>
                  <TableCell>
                    {new Date(transaction.date).toLocaleDateString()}
                  </TableCell>
                  <TableCell>
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
                    <TableCell>
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
                  <TableCell align="right">
                    <Typography
                      variant="body1"
                      fontWeight={600}
                      sx={{
                        color: transaction.amount >= 0 ? '#38b2ac' : '#e53e3e'
                      }}
                    >
                      {formatCurrency(transaction.amount)}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    <IconButton
                      size="small"
                      onClick={() => handleOpenDialog(transaction)}
                      sx={{ color: '#667eea', mr: 0.5 }}
                    >
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton
                      size="small"
                      onClick={() => handleDeleteTransaction(transaction.id)}
                      sx={{ color: '#e53e3e' }}
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
        fullScreen={isMobile}
      >
        <DialogTitle sx={{ 
          bgcolor: '#667eea', 
          color: 'white',
          display: 'flex',
          alignItems: 'center'
        }}>
          <ReceiptIcon sx={{ mr: 1 }} />
          {editingTransaction ? "Edit Transaction" : "Add New Transaction"}
        </DialogTitle>
        <DialogContent sx={{ mt: 2 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Box>
              <TextField
                name="date"
                label="Date"
                type="date"
                fullWidth
                value={formData.date}
                onChange={handleInputChange}
                InputLabelProps={{ shrink: true }}
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
              />
            </Box>

            <Box sx={{ width: { xs: '100%', sm: '48%' }, display: 'inline-block' }}>
              <TextField
                name="amount"
                label="Amount"
                type="number"
                fullWidth
                value={formData.amount}
                onChange={handleInputChange}
                inputProps={{ step: "0.01", min: "0" }}
                placeholder="0.00"
              />
            </Box>

            <Box sx={{ width: { xs: '100%', sm: '48%' }, display: 'inline-block', ml: { sm: '4%' } }}>
              <FormControl fullWidth>
                <InputLabel>Category</InputLabel>
                <Select
                  name="category"
                  value={formData.category}
                  onChange={(e) => setFormData(prev => ({ ...prev, category: e.target.value }))}
                  label="Category"
                >
                  {categories.map(category => (
                    <MenuItem key={category.id} value={category.name}>
                      {category.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={handleCloseDialog} color="inherit">
            Cancel
          </Button>
          <Button
            onClick={handleSaveTransaction}
            variant="contained"
            sx={{
              bgcolor: '#667eea',
              '&:hover': { bgcolor: '#5a6edb' }
            }}
          >
            {editingTransaction ? 'Update' : 'Save'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
