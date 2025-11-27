"use client";
import React, { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
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
  DonutLarge as DonutLargeIcon
} from "@mui/icons-material";
import { financeService, CategoryWiseExpenses, BudgetAnalysis, SpendingTrend, BudgetAlert as ApiBudgetAlert } from "../../lib/financeService";

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

interface BudgetAlertUI {
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
  const [budgetAlerts, setBudgetAlerts] = useState<BudgetAlertUI[]>([]);
  const [totalSpending, setTotalSpending] = useState(0);
  const [budgetAnalysis, setBudgetAnalysis] = useState<BudgetAnalysis | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchAnalysisData = async () => {
      setLoading(true);
      setError(null);

      try {
        const currentDate = new Date();
        const currentMonth = currentDate.getMonth() + 1;
        const currentYear = currentDate.getFullYear();

        // Fetch all required data in parallel
        const [
          categoryWiseExpenses,
          budgetAnalysisData,
          spendingTrendsData,
          budgetAlertsData
        ] = await Promise.all([
          financeService.getCategoryWiseExpenses(currentMonth, currentYear),
          financeService.getBudgetAnalysis(currentMonth, currentYear),
          financeService.getSpendingTrends(6),
          financeService.getBudgetAlerts()
        ]);

        // Convert category-wise expenses to spending data format
        const colors = ["#FF6B6B", "#4ECDC4", "#96CEB4", "#45B7D1", "#BB8FCE", "#F7DC6F", "#F7DC6F", "#FFA07A"];
        const icons = ["restaurant", "transport", "education", "entertainment", "shopping", "healthcare", "home", "other"];

        const totalSpendingAmount = Object.values(categoryWiseExpenses).reduce((sum, amount) => sum + amount, 0);

        const spendingData: SpendingData[] = Object.entries(categoryWiseExpenses).map(([category, amount], index) => ({
          category,
          amount,
          color: colors[index % colors.length],
          percentage: totalSpendingAmount > 0 ? (amount / totalSpendingAmount) * 100 : 0,
          icon: icons[index % icons.length]
        }));

        // Convert spending trends to monthly data format
        const monthlyData: MonthlyData[] = spendingTrendsData.monthlyTotals.map((trend, index) => {
          const date = new Date(trend.month);
          return {
            month: date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' }),
            income: 0, // We don't have income data from the API
            expenses: trend.total,
            balance: 0, // We don't have balance data
            savings: 0 // We don't have savings data
          };
        });

        // Convert budget alerts to the expected format
        const alertsData: BudgetAlertUI[] = budgetAlertsData.map((alert: ApiBudgetAlert) => ({
          category: alert.categoryName,
          spent: alert.spent,
          budget: alert.budgetLimit,
          percentage: alert.percentageUsed,
          severity: alert.percentageUsed >= 90 ? 'high' : alert.percentageUsed >= 70 ? 'medium' : 'low'
        }));

        setSpendingByCategory(spendingData);
        setMonthlyTrends(monthlyData);
        setBudgetAlerts(alertsData);
        setBudgetAnalysis(budgetAnalysisData);
        setTotalSpending(totalSpendingAmount);

      } catch (err) {
        console.error('Error fetching analysis data:', err);
        setError('Failed to load analysis data. Using sample data.');

        // Fallback to sample data
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

        const sampleBudgetAlerts: BudgetAlertUI[] = [
          { category: "Food & Dining", spent: 287.50, budget: 300, percentage: 95.8, severity: 'high' },
          { category: "Transportation", spent: 156.75, budget: 200, percentage: 78.4, severity: 'medium' },
          { category: "Entertainment", spent: 94.25, budget: 150, percentage: 62.8, severity: 'low' }
        ];

        setSpendingByCategory(sampleSpendingData);
        setMonthlyTrends(sampleMonthlyData);
        setBudgetAlerts(sampleBudgetAlerts);
        setTotalSpending(sampleSpendingData.reduce((sum, item) => sum + item.amount, 0));
      } finally {
        setLoading(false);
      }
    };

    fetchAnalysisData();
  }, [timeRange]);

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
          <AnalyticsIcon sx={{ mr: 1, color: '#3b82f6' }} />
          <Typography variant="h6" fontWeight={600} sx={{ color: '#ffffff' }}>
            Spending Analysis
          </Typography>
        </Box>

        <FormControl size="small" sx={{ minWidth: 150 }}>
          <InputLabel sx={{ color: '#94a3b8' }}>Time Range</InputLabel>
          <Select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            label="Time Range"
            sx={{
              color: '#ffffff',
              '.MuiOutlinedInput-notchedOutline': { borderColor: '#334155' },
              '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: '#475569' },
              '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: '#3b82f6' },
              '.MuiSvgIcon-root': { color: '#94a3b8' }
            }}
          >
            <MenuItem value="thisMonth">This Month</MenuItem>
            <MenuItem value="last3Months">Last 3 Months</MenuItem>
            <MenuItem value="last6Months">Last 6 Months</MenuItem>
            <MenuItem value="thisYear">This Year</MenuItem>
          </Select>
        </FormControl>
      </Box>

      {/* Error Display */}
      {error && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Main Content Layout */}
      <Box sx={{ display: 'flex', flexDirection: { xs: 'column', lg: 'row' }, gap: 3 }}>
        {/* Spending by Category */}
        <Box sx={{ flex: 2 }}>
          <Paper elevation={0} sx={{ p: 3, borderRadius: 3, height: '100%', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <DonutLargeIcon sx={{ mr: 1, color: '#3b82f6' }} />
              <Typography variant="h6" fontWeight={600}>
                Spending Breakdown
              </Typography>
            </Box>

            <Box sx={{
              display: 'grid',
              gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' },
              gap: 2,
              mb: 3
            }}>
              {spendingByCategory.map((item, index) => (
                <Box
                  key={index}
                  sx={{
                    p: 2,
                    border: '1px solid #1e293b',
                    borderRadius: 2,
                    transition: 'transform 0.2s, box-shadow 0.2s',
                    '&:hover': {
                      transform: 'translateY(-2px)',
                      boxShadow: '0 4px 12px rgba(0,0,0,0.5)',
                      bgcolor: 'rgba(255,255,255,0.05)'
                    }
                  }}
                >
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
                        bgcolor: 'rgba(255,255,255,0.1)',
                        '& .MuiLinearProgress-bar': {
                          bgcolor: item.color,
                          borderRadius: 4
                        }
                      }}
                    />
                    <Typography variant="body2" sx={{ minWidth: 35, color: '#94a3b8' }}>
                      {item.percentage.toFixed(2)}%
                    </Typography>
                  </Box>
                </Box>
              ))}
            </Box>

            <Divider sx={{ my: 3, borderColor: '#1e293b' }} />

            <Box sx={{
              bgcolor: 'rgba(59, 130, 246, 0.1)',
              p: 2,
              borderRadius: 2,
              border: '1px solid rgba(59, 130, 246, 0.2)'
            }}>
              <Typography variant="h6" color="#3b82f6" fontWeight={600}>
                Total Spending: {formatCurrency(totalSpending)}
              </Typography>
              <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                Average per category: {formatCurrency(totalSpending / (spendingByCategory.length || 1))}
              </Typography>
            </Box>
          </Paper>
        </Box>

        {/* Budget Alerts & Monthly Trends */}
        <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* Budget Alerts */}
          <Paper elevation={0} sx={{ p: 3, borderRadius: 3, bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
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
                      <Typography variant="body2" color="text.secondary" sx={{ color: '#94a3b8' }}>
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
              <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                No budget alerts at this time
              </Typography>
            )}
          </Paper>

          {/* Monthly Trends */}
          <Paper elevation={0} sx={{ p: 3, borderRadius: 3, bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <TimelineIcon sx={{ mr: 1, color: '#3b82f6' }} />
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
                    border: '1px solid #1e293b',
                    borderRadius: 2,
                    bgcolor: 'rgba(59, 130, 246, 0.05)'
                  }}
                >
                  <Typography variant="body1" fontWeight={600} sx={{ mb: 1 }}>
                    {month.month}
                  </Typography>

                  <Box sx={{ display: 'flex', gap: 1 }}>
                    <Box sx={{ flex: 1 }}>
                      <Typography variant="body2" color="text.secondary">
                        Income
                      </Typography>
                      <Typography variant="body2" fontWeight={600} color="#38b2ac">
                        {formatCurrency(month.income)}
                      </Typography>
                    </Box>
                    <Box sx={{ flex: 1 }}>
                      <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                        Expenses
                      </Typography>
                      <Typography variant="body2" fontWeight={600} color="#ef4444">
                        {formatCurrency(month.expenses)}
                      </Typography>
                    </Box>
                    <Box sx={{ flex: 1 }}>
                      <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                        Balance
                      </Typography>
                      <Typography
                        variant="body2"
                        fontWeight={600}
                        color={month.balance >= 0 ? '#10b981' : '#ef4444'}
                      >
                        {formatCurrency(month.balance)}
                      </Typography>
                    </Box>
                  </Box>
                </Box>
              ))}
            </Box>
          </Paper>
        </Box>
      </Box>

      {/* Financial Insights */}
      <Box sx={{ mt: 3 }}>
        <Paper elevation={0} sx={{ p: 3, borderRadius: 3, bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <TrendingUpIcon sx={{ mr: 1, color: '#3b82f6' }} />
            <Typography variant="h6" fontWeight={600}>
              Financial Insights
            </Typography>
          </Box>

          <Box sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', md: '1fr 1fr 1fr' },
            gap: 3
          }}>
            <Card sx={{
              bgcolor: 'rgba(16, 185, 129, 0.05)',
              border: '1px solid rgba(16, 185, 129, 0.2)',
              height: '100%'
            }}>
              <CardContent>
                <Typography variant="h6" color="#10b981" fontWeight={600} gutterBottom>
                  💡 Tip
                </Typography>
                <Typography variant="body2" sx={{ color: '#ffffff' }}>
                  You&apos;re spending most on food &amp; dining. Consider meal prep or cooking at home to save money.
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{
              bgcolor: 'rgba(59, 130, 246, 0.05)',
              border: '1px solid rgba(59, 130, 246, 0.2)',
              height: '100%'
            }}>
              <CardContent>
                <Typography variant="h6" color="#3b82f6" fontWeight={600} gutterBottom>
                  📊 Trend
                </Typography>
                <Typography variant="body2" sx={{ color: '#ffffff' }}>
                  Your spending has decreased by 12% compared to last month. Great job maintaining control!
                </Typography>
              </CardContent>
            </Card>

            <Card sx={{
              bgcolor: 'rgba(239, 68, 68, 0.05)',
              border: '1px solid rgba(239, 68, 68, 0.2)',
              height: '100%'
            }}>
              <CardContent>
                <Typography variant="h6" color="#ef4444" fontWeight={600} gutterBottom>
                  ⚠️ Alert
                </Typography>
                <Typography variant="body2" sx={{ color: '#ffffff' }}>
                  You&apos;re close to your food budget limit. Consider adjusting your spending for the rest of the month.
                </Typography>
              </CardContent>
            </Card>
          </Box>
        </Paper>
      </Box>
    </Box>
  );
}
