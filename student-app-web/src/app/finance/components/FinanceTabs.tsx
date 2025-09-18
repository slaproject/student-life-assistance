"use client";
import React, { useState } from "react";
import {
  Box,
  Tabs,
  Tab,
  Typography,
  Paper,
  useTheme,
  useMediaQuery
} from "@mui/material";
import {
  AttachMoney as AttachMoneyIcon,
  Receipt as ReceiptIcon,
  Analytics as AnalyticsIcon
} from "@mui/icons-material";
import ExpensesTab from "./ExpensesTab";
import SpendingAnalysisTab from "./SpendingAnalysisTab";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel({ children, value, index }: TabPanelProps) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`finance-tabpanel-${index}`}
      aria-labelledby={`finance-tab-${index}`}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

function a11yProps(index: number) {
  return {
    id: `finance-tab-${index}`,
    'aria-controls': `finance-tabpanel-${index}`,
  };
}

export default function FinanceTabs() {
  const [activeTab, setActiveTab] = useState(0); // 0 = Spending Analysis (default)
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  return (
    <Box sx={{
      maxWidth: 1400,
      mx: 'auto',
      p: { xs: 2, sm: 3 },
      background: 'linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(102, 126, 234, 0.05))',
      minHeight: 'calc(100vh - 64px)',
      borderRadius: 2
    }}>
      {/* Header */}
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <AttachMoneyIcon 
          sx={{ 
            mr: 2, 
            fontSize: { xs: 28, sm: 32 }, 
            color: '#667eea' 
          }} 
        />
        <Typography 
          variant={isMobile ? "h5" : "h4"} 
          sx={{ 
            color: '#2C3E50', 
            fontWeight: 600,
            flexGrow: 1 
          }}
        >
          Student Finance Tracker
        </Typography>
      </Box>

      {/* Tab Navigation */}
      <Paper 
        elevation={3} 
        sx={{ 
          borderRadius: 3,
          overflow: 'hidden',
          boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
        }}
      >
        <Tabs
          value={activeTab}
          onChange={handleTabChange}
          aria-label="finance tabs"
          variant={isMobile ? "fullWidth" : "standard"}
          sx={{
            borderBottom: 1,
            borderColor: 'divider',
            bgcolor: '#ffffff',
            '& .MuiTab-root': {
              minHeight: { xs: 60, sm: 72 },
              fontSize: { xs: '0.875rem', sm: '1rem' },
              fontWeight: 600,
              textTransform: 'none',
              color: '#666',
              '&.Mui-selected': {
                color: '#667eea',
              }
            },
            '& .MuiTabs-indicator': {
              backgroundColor: '#667eea',
              height: 3,
            }
          }}
        >
          <Tab
            icon={<AnalyticsIcon />}
            label="Spending Analysis"
            iconPosition="start"
            sx={{
              flexDirection: 'row',
              gap: 1,
              minWidth: { xs: 120, sm: 180 }
            }}
            {...a11yProps(0)}
          />
          <Tab
            icon={<ReceiptIcon />}
            label="Expenses"
            iconPosition="start"
            sx={{
              flexDirection: 'row',
              gap: 1,
              minWidth: { xs: 120, sm: 140 }
            }}
            {...a11yProps(1)}
          />
        </Tabs>

        {/* Tab Content */}
        <Box sx={{ bgcolor: '#f8f9ff' }}>
          <TabPanel value={activeTab} index={0}>
            <SpendingAnalysisTab />
          </TabPanel>
          <TabPanel value={activeTab} index={1}>
            <ExpensesTab />
          </TabPanel>
        </Box>
      </Paper>
    </Box>
  );
}
