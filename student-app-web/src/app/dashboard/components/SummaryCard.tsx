"use client";

import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Typography,
    TextField,
    Button,
    CircularProgress,
    Alert,
    Fade,
    Chip,
    IconButton,
    InputAdornment
} from '@mui/material';
import AutoAwesomeIcon from '@mui/icons-material/AutoAwesome';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { summaryService } from '../../lib/api';

export default function SummaryCard() {
    const [text, setText] = useState('');
    const [summary, setSummary] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [remaining, setRemaining] = useState<number | null>(null);
    const [copied, setCopied] = useState(false);

    useEffect(() => {
        fetchUsage();
    }, []);

    const fetchUsage = async () => {
        try {
            const usage = await summaryService.getUsage();
            setRemaining(usage);
        } catch (err) {
            console.error('Failed to fetch usage:', err);
        }
    };

    const handleSummarize = async () => {
        if (!text.trim()) return;

        setLoading(true);
        setError(null);
        setSummary('');

        try {
            const response = await summaryService.generateSummary(text, 'TEXT');
            setSummary(response.summary);
            setRemaining(response.remainingRequests);
        } catch (err: unknown) {
            console.error('Summary error:', err);
            const error = err as { response?: { status?: number; data?: string } };
            if (error.response?.status === 429) {
                setError("You have reached your monthly limit of 5 requests.");
            } else {
                setError(error.response?.data || "Failed to generate explanation. Please try again.");
            }
        } finally {
            setLoading(false);
        }
    };

    const handleCopy = () => {
        navigator.clipboard.writeText(summary);
        setCopied(true);
        setTimeout(() => setCopied(false), 2000);
    };

    return (
        <Paper
            elevation={0}
            className="dashboard-card"
            sx={{
                p: 4,
                display: 'flex',
                flexDirection: 'column',
                position: 'relative',
                overflow: 'hidden',
                bgcolor: '#0a0a0a',
                border: '1px solid #1e293b',
                color: '#ffffff'
            }}
        >
            <Box sx={{
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                height: '6px',
                background: 'var(--primary-gradient)'
            }} />

            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 4 }}>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Box sx={{
                        p: 1.5,
                        borderRadius: '12px',
                        background: 'rgba(79, 70, 229, 0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}>
                        <AutoAwesomeIcon sx={{ color: 'var(--primary)', fontSize: 28 }} />
                    </Box>
                    <Box>
                        <Typography variant="h5" fontWeight="800" sx={{ color: '#ffffff', letterSpacing: '-0.025em' }}>
                            Explain Me This
                        </Typography>
                        <Typography variant="body2" sx={{ color: '#94a3b8' }}>
                            AI-powered simple explanations
                        </Typography>
                    </Box>
                </Box>
                {remaining !== null && (
                    <Chip
                        label={`${remaining} left`}
                        size="medium"
                        sx={{
                            fontWeight: 600,
                            bgcolor: remaining > 0 ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                            color: remaining > 0 ? 'var(--success)' : 'var(--danger)',
                            border: 'none'
                        }}
                    />
                )}
            </Box>

            <TextField
                multiline
                rows={6}
                fullWidth
                placeholder="Paste any complex text here, and I'll explain it in simple terms..."
                value={text}
                onChange={(e) => setText(e.target.value)}
                disabled={loading || (remaining === 0 && !summary)}
                sx={{
                    mb: 3,
                    '& .MuiOutlinedInput-root': {
                        borderRadius: '16px',
                        backgroundColor: 'rgba(255, 255, 255, 0.05)',
                        color: '#ffffff',
                        transition: 'all 0.2s ease',
                        '& fieldset': {
                            borderColor: '#334155',
                        },
                        '&:hover fieldset': {
                            borderColor: '#475569',
                        },
                        '&.Mui-focused': {
                            backgroundColor: 'rgba(255, 255, 255, 0.08)',
                            boxShadow: '0 0 0 4px rgba(59, 130, 246, 0.1)',
                            '& fieldset': {
                                borderColor: '#3b82f6',
                            }
                        }
                    }
                }}
            />

            {error && (
                <Fade in>
                    <Alert severity="error" sx={{ mb: 3, borderRadius: '12px' }}>
                        {error}
                    </Alert>
                </Fade>
            )}

            {summary && (
                <Fade in={!!summary}>
                    <Paper
                        elevation={0}
                        sx={{
                            p: 3,
                            mb: 3,
                            bgcolor: '#0f172a',
                            border: '1px solid #1e293b',
                            borderRadius: '16px',
                            position: 'relative'
                        }}
                    >
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                            <Typography variant="subtitle2" sx={{
                                color: '#3b82f6',
                                fontWeight: 700,
                                textTransform: 'uppercase',
                                letterSpacing: '0.05em',
                                fontSize: '0.75rem'
                            }}>
                                Explanation
                            </Typography>
                            <IconButton
                                size="small"
                                onClick={handleCopy}
                                sx={{
                                    color: copied ? '#10b981' : '#3b82f6',
                                    bgcolor: 'rgba(255,255,255,0.1)',
                                    '&:hover': { bgcolor: 'rgba(255,255,255,0.2)' }
                                }}
                            >
                                {copied ? <CheckCircleIcon fontSize="small" /> : <ContentCopyIcon fontSize="small" />}
                            </IconButton>
                        </Box>
                        <Typography variant="body1" sx={{ color: '#e2e8f0', lineHeight: 1.7 }}>
                            {summary}
                        </Typography>
                    </Paper>
                </Fade>
            )}

            <Box sx={{ mt: 'auto', display: 'flex', justifyContent: 'flex-end' }}>
                <Button
                    variant="contained"
                    size="large"
                    startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <AutoAwesomeIcon />}
                    onClick={handleSummarize}
                    disabled={loading || !text.trim() || (remaining === 0 && !summary)}
                    sx={{
                        borderRadius: '12px',
                        textTransform: 'none',
                        fontSize: '1rem',
                        fontWeight: 600,
                        padding: '10px 28px',
                        background: 'var(--primary-gradient)',
                        boxShadow: '0 4px 12px rgba(79, 70, 229, 0.3)',
                        '&:hover': {
                            background: 'var(--primary-gradient)',
                            boxShadow: '0 6px 16px rgba(79, 70, 229, 0.4)',
                            transform: 'translateY(-1px)'
                        },
                        '&:disabled': {
                            background: '#1e293b',
                            color: '#64748b'
                        }
                    }}
                >
                    {loading ? 'Analyzing...' : 'Explain It'}
                </Button>
            </Box>
        </Paper>
    );
}
