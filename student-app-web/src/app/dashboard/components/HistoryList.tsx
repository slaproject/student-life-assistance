"use client";

import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    Paper,
    List,
    ListItem,
    ListItemText,
    Chip,
    CircularProgress,
    Divider,
    Collapse,
    IconButton
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import HistoryIcon from '@mui/icons-material/History';
import { summaryService } from '../../lib/api';

interface HistoryItem {
    id: string;
    requestContent: string;
    responseSummary: string;
    requestType: string;
    createdAt: string;
}

export default function HistoryList() {
    const [history, setHistory] = useState<HistoryItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [expandedId, setExpandedId] = useState<string | null>(null);

    useEffect(() => {
        fetchHistory();
    }, []);

    const fetchHistory = async () => {
        try {
            const data = await summaryService.getHistory();
            setHistory(data);
        } catch (error) {
            console.error("Failed to fetch history", error);
        } finally {
            setLoading(false);
        }
    };

    const handleExpand = (id: string) => {
        setExpandedId(expandedId === id ? null : id);
    };

    if (loading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
                <CircularProgress />
            </Box>
        );
    }

    if (history.length === 0) {
        return (

            <Paper elevation={0} className="dashboard-card" sx={{ p: 4, textAlign: 'center', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
                <HistoryIcon sx={{ fontSize: 48, color: '#94a3b8', mb: 2, opacity: 0.5 }} />
                <Typography variant="h6" sx={{ color: '#94a3b8' }}>
                    No history yet
                </Typography>
                <Typography variant="body2" sx={{ color: '#64748b' }}>
                    Your generated summaries will appear here.
                </Typography>
            </Paper>
        );

    }

    return (
        <Paper elevation={0} className="dashboard-card" sx={{ p: 0, overflow: 'hidden', bgcolor: '#0a0a0a', border: '1px solid #1e293b', color: '#ffffff' }}>
            <Box sx={{ p: 3, borderBottom: '1px solid', borderColor: '#1e293b', bgcolor: 'rgba(255, 255, 255, 0.05)' }}>
                <Typography variant="h6" fontWeight="bold" sx={{ display: 'flex', alignItems: 'center', gap: 1, color: '#ffffff' }}>
                    <HistoryIcon sx={{ color: '#3b82f6' }} />
                    Recent Explanations
                </Typography>
            </Box>
            <List sx={{ p: 0 }}>
                {history.map((item, index) => (
                    <React.Fragment key={item.id}>
                        <ListItem
                            alignItems="flex-start"
                            sx={{
                                p: 3,
                                cursor: 'pointer',
                                '&:hover': { bgcolor: 'rgba(255,255,255,0.05)' }
                            }}
                            onClick={() => handleExpand(item.id)}
                        >
                            <ListItemText
                                primary={
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                                        <Chip
                                            label={item.requestType}
                                            size="small"
                                            color={item.requestType === 'YOUTUBE' ? 'error' : 'primary'}
                                            variant="outlined"
                                            sx={{ fontSize: '0.7rem', height: 20 }}
                                        />
                                        <Typography variant="caption" sx={{ color: '#94a3b8' }}>
                                            {new Date(item.createdAt).toLocaleDateString()}
                                        </Typography>
                                    </Box>
                                }
                                secondary={
                                    <Box>
                                        <Typography
                                            variant="subtitle1"
                                            color="#ffffff"
                                            fontWeight="500"
                                            sx={{
                                                display: '-webkit-box',
                                                WebkitLineClamp: 2,
                                                WebkitBoxOrient: 'vertical',
                                                overflow: 'hidden',
                                                mb: 1
                                            }}
                                        >
                                            {item.requestContent}
                                        </Typography>
                                        <Collapse in={expandedId === item.id}>
                                            <Paper
                                                variant="outlined"
                                                sx={{
                                                    p: 2,
                                                    mt: 2,
                                                    bgcolor: '#0f172a',
                                                    borderRadius: 2,
                                                    border: '1px dashed #334155'
                                                }}
                                            >
                                                <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', color: '#cbd5e1' }}>
                                                    {item.responseSummary}
                                                </Typography>
                                            </Paper>
                                        </Collapse>
                                        {!expandedId && (
                                            <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                                                <Typography variant="caption" sx={{ display: 'flex', alignItems: 'center', gap: 0.5, color: '#3b82f6' }}>
                                                    View Explanation <ExpandMoreIcon fontSize="small" />
                                                </Typography>
                                            </Box>
                                        )}
                                    </Box>
                                }
                            />
                        </ListItem>
                        {index < history.length - 1 && <Divider component="li" sx={{ borderColor: '#1e293b' }} />}
                    </React.Fragment>
                ))}
            </List>
        </Paper>
    );
}
