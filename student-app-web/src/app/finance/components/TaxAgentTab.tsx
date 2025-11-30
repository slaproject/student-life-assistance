"use client";
import React, { useState } from "react";
import {
  Box,
  Typography,
  Paper,
  Card,
  CardContent,
  Button,
  Checkbox,
  FormControlLabel,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Alert,
  List,
  ListItem,
  ListItemText,
  Link,
  Divider,
  useTheme,
  useMediaQuery
} from "@mui/material";
import {
  AccountBalance as AccountBalanceIcon,
  CheckCircle as CheckCircleIcon,
  OpenInNew as OpenInNewIcon
} from "@mui/icons-material";
import { taxAgentService } from "../../lib/api";

const US_STATES = [
  "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
  "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
  "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
  "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
  "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
  "New Hampshire", "New Jersey", "New Mexico", "New York",
  "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
  "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
  "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
  "West Virginia", "Wisconsin", "Wyoming"
];

interface TaxSubsection {
  title: string;
  content: string;
}

interface TaxSection {
  title: string;
  subsections: TaxSubsection[];
  items: string[];
}

interface TaxAgentResponse {
  sections: TaxSection[];
  links: string[];
  summary?: string;
}

export default function TaxAgentTab() {
  const [hasOnCampusJob, setHasOnCampusJob] = useState(false);
  const [hasWorkStudy, setHasWorkStudy] = useState(false);
  const [residentState, setResidentState] = useState("");
  const [workState, setWorkState] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [response, setResponse] = useState<TaxAgentResponse | null>(null);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setResponse(null);

    if (!residentState) {
      setError("Please select your state of residence.");
      return;
    }

    setLoading(true);

    try {
      const result = await taxAgentService.generateTaxGuidance({
        hasOnCampusJob,
        hasWorkStudy,
        residentState,
        workState: workState || "Same as residence"
      });
      setResponse(result);
    } catch (err: any) {
      setError(
        err.response?.data?.message ||
        err.message ||
        "Failed to generate tax guidance. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: { xs: 2, sm: 3 } }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <AccountBalanceIcon
          sx={{
            mr: 2,
            fontSize: { xs: 28, sm: 32 },
            color: '#3b82f6'
          }}
        />
        <Typography
          variant={isMobile ? "h5" : "h4"}
          sx={{
            color: '#ffffff',
            fontWeight: 600
          }}
        >
          AI Tax Agent
        </Typography>
      </Box>

      <Paper
        elevation={3}
        sx={{
          p: { xs: 2, sm: 3 },
          borderRadius: 3,
          bgcolor: '#0a0a0a',
          border: '1px solid #1e293b',
          boxShadow: '0 4px 20px rgba(0,0,0,0.2)'
        }}
      >
        <Typography
          variant="body1"
          sx={{ color: '#94a3b8', mb: 3 }}
        >
          Get personalized tax guidance based on your employment status and location.
          Fill out the form below to receive step-by-step instructions and helpful resources.
        </Typography>

        <form onSubmit={handleSubmit}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={hasOnCampusJob}
                  onChange={(e) => setHasOnCampusJob(e.target.checked)}
                  sx={{
                    color: '#3b82f6',
                    '&.Mui-checked': {
                      color: '#3b82f6'
                    }
                  }}
                />
              }
              label="I have an on-campus job"
              sx={{ color: '#ffffff' }}
            />

            <FormControlLabel
              control={
                <Checkbox
                  checked={hasWorkStudy}
                  onChange={(e) => setHasWorkStudy(e.target.checked)}
                  sx={{
                    color: '#3b82f6',
                    '&.Mui-checked': {
                      color: '#3b82f6'
                    }
                  }}
                />
              }
              label="I participate in work-study"
              sx={{ color: '#ffffff' }}
            />

            <FormControl fullWidth>
              <InputLabel
                id="resident-state-label"
                sx={{ color: '#94a3b8' }}
              >
                State of Residence *
              </InputLabel>
              <Select
                labelId="resident-state-label"
                value={residentState}
                onChange={(e) => setResidentState(e.target.value)}
                label="State of Residence *"
                required
                sx={{
                  color: '#ffffff',
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#1e293b'
                  },
                  '&:hover .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#3b82f6'
                  },
                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#3b82f6'
                  },
                  '& .MuiSvgIcon-root': {
                    color: '#94a3b8'
                  }
                }}
                MenuProps={{
                  PaperProps: {
                    sx: {
                      bgcolor: '#1e293b',
                      color: '#ffffff',
                      maxHeight: 300
                    }
                  }
                }}
              >
                {US_STATES.map((state) => (
                  <MenuItem key={state} value={state} sx={{ color: '#ffffff' }}>
                    {state}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel
                id="work-state-label"
                sx={{ color: '#94a3b8' }}
              >
                State Where I Work
              </InputLabel>
              <Select
                labelId="work-state-label"
                value={workState}
                onChange={(e) => setWorkState(e.target.value)}
                label="State Where I Work"
                sx={{
                  color: '#ffffff',
                  '& .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#1e293b'
                  },
                  '&:hover .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#3b82f6'
                  },
                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                    borderColor: '#3b82f6'
                  },
                  '& .MuiSvgIcon-root': {
                    color: '#94a3b8'
                  }
                }}
                MenuProps={{
                  PaperProps: {
                    sx: {
                      bgcolor: '#1e293b',
                      color: '#ffffff',
                      maxHeight: 300
                    }
                  }
                }}
              >
                <MenuItem value="Same as residence" sx={{ color: '#ffffff' }}>
                  Same as residence
                </MenuItem>
                {US_STATES.map((state) => (
                  <MenuItem key={state} value={state} sx={{ color: '#ffffff' }}>
                    {state}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            {error && (
              <Alert severity="error" sx={{ bgcolor: '#1e293b', color: '#ef4444' }}>
                {error}
              </Alert>
            )}

            <Button
              type="submit"
              variant="contained"
              disabled={loading || !residentState}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : <CheckCircleIcon />}
              sx={{
                bgcolor: '#3b82f6',
                color: '#ffffff',
                py: 1.5,
                '&:hover': {
                  bgcolor: '#2563eb'
                },
                '&:disabled': {
                  bgcolor: '#1e293b',
                  color: '#64748b'
                }
              }}
            >
              {loading ? "Generating Guidance..." : "Get Tax Guidance"}
            </Button>
          </Box>
        </form>

        {response && (
          <Box sx={{ mt: 4 }}>
            <Divider sx={{ my: 4, borderColor: '#1e293b' }} />
            
            {/* Header Section */}
            <Box sx={{ mb: 4 }}>
              <Typography
                variant="h4"
                sx={{
                  color: '#ffffff',
                  fontWeight: 700,
                  mb: 0,
                  fontSize: { xs: '1.5rem', sm: '1.75rem' },
                  letterSpacing: '-0.02em'
                }}
              >
                Tax Guidance
              </Typography>
            </Box>

            {/* Sections */}
            {response.sections.map((section, sectionIndex) => {
              // Check if this is a "STEPS" or main heading section
              const isStepsSection = section.title.toUpperCase().includes("STEP") || 
                                     section.title.toUpperCase().includes("STEPS") ||
                                     section.title.toUpperCase().startsWith("STEP");
              
              // Clean the title - remove "STEPS:" or "STEPS" prefix
              let cleanTitle = section.title;
              if (cleanTitle.toUpperCase().startsWith("STEPS:")) {
                cleanTitle = cleanTitle.substring(6).trim();
              } else if (cleanTitle.toUpperCase().startsWith("STEPS")) {
                cleanTitle = cleanTitle.substring(5).trim();
              }
              
              return (
                <Card
                  key={sectionIndex}
                  elevation={0}
                  sx={{
                    bgcolor: isStepsSection ? '#0f172a' : '#1e293b',
                    border: isStepsSection ? '1px solid rgba(59, 130, 246, 0.3)' : '1px solid rgba(51, 65, 85, 0.5)',
                    mb: 4,
                    borderRadius: 3,
                    overflow: 'hidden',
                    transition: 'all 0.3s ease',
                    '&:hover': {
                      borderColor: isStepsSection ? 'rgba(59, 130, 246, 0.5)' : 'rgba(51, 65, 85, 0.8)',
                      boxShadow: '0 8px 24px rgba(0, 0, 0, 0.3)'
                    }
                  }}
                >
                  <CardContent sx={{ p: { xs: 2.5, sm: 4 } }}>
                    {/* Section Title */}
                    <Box sx={{ mb: 3 }}>
                      <Typography
                        variant="h5"
                        sx={{
                          color: isStepsSection ? '#60a5fa' : '#3b82f6',
                          fontWeight: 700,
                          fontSize: { xs: '1.1rem', sm: '1.25rem' },
                          letterSpacing: '-0.01em',
                          display: 'flex',
                          alignItems: 'center',
                          gap: 1.5,
                          mb: 1,
                          textDecoration: 'underline',
                          textUnderlineOffset: '8px',
                          textDecorationThickness: '2px'
                        }}
                      >
                        {isStepsSection && (
                          <Box
                            sx={{
                              width: 4,
                              height: 24,
                              bgcolor: '#3b82f6',
                              borderRadius: 2,
                              flexShrink: 0
                            }}
                          />
                        )}
                        {cleanTitle}
                      </Typography>
                      {isStepsSection && (
                        <Box
                          sx={{
                            height: 1,
                            bgcolor: 'rgba(59, 130, 246, 0.2)',
                            mt: 2,
                            borderRadius: 1
                          }}
                        />
                      )}
                    </Box>

                    {/* Subsections */}
                    {section.subsections.length > 0 && (
                      <Box sx={{ mb: section.items.length > 0 ? 3.5 : 0 }}>
                        {section.subsections.map((subsection, subIndex) => (
                          <Box
                            key={subIndex}
                            sx={{
                              mb: 3,
                              pl: { xs: 2, sm: 3 },
                              borderLeft: '3px solid #60a5fa',
                              bgcolor: 'rgba(15, 23, 42, 0.5)',
                              borderRadius: '0 12px 12px 0',
                              py: 2.5,
                              pr: 2,
                              transition: 'all 0.2s ease',
                              '&:hover': {
                                bgcolor: 'rgba(15, 23, 42, 0.7)',
                                borderLeftColor: '#3b82f6'
                              }
                            }}
                          >
                            <Typography
                              variant="subtitle1"
                              sx={{
                                color: '#60a5fa',
                                fontWeight: 600,
                                mb: 1.5,
                                fontSize: { xs: '0.95rem', sm: '1.05rem' },
                                display: 'flex',
                                alignItems: 'center',
                                gap: 1.5
                              }}
                            >
                              <Box
                                component="span"
                                sx={{
                                  width: 8,
                                  height: 8,
                                  borderRadius: '50%',
                                  bgcolor: '#60a5fa',
                                  display: 'inline-block',
                                  flexShrink: 0,
                                  boxShadow: '0 0 8px rgba(96, 165, 250, 0.5)'
                                }}
                              />
                              {subsection.title}
                            </Typography>
                            {subsection.content && (
                              <Typography
                                variant="body2"
                                sx={{
                                  color: '#e2e8f0',
                                  lineHeight: 1.75,
                                  fontSize: { xs: '0.9rem', sm: '0.95rem' },
                                  pl: 2.5,
                                  fontWeight: 400
                                }}
                              >
                                {subsection.content}
                              </Typography>
                            )}
                          </Box>
                        ))}
                      </Box>
                    )}

                    {/* Regular Items/Bullet Points */}
                    {section.items.length > 0 && (
                      <Box sx={{ mt: section.subsections.length > 0 ? 2.5 : 0 }}>
                        {section.items.map((item, itemIndex) => (
                          <Box
                            key={itemIndex}
                            sx={{
                              display: 'flex',
                              alignItems: 'flex-start',
                              mb: 2.5,
                              pl: { xs: 1.5, sm: 2 },
                              transition: 'all 0.2s ease',
                              '&:hover': {
                                pl: { xs: 2, sm: 2.5 },
                                '& .bullet-point': {
                                  transform: 'scale(1.2)',
                                  boxShadow: '0 0 12px rgba(59, 130, 246, 0.6)'
                                }
                              }
                            }}
                          >
                            <Box
                              className="bullet-point"
                              sx={{
                                minWidth: 10,
                                width: 10,
                                height: 10,
                                borderRadius: '50%',
                                bgcolor: '#3b82f6',
                                mt: 1.25,
                                mr: 2.5,
                                flexShrink: 0,
                                transition: 'all 0.2s ease',
                                boxShadow: '0 0 8px rgba(59, 130, 246, 0.4)'
                              }}
                            />
                            <Typography
                              variant="body2"
                              sx={{
                                color: '#cbd5e1',
                                lineHeight: 1.75,
                                fontSize: { xs: '0.9rem', sm: '0.95rem' },
                                flex: 1,
                                fontWeight: 400
                              }}
                            >
                              {item}
                            </Typography>
                          </Box>
                        ))}
                      </Box>
                    )}
                  </CardContent>
                </Card>
              );
            })}

            {response.links && response.links.length > 0 && (
              <Card
                elevation={0}
                sx={{
                  bgcolor: '#1e293b',
                  border: '1px solid rgba(51, 65, 85, 0.5)',
                  borderRadius: 3,
                  overflow: 'hidden',
                  transition: 'all 0.3s ease',
                  '&:hover': {
                    borderColor: 'rgba(51, 65, 85, 0.8)',
                    boxShadow: '0 8px 24px rgba(0, 0, 0, 0.3)'
                  }
                }}
              >
                <CardContent sx={{ p: { xs: 2.5, sm: 4 } }}>
                  <Typography
                    variant="h6"
                    sx={{
                      color: '#3b82f6',
                      fontWeight: 700,
                      mb: 3,
                      fontSize: { xs: '1.1rem', sm: '1.2rem' },
                      letterSpacing: '-0.01em',
                      display: 'flex',
                      alignItems: 'center',
                      gap: 1.5
                    }}
                  >
                    <Box
                      sx={{
                        width: 4,
                        height: 20,
                        bgcolor: '#3b82f6',
                        borderRadius: 2,
                        flexShrink: 0
                      }}
                    />
                    Helpful Resources
                  </Typography>
                  <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                    {response.links.map((link, index) => (
                      <Box
                        key={index}
                        sx={{
                          display: 'flex',
                          alignItems: 'center',
                          p: 2,
                          bgcolor: 'rgba(15, 23, 42, 0.5)',
                          borderRadius: 2,
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            bgcolor: 'rgba(15, 23, 42, 0.7)',
                            transform: 'translateX(4px)'
                          }
                        }}
                      >
                        <Link
                          href={link}
                          target="_blank"
                          rel="noopener noreferrer"
                          sx={{
                            color: '#60a5fa',
                            textDecoration: 'none',
                            display: 'flex',
                            alignItems: 'center',
                            gap: 1.5,
                            flex: 1,
                            fontSize: { xs: '0.875rem', sm: '0.9rem' },
                            fontWeight: 400,
                            transition: 'all 0.2s ease',
                            '&:hover': {
                              color: '#3b82f6',
                              gap: 2
                            }
                          }}
                        >
                          <Box
                            sx={{
                              width: 6,
                              height: 6,
                              borderRadius: '50%',
                              bgcolor: '#60a5fa',
                              flexShrink: 0,
                              transition: 'all 0.2s ease'
                            }}
                          />
                          <Box
                            component="span"
                            sx={{
                              flex: 1,
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              whiteSpace: 'nowrap'
                            }}
                          >
                            {link}
                          </Box>
                          <OpenInNewIcon 
                            sx={{ 
                              fontSize: 16,
                              flexShrink: 0,
                              opacity: 0.7,
                              transition: 'all 0.2s ease'
                            }} 
                          />
                        </Link>
                      </Box>
                    ))}
                  </Box>
                </CardContent>
              </Card>
            )}
          </Box>
        )}
      </Paper>
    </Box>
  );
}

