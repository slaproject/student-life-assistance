"use client";
import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  Grid2 as Grid,
  Card,
  CardContent,
  LinearProgress,
  Alert,
  CircularProgress,
  Divider,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from "@mui/material";
import {
  TrendingUp as TrendingUpIcon,
  Analytics as AnalyticsIcon,
  Timeline as TimelineIcon,
  Warning as WarningIcon,
  CheckCircle as CheckCircleIcon,
  DonutLarge as DonutLargeIcon
} from "@mui/icons-material";

interface SpendingData {
  category: string;
  amount: number;
  color: string;
  percentage: number;
  icon: string;
}

interface MonthlyData {
  month: string;
  income: number;
  expenses: number;
  balance: number;
  savings: number;
}

interface BudgetAlert {
  category: string;
  spent: number;
  budget: number;
  percentage: number;
  severity: 'low' | 'medium' | 'high';
}

export default function SpendingAnalysisTab() {
  const [timeRange, setTimeRange] = useState('thisMonth');
  const [loading, setLoading] = useState(false);
  const [spendingByCategory, setSpendingByCategory] = useState<SpendingData[]>([]);
  const [monthlyTrends, setMonthlyTrends] = useState<MonthlyData[]>([]);
  const [budgetAlerts, setBudgetAlerts] = useState<BudgetAlert[]>([]);
  const [totalSpending, setTotalSpending] = useState(0);

  // Enhanced sample data with icons
  const sampleSpendingData: SpendingData[] = [
    { category: "Food & Dining", amount: 287.50, color: "#FF6B6B", percentage: 35.2, icon: "restaurant" },
    { category: "Transportation", amount: 156.75, color: "#4ECDC4", percentage: 19.2, icon: "transport" },
    { category: "Education", amount: 189.00, color: "#96CEB4", percentage: 23.1, icon: "education" },
    { category: "Entertainment", amount: 94.25, color: "#45B7D1", percentage: 11.5, icon: "entertainment" },
    { category: "Shopping", amount: 67.80, color: "#BB8FCE", percentage: 8.3, icon: "shopping" },
    { category: "Healthcare", amount: 22.45, color: "#F7DC6F", percentage: 2.7, icon: "healthcare" }
  ];

  const sampleMonthlyData: MonthlyData[] = [
    { month: "Jun 2025", income: 1250, expenses: 845, balance: 405, savings: 125 },
    { month: "Jul 2025", income: 1180, expenses: 920, balance: 260, savings: 118 },
    { month: "Aug 2025", income: 1350, expenses: 1050, balance: 300, savings: 135 },
    { month: "Sep 2025", income: 1280, expenses: 817, balance: 463, savings: 128 }
  ];

  const sampleBudgetAlerts: BudgetAlert[] = [
    { category: "Food & Dining", spent: 287.50, budget: 300, percentage: 95.8, severity: 'high' },
    { category: "Transportation", spent: 156.75, budget: 200, percentage: 78.4, severity: 'medium' },
    { category: "Entertainment", spent: 94.25, budget: 150, percentage: 62.8, severity: 'low' }
  ];

  useEffect(() => {
    setLoading(true);
    setTimeout(() => {
      setSpendingByCategory(sampleSpendingData);
      setMonthlyTrends(sampleMonthlyData);
      setBudgetAlerts(sampleBudgetAlerts);
      setTotalSpending(sampleSpendingData.reduce((sum, item) => sum + item.amount, 0));
      setLoading(false);
    }, 500);
  }, [timeRange, sampleSpendingData, sampleMonthlyData, sampleBudgetAlerts]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const getAlertColor = (severity: string) => {
    switch (severity) {
      case 'high': return '#e53e3e';
      case 'medium': return '#f6ad55';
      case 'low': return '#38b2ac';
      default: return '#666';
    }
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
      {/* Header with Time Range Selector */}
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center', 
        mb: 3,
        flexDirection: { xs: 'column', sm: 'row' },
        gap: 2
      }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <AnalyticsIcon sx={{ mr: 1, color: '#667eea' }} />
          <Typography variant="h6" fontWeight={600}>
            Spending Analysis
          </Typography>
        </Box>
        
        <FormControl size="small" sx={{ minWidth: 150 }}>
          <InputLabel>Time Range</InputLabel>
          <Select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            label="Time Range"
          >
            <MenuItem value="thisMonth">This Month</MenuItem>
            <MenuItem value="last3Months">Last 3 Months</MenuItem>
            <MenuItem value="last6Months">Last 6 Months</MenuItem>
            <MenuItem value="thisYear">This Year</MenuItem>
          </Select>
        </FormControl>
      </Box>

      <Grid container spacing={3}>
        {/* Spending by Category */}
        <Grid xs={12} lg={8}>
          <Paper elevation={2} sx={{ p: 3, borderRadius: 3, height: '100%' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <DonutLargeIcon sx={{ mr: 1, color: '#667eea' }} />
              <Typography variant="h6" fontWeight={600}>
                Spending Breakdown
              </Typography>
            </Box>

            <Grid container spacing={2}>
              {spendingByCategory.map((item, index) => (
                <Grid xs={12} sm={6} key={index}>
                  <Box sx={{ 
                    p: 2, 
                    border: '1px solid rgba(0,0,0,0.1)', 
                    borderRadius: 2,
                    transition: 'transform 0.2s, box-shadow 0.2s',
                    '&:hover': {
                      transform: 'translateY(-2px)',
                      boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
                    }
                  }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="body1" fontWeight={500}>
                        {item.category}
                      </Typography>
                      <Typography variant="body1" fontWeight={600} color={item.color}>
                        {formatCurrency(item.amount)}
                      </Typography>
                    </Box>
                    
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <LinearProgress
                        variant="determinate"
                        value={item.percentage}
                        sx={{
                          flexGrow: 1,
                          height: 8,
                          borderRadius: 4,
                          bgcolor: 'rgba(0,0,0,0.1)',
                          '& .MuiLinearProgress-bar': {
                            bgcolor: item.color,
                            borderRadius: 4
                          }
                        }}
                      />
                      <Typography variant="body2" color="text.secondary" sx={{ minWidth: 35 }}>
                        {item.percentage}%
                      </Typography>
                    </Box>
                  </Box>
                </Grid>
              ))}
            </Grid>

            <Divider sx={{ my: 3 }} />

            <Box sx={{ 
              bgcolor: 'rgba(102, 126, 234, 0.05)', 
              p: 2, 
              borderRadius: 2,
              border: '1px solid rgba(102, 126, 234, 0.1)'
            }}>
              <Typography variant="h6" color="#667eea" fontWeight={600}>
                Total Spending: {formatCurrency(totalSpending)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Average per category: {formatCurrency(totalSpending / (spendingByCategory.length || 1))}
              </Typography>
            </Box>
          </Paper>
        </Grid>

        {/* Budget Alerts & Monthly Trends */}
        <Grid xs={12} lg={4}>
          {/* Budget Alerts */}
          <Paper elevation={2} sx={{ p: 3, borderRadius: 3, mb: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <WarningIcon sx={{ mr: 1, color: '#f6ad55' }} />
              <Typography variant="h6" fontWeight={600}>
                Budget Alerts
              </Typography>
            </Box>

            {budgetAlerts.length > 0 ? (
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                {budgetAlerts.map((alert, index) => (
                  <Alert 
                    key={index}
                    severity={alert.severity === 'high' ? 'error' : alert.severity === 'medium' ? 'warning' : 'info'}
                    sx={{ 
                      borderRadius: 2,
                      '& .MuiAlert-icon': {
                        alignItems: 'center'
                      }
                    }}
                  >
                    <Box>
                      <Typography variant="body2" fontWeight={600}>
                        {alert.category}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {formatCurrency(alert.spent)} of {formatCurrency(alert.budget)} ({alert.percentage.toFixed(1)}%)
                      </Typography>
                      <LinearProgress
                        variant="determinate"
                        value={Math.min(alert.percentage, 100)}
                        sx={{
                          mt: 1,
                          height: 4,
                          borderRadius: 2,
                          '& .MuiLinearProgress-bar': {
                            bgcolor: getAlertColor(alert.severity)
                          }
                        }}
                      />
                    </Box>
                  </Alert>
                ))}
              </Box>
            ) : (
              <Typography variant="body2" color="text.secondary">
                No budget alerts at this time
              </Typography>
            )}
          </Paper>

          {/* Monthly Trends */}
          <Paper elevation={2} sx={{ p: 3, borderRadius: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <TimelineIcon sx={{ mr: 1, color: '#667eea' }} />
              <Typography variant="h6" fontWeight={600}>
                Monthly Trends
              </Typography>
            </Box>

            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
              {monthlyTrends.map((month, index) => (
                <Box 
                  key={index}
                  sx={{ 
                    p: 2, 
                    border: '1px solid rgba(0,0,0,0.1)', 
                    borderRadius: 2,
                    bgcolor: 'rgba(102, 126, 234, 0.02)'
                  }}
                >
                  <Typography variant="body1" fontWeight={600} sx={{ mb: 1 }}>
                    {month.month}
                  </Typography>
                  
                  <Grid container spacing={1}>
                    <Grid xs={4}>
                      <Typography variant="body2" color="text.secondary">
                        Income
                      </Typography>
                      <Typography variant="body2" fontWeight={600} color="#38b2ac">
                        {formatCurrency(month.income)}
                      </Typography>
                    </Grid>
                    <Grid xs={4}>
                      <Typography variant="body2" color="text.secondary">
                        Expenses
                      </Typography>
                      <Typography variant="body2" fontWeight={600} color="#e53e3e">
                        {formatCurrency(month.expenses)}
                      </Typography>
                    </Grid>
                    <Grid xs={4}>
                      <Typography variant="body2" color="text.secondary">
                        Balance
                      </Typography>
                      <Typography 
                        variant="body2" 
                        fontWeight={600} 
                        color={month.balance >= 0 ? '#38b2ac' : '#e53e3e'}
                      >
                        {formatCurrency(month.balance)}
                      </Typography>
                    </Grid>
                  </Grid>
                </Box>
              ))}
            </Box>
          </Paper>
        </Grid>

        {/* Financial Insights */}
        <Grid xs={12}>
          <Paper elevation={2} sx={{ p: 3, borderRadius: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <TrendingUpIcon sx={{ mr: 1, color: '#667eea' }} />
              <Typography variant="h6" fontWeight={600}>
                Financial Insights
              </Typography>
            </Box>

            <Grid container spacing={3}>
              <Grid xs={12} md={4}>
                <Card sx={{ 
                  bgcolor: 'rgba(56, 178, 172, 0.05)', 
                  border: '1px solid rgba(56, 178, 172, 0.2)',
                  height: '100%'
                }}>
                  <CardContent>
                    <Typography variant="h6" color="#38b2ac" fontWeight={600} gutterBottom>
                      💡 Tip
                    </Typography>
                    <Typography variant="body2">
                      You&apos;re spending most on food &amp; dining. Consider meal prep or cooking at home to save money.
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
              
              <Grid xs={12} md={4}>
                <Card sx={{ 
                  bgcolor: 'rgba(102, 126, 234, 0.05)', 
                  border: '1px solid rgba(102, 126, 234, 0.2)',
                  height: '100%'
                }}>
                  <CardContent>
                    <Typography variant="h6" color="#667eea" fontWeight={600} gutterBottom>
                      📊 Trend
                    </Typography>
                    <Typography variant="body2">
                      Your spending has decreased by 12% compared to last month. Great job maintaining control!
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
              
              <Grid xs={12} md={4}>
                <Card sx={{ 
                  bgcolor: 'rgba(245, 101, 101, 0.05)', 
                  border: '1px solid rgba(245, 101, 101, 0.2)',
                  height: '100%'
                }}>
                  <CardContent>
                    <Typography variant="h6" color="#f56565" fontWeight={600} gutterBottom>
                      ⚠️ Alert
                    </Typography>
                    <Typography variant="body2">
                      You&apos;re close to your food budget limit. Consider adjusting your spending for the rest of the month.
                    </Typography>
                  </CardContent>
                </Card>
              </Grid>
            </Grid>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}
