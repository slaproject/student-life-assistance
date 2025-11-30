"use client";
import React from "react";
import { useRouter } from "next/navigation";
import {
  Box,
  Button,
  Container,
  Paper,
  Stack,
  Typography,
  useTheme,
  useMediaQuery
} from "@mui/material";
import {
  ChevronRight as ChevronRightIcon,
  School as SchoolIcon,
  CalendarToday as CalendarIcon,
  CheckBox as CheckSquareIcon,
  Timer as ClockIcon,
  AttachMoney as DollarSignIcon,
  TrendingUp as TrendingUpIcon,
  Group as UsersIcon,
  Security as ShieldIcon,
  Smartphone as SmartphoneIcon,
  ArrowForward as ArrowRightIcon,
  Flag as TargetIcon,
  Notifications as MessageCircleIcon,
  Psychology as PsychologyIcon,
  AccountBalance as AccountBalanceIcon
} from "@mui/icons-material";
import { useAuth } from "./AuthContext";

export default function Home() {
  const router = useRouter();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  const { isAuthenticated } = useAuth();

  const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    if (element) element.scrollIntoView({ behavior: "smooth" });
  };

  const coreFeatures = [
    {
      icon: <PsychologyIcon fontSize="large" />,
      title: "AI Tutor",
      description: "Get instant explanations, summaries, and study help powered by advanced AI technology.",
      href: "/dashboard",
    },
    {
      icon: <CalendarIcon fontSize="large" />,
      title: "Smart Calendar",
      description: "AI-powered scheduling that adapts to your academic schedule and personal commitments.",
      href: "/calendar",
    },
    {
      icon: <CheckSquareIcon fontSize="large" />,
      title: "To-Do Management",
      description: "Intelligent task prioritization with deadline tracking, reminders, and custom workflows.",
      href: "/tasks",
    },
    {
      icon: <ClockIcon fontSize="large" />,
      title: "Pomodoro Timer",
      description: "Boost productivity with customizable focus sessions, breaks, and progress tracking.",
      href: "/pomodoro",
    },
    {
      icon: <DollarSignIcon fontSize="large" />,
      title: "Finance Tracker",
      description: "AI-powered expense tracking with receipt upload and automatic transaction categorization.",
      href: "/finance",
    },
    {
      icon: <AccountBalanceIcon fontSize="large" />,
      title: "AI Tax Agent",
      description: "Get personalized US tax guidance for students with step-by-step instructions and resources.",
      href: "/finance",
    }
  ];

  const extraFeatures = [
    {
      icon: <TrendingUpIcon />,
      title: "Spending Analytics",
      description: "Track expenses by category, view spending trends, and get budget analysis with visual charts."
    },
    {
      icon: <TargetIcon />,
      title: "Financial Goals",
      description: "Set savings targets, track progress, and manage debt payoff plans with real-time updates."
    },
    {
      icon: <MessageCircleIcon />,
      title: "Budget Alerts",
      description: "Receive intelligent alerts when approaching or exceeding budget limits to stay on track."
    },
    {
      icon: <PsychologyIcon />,
      title: "AI Receipt Scanning",
      description: "Upload receipt images and let AI automatically extract transaction details and categories."
    },
    {
      icon: <CheckSquareIcon />,
      title: "Task Prioritization",
      description: "Organize tasks with custom columns, set priorities, and track deadlines efficiently."
    },
    {
      icon: <CalendarIcon />,
      title: "Calendar Integration",
      description: "Sync your academic schedule, personal events, and deadlines in one unified view."
    }
  ];

  const stats = [
    { number: "50", label: "Beta Testers" },
    { number: "95%", label: "Satisfaction Rate" },
    { number: "5K+", label: "Tasks Completed" },
    { number: "15%", label: "Productivity Boost" }
  ];

  return (
    <Box sx={{ bgcolor: "#000000", minHeight: "100vh", color: "#ffffff" }}>
      {/* Hero */}
      <Box
        id="home"
        sx={{
          position: "relative",
          minHeight: { xs: "90vh", md: "100vh" },
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          overflow: "hidden",
          background: "radial-gradient(circle at 50% 50%, #0a1929 0%, #000000 100%)"
        }}
      >
        {/* Abstract Background Elements */}
        <Box
          sx={{
            position: "absolute",
            top: "20%",
            left: "10%",
            width: "300px",
            height: "300px",
            background: "linear-gradient(135deg, #1e3a8a 0%, #000000 100%)",
            filter: "blur(100px)",
            opacity: 0.4,
            borderRadius: "50%"
          }}
        />
        <Box
          sx={{
            position: "absolute",
            bottom: "10%",
            right: "10%",
            width: "400px",
            height: "400px",
            background: "linear-gradient(135deg, #2563eb 0%, #000000 100%)",
            filter: "blur(120px)",
            opacity: 0.3,
            borderRadius: "50%"
          }}
        />

        <Container sx={{ position: "relative", zIndex: 1, textAlign: "center" }}>
          <Typography
            variant="h1"
            sx={{
              fontWeight: 800,
              color: "#fff",
              mb: 3,
              lineHeight: 1.1,
              fontSize: { xs: "2.5rem", md: "4.5rem", lg: "5.5rem" },
              letterSpacing: "-0.02em"
            }}
          >
            Your Ultimate{" "}
            <Box
              component="span"
              sx={{
                display: "block",
                background: "linear-gradient(90deg, #3b82f6 0%, #60a5fa 100%)",
                WebkitBackgroundClip: "text",
                backgroundClip: "text",
                color: "transparent",
                mt: 1
              }}
            >
              Student Life Assistant
            </Box>
          </Typography>
          <Typography
            variant="h6"
            sx={{
              color: "#94a3b8",
              maxWidth: 800,
              mx: "auto",
              mb: 6,
              lineHeight: 1.6,
              fontSize: { xs: "1rem", md: "1.25rem" }
            }}
          >
            S.L.A.P combines AI-powered tutoring, intelligent scheduling, task management, 
            productivity tracking, financial planning, and tax guidance into one powerful, professional platform.
          </Typography>
          <Stack
            direction={{ xs: "column", sm: "row" }}
            spacing={3}
            justifyContent="center"
          >
            <Button
              onClick={() => scrollToSection("features")}
              variant="contained"
              endIcon={<ArrowRightIcon />}
              sx={{
                px: 5,
                py: 2,
                fontSize: "1.1rem",
                fontWeight: 700,
                borderRadius: "50px",
                bgcolor: "#2563eb",
                boxShadow: "0 0 20px rgba(37, 99, 235, 0.5)",
                "&:hover": {
                  bgcolor: "#1d4ed8",
                  boxShadow: "0 0 30px rgba(37, 99, 235, 0.7)"
                }
              }}
            >
              Explore Features
            </Button>
            {!isAuthenticated && (
              <Button
                onClick={() => router.push("/signup")}
                variant="outlined"
                sx={{
                  px: 5,
                  py: 2,
                  fontSize: "1.1rem",
                  fontWeight: 700,
                  borderRadius: "50px",
                  borderColor: "rgba(255,255,255,0.2)",
                  color: "#fff",
                  backdropFilter: "blur(10px)",
                  "&:hover": {
                    borderColor: "#fff",
                    bgcolor: "rgba(255,255,255,0.05)"
                  }
                }}
              >
                Get Started
              </Button>
            )}
            {isAuthenticated && (
              <Button
                onClick={() => router.push("/dashboard")}
                variant="outlined"
                sx={{
                  px: 5,
                  py: 2,
                  fontSize: "1.1rem",
                  fontWeight: 700,
                  borderRadius: "50px",
                  borderColor: "rgba(255,255,255,0.2)",
                  color: "#fff",
                  backdropFilter: "blur(10px)",
                  "&:hover": {
                    borderColor: "#fff",
                    bgcolor: "rgba(255,255,255,0.05)"
                  }
                }}
              >
                Go to Dashboard
              </Button>
            )}
          </Stack>
        </Container>
      </Box>

      {/* Core Features */}
      <Box id="features" sx={{ py: { xs: 10, md: 16 }, bgcolor: "#000000" }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 10 }}>
            <Typography variant="overline" sx={{ color: "#3b82f6", fontWeight: 700, letterSpacing: 2 }}>
              CORE CAPABILITIES
            </Typography>
            <Typography variant="h3" sx={{ fontWeight: 800, color: "#fff", mt: 2, mb: 3 }}>
              Everything You Need in One Place
            </Typography>
            <Typography variant="h6" sx={{ color: "#64748b", maxWidth: 700, mx: "auto" }}>
              Seamlessly integrated tools to keep you organized, productive, and ahead of the curve.
            </Typography>
          </Box>

          <Box sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', md: '1fr 1fr', lg: 'repeat(3, 1fr)' },
            gap: 4
          }}>
            {coreFeatures.map((f, i) => (
              <Box key={i}>
                <Paper
                  onClick={() => router.push(f.href)}
                  sx={{
                    p: 4,
                    height: '100%',
                    bgcolor: "#0a0a0a",
                    border: "1px solid #1e293b",
                    borderRadius: 4,
                    cursor: "pointer",
                    transition: "all 0.3s ease",
                    "&:hover": {
                      transform: "translateY(-8px)",
                      borderColor: "#3b82f6",
                      boxShadow: "0 10px 40px -10px rgba(59, 130, 246, 0.2)"
                    }
                  }}
                  elevation={0}
                >
                  <Box
                    sx={{
                      width: 60,
                      height: 60,
                      borderRadius: 3,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#60a5fa",
                      mb: 3,
                      bgcolor: "rgba(59, 130, 246, 0.1)"
                    }}
                  >
                    {f.icon}
                  </Box>
                  <Typography variant="h6" sx={{ fontWeight: 700, mb: 2, color: "#fff" }}>
                    {f.title}
                  </Typography>
                  <Typography sx={{ color: "#94a3b8", mb: 3, lineHeight: 1.6 }}>
                    {f.description}
                  </Typography>
                  <Stack direction="row" alignItems="center" sx={{ color: "#3b82f6", fontWeight: 600 }}>
                    Try it now <ChevronRightIcon fontSize="small" sx={{ ml: 0.5 }} />
                  </Stack>
                </Paper>
              </Box>
            ))}
          </Box>
        </Container>
      </Box>

      {/* Additional Features */}
      <Box sx={{ py: { xs: 10, md: 16 }, bgcolor: "#050505", borderTop: "1px solid #111" }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 10 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, color: "#fff", mb: 3 }}>
              Built for Student Success
            </Typography>
            <Typography variant="h6" sx={{ color: "#64748b", maxWidth: 700, mx: "auto" }}>
              Advanced tools and insights designed to help you excel in your academic journey.
            </Typography>
          </Box>

          <Box sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', md: '1fr 1fr', lg: 'repeat(3, 1fr)' },
            gap: 4,
            alignItems: 'stretch'
          }}>
            {extraFeatures.map((f, i) => (
              <Box key={i} sx={{ display: 'flex', height: '100%' }}>
                <Paper
                  elevation={0}
                  sx={{
                    p: 4,
                    bgcolor: "transparent",
                    border: "1px solid transparent",
                    borderRadius: 4,
                    transition: "all 0.3s",
                    width: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    height: '100%',
                    "&:hover": {
                      bgcolor: "#0a0a0a",
                      border: "1px solid #1e293b"
                    }
                  }}
                >
                  <Box
                    sx={{
                      width: 50,
                      height: 50,
                      borderRadius: "12px",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#3b82f6",
                      mb: 2,
                      bgcolor: "rgba(59, 130, 246, 0.1)",
                      flexShrink: 0
                    }}
                  >
                    {f.icon}
                  </Box>
                  <Typography variant="h6" sx={{ fontWeight: 700, mb: 1, color: "#fff", flexShrink: 0 }}>
                    {f.title}
                  </Typography>
                  <Typography sx={{ color: "#64748b", lineHeight: 1.6, flex: 1 }}>{f.description}</Typography>
                </Paper>
              </Box>
            ))}
          </Box>
        </Container>
      </Box>

      {/* Statistics */}
      <Box
        sx={{
          py: { xs: 10, md: 16 },
          bgcolor: "#000",
          position: "relative",
          overflow: "hidden"
        }}
      >
        <Box
          sx={{
            position: "absolute",
            inset: 0,
            background: "linear-gradient(180deg, rgba(37, 99, 235, 0.05) 0%, rgba(0,0,0,0) 100%)"
          }}
        />
        <Container maxWidth="lg" sx={{ position: "relative", zIndex: 1 }}>
          <Box sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr 1fr', md: 'repeat(4, 1fr)' },
            gap: 4,
            textAlign: "center"
          }}>
            {stats.map((s, i) => (
              <Box key={i} sx={{ p: 2 }}>
                <Typography
                  variant="h3"
                  sx={{
                    fontWeight: 800,
                    color: "#3b82f6",
                    mb: 1,
                    textShadow: "0 0 20px rgba(59, 130, 246, 0.3)"
                  }}
                >
                  {s.number}
                </Typography>
                <Typography sx={{ color: "#94a3b8", fontSize: "1.1rem", fontWeight: 500 }}>
                  {s.label}
                </Typography>
              </Box>
            ))}
          </Box>
        </Container>
      </Box>

      {/* CTA */}
      <Box sx={{ py: { xs: 10, md: 16 }, bgcolor: "#0a0a0a", borderTop: "1px solid #111" }}>
        <Container maxWidth="md" sx={{ textAlign: "center" }}>
          <Typography variant="h3" sx={{ fontWeight: 800, color: "#fff", mb: 2 }}>
            Ready to Transform Your Student Life?
          </Typography>
          <Typography variant="h6" sx={{ color: "#64748b", mb: 5 }}>
            Join thousands of successful students using S.L.A.P to achieve their goals.
          </Typography>
          <Stack
            direction={{ xs: "column", sm: "row" }}
            spacing={2}
            justifyContent="center"
          >
            <Button
              onClick={() => router.push("/calendar")}
              variant="contained"
              size="large"
              endIcon={<ChevronRightIcon />}
              sx={{
                px: 5,
                py: 1.5,
                fontWeight: 700,
                borderRadius: "50px",
                bgcolor: "#2563eb",
                "&:hover": { bgcolor: "#1d4ed8" }
              }}
            >
              Get Started Now
            </Button>
            {!isAuthenticated && (
              <Button
                onClick={() => router.push("/signup")}
                variant="outlined"
                size="large"
                sx={{
                  px: 5,
                  py: 1.5,
                  fontWeight: 700,
                  borderRadius: "50px",
                  borderColor: "#334155",
                  color: "#fff",
                  "&:hover": { borderColor: "#fff", bgcolor: "rgba(255,255,255,0.05)" }
                }}
              >
                Create Account
              </Button>
            )}
            {isAuthenticated && (
              <Button
                onClick={() => router.push("/dashboard")}
                variant="outlined"
                size="large"
                sx={{
                  px: 5,
                  py: 1.5,
                  fontWeight: 700,
                  borderRadius: "50px",
                  borderColor: "#334155",
                  color: "#fff",
                  "&:hover": { borderColor: "#fff", bgcolor: "rgba(255,255,255,0.05)" }
                }}
              >
                Go to Dashboard
              </Button>
            )}
          </Stack>
        </Container>
      </Box>

      {/* Footer */}
      <Box sx={{ bgcolor: "#000", py: 6, borderTop: "1px solid #111" }}>
        <Container
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            alignItems: { xs: "center", md: "center" },
            justifyContent: "space-between",
            gap: 3
          }}
        >
          <Stack direction="row" alignItems="center" spacing={1}>
            <SchoolIcon sx={{ color: "#3b82f6", fontSize: 32 }} />
            <Typography sx={{ color: "#fff", fontWeight: 800, fontSize: 24, letterSpacing: -0.5 }}>
              S.L.A.P
            </Typography>
          </Stack>
          <Box sx={{ color: "#475569", textAlign: { xs: "center", md: "right" } }}>
            <Typography variant="body2">© 2025 Student Life Assistant Project. All rights reserved.</Typography>
            <Typography variant="body2" sx={{ mt: 0.5 }}>
              Empowering students with professional tools.
            </Typography>
          </Box>
        </Container>
      </Box>
    </Box>
  );
}