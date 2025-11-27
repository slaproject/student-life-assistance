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
  ViewColumn as ViewColumnIcon,
  BarChart as BarChartIcon,
  Timeline as TimelineIcon
} from "@mui/icons-material";
import TaskBoard from "./TaskBoard";

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
      id={`task-tabpanel-${index}`}
      aria-labelledby={`task-tab-${index}`}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
}

function a11yProps(index: number) {
  return {
    id: `task-tab-${index}`,
    'aria-controls': `task-tabpanel-${index}`,
  };
}

export default function TaskTabs() {
  const [activeTab, setActiveTab] = useState(0);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  return (
    <Box sx={{
      minHeight: '100vh',
      bgcolor: '#000000',
      color: '#ffffff'
    }}>


      {/* Tab Navigation */}
      <Paper
        elevation={0}
        sx={{
          borderRadius: 0,
          bgcolor: '#0a0a0a',
          borderBottom: '1px solid',
          borderColor: '#1e293b'
        }}
      >
        <Box sx={{ maxWidth: 1400, mx: 'auto' }}>
          <Tabs
            value={activeTab}
            onChange={handleTabChange}
            aria-label="task management tabs"
            variant={isMobile ? "fullWidth" : "standard"}
            sx={{
              '& .MuiTab-root': {
                minHeight: { xs: 60, sm: 72 },
                fontSize: { xs: '0.875rem', sm: '1rem' },
                fontWeight: 600,
                textTransform: 'none',
                color: '#94a3b8',
                transition: 'all 0.2s ease',
                '&.Mui-selected': {
                  color: '#3b82f6',
                  fontWeight: 700,
                },
                '&:hover': {
                  color: '#60a5fa',
                  bgcolor: 'rgba(59, 130, 246, 0.1)'
                }
              },
              '& .MuiTabs-indicator': {
                backgroundColor: '#3b82f6',
                height: 3,
                borderRadius: '3px 3px 0 0',
              }
            }}
          >
            <Tab
              icon={<ViewColumnIcon />}
              label="Kanban Board"
              iconPosition="start"
              sx={{
                flexDirection: 'row',
                gap: 1,
                minWidth: { xs: 120, sm: 180 }
              }}
              {...a11yProps(0)}
            />
            <Tab
              icon={<TimelineIcon />}
              label="Timeline View"
              iconPosition="start"
              sx={{
                flexDirection: 'row',
                gap: 1,
                minWidth: { xs: 120, sm: 160 }
              }}
              {...a11yProps(1)}
            />
            <Tab
              icon={<BarChartIcon />}
              label="Analytics"
              iconPosition="start"
              sx={{
                flexDirection: 'row',
                gap: 1,
                minWidth: { xs: 120, sm: 140 }
              }}
              {...a11yProps(2)}
            />
          </Tabs>
        </Box>
      </Paper>

      {/* Tab Content */}
      <Box sx={{ maxWidth: 1400, mx: 'auto' }}>
        <TabPanel value={activeTab} index={0}>
          <TaskBoard />
        </TabPanel>

        <TabPanel value={activeTab} index={1}>
          <Box sx={{
            p: 4,
            textAlign: 'center',
            minHeight: 400,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            bgcolor: '#0a0a0a',
            m: 3,
            borderRadius: 3,
            border: '1px solid #1e293b',
            boxShadow: '0 4px 20px rgba(0,0,0,0.2)'
          }}>
            <TimelineIcon sx={{ fontSize: 64, color: '#334155', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 600, mb: 1, color: '#ffffff' }}>
              Timeline View
            </Typography>
            <Typography variant="body1" sx={{ color: '#94a3b8' }}>
              Coming Soon - View your tasks in a timeline format
            </Typography>
          </Box>
        </TabPanel>

        <TabPanel value={activeTab} index={2}>
          <Box sx={{
            p: 4,
            textAlign: 'center',
            minHeight: 400,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            bgcolor: '#0a0a0a',
            m: 3,
            borderRadius: 3,
            border: '1px solid #1e293b',
            boxShadow: '0 4px 20px rgba(0,0,0,0.2)'
          }}>
            <BarChartIcon sx={{ fontSize: 64, color: '#334155', mb: 2 }} />
            <Typography variant="h5" sx={{ fontWeight: 600, mb: 1, color: '#ffffff' }}>
              Task Analytics
            </Typography>
            <Typography variant="body1" sx={{ color: '#94a3b8' }}>
              Coming Soon - Detailed analytics and insights about your tasks
            </Typography>
          </Box>
        </TabPanel>
      </Box>
    </Box>
  );
}