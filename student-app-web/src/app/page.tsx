"use client";
import React from "react";
import { useRouter } from "next/navigation";
import {
  Box,
  Button,
  Container,
  Paper,
  Stack,
  Typography
} from "@mui/material";
import Grid from "@mui/material/Grid";
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
  Notifications as MessageCircleIcon
} from "@mui/icons-material";

export default function Home() {
  const router = useRouter();

  const scrollToSection = (sectionId: string) => {
    const element = document.getElementById(sectionId);
    if (element) element.scrollIntoView({ behavior: "smooth" });
  };

  const coreFeatures = [
    {
      icon: <CalendarIcon fontSize="large" />,
      title: "Smart Calendar",
      description: "AI-powered scheduling that adapts to your academic and personal life.",
      href: "/calendar",
      gradient: "linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)"
    },
    {
      icon: <CheckSquareIcon fontSize="large" />,
      title: "To-Do Management",
      description: "Intelligent task prioritization with deadline tracking and reminders.",
      href: "/tasks",
      gradient: "linear-gradient(135deg, #22c55e 0%, #16a34a 100%)"
    },
    {
      icon: <ClockIcon fontSize="large" />,
      title: "Pomodoro Timer",
      description: "Boost productivity with customizable focus sessions and breaks.",
      href: "/pomodoro",
      gradient: "linear-gradient(135deg, #a855f7 0%, #7c3aed 100%)"
    },
    {
      icon: <DollarSignIcon fontSize="large" />,
      title: "Finance Tracker",
      description: "Manage your budget with expense tracking and financial insights.",
      href: "/finance",
      gradient: "linear-gradient(135deg, #14b8a6 0%, #0d9488 100%)"
    }
  ];

  const extraFeatures = [
    {
      icon: <TrendingUpIcon />,
      title: "Progress Analytics",
      description:
        "Detailed insights into your productivity patterns and performance trends."
    },
    {
      icon: <UsersIcon />,
      title: "Study Groups",
      description:
        "Connect with classmates, share resources, and collaborate on projects."
    },
    {
      icon: <ShieldIcon />,
      title: "Data Security",
      description:
        "Your personal and academic data is protected with enterprise-grade security."
    },
    {
      icon: <SmartphoneIcon />,
      title: "Mobile Optimized",
      description:
        "Access everything on any device with our responsive, mobile-first design."
    },
    {
      icon: <MessageCircleIcon />,
      title: "Smart Notifications",
      description:
        "Intelligent reminders that adapt to your schedule and preferences."
    },
    {
      icon: <TargetIcon />,
      title: "Goal Setting",
      description:
        "Set and track goals with milestones and motivating progress markers."
    }
  ];

  const stats = [
    { number: "50K+", label: "Active Students" },
    { number: "98%", label: "Satisfaction Rate" },
    { number: "2M+", label: "Tasks Completed" },
    { number: "23%", label: "Avg. GPA Improvement" }
  ];

  return (
    <Box sx={{ bgcolor: "#fff", minHeight: "100vh" }}>
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
          background:
            "linear-gradient(135deg, rgba(37,99,235,1) 0%, rgba(147,51,234,1) 50%, rgba(20,184,166,1) 100%)"
        }}
      >
        <Box
          sx={{
            position: "absolute",
            inset: 0,
            opacity: 0.18,
            backgroundImage:
              "url('https://images.pexels.com/photos/1438081/pexels-photo-1438081.jpeg?auto=compress&cs=tinysrgb&w=1920&h=1280&fit=crop')",
            backgroundSize: "cover",
            backgroundPosition: "center"
          }}
        />
        <Container sx={{ position: "relative", zIndex: 1, textAlign: "center" }}>
          <Typography
            variant="h2"
            sx={{
              fontWeight: 800,
              color: "#fff",
              mb: 2,
              lineHeight: 1.2,
              fontSize: { xs: "2.25rem", md: "3.5rem", lg: "4rem" }
            }}
          >
            Your Ultimate{" "}
            <Box
              component="span"
              sx={{
                display: "block",
                background:
                  "linear-gradient(90deg, #f59e0b 0%, #f97316 100%)",
                WebkitBackgroundClip: "text",
                backgroundClip: "text",
                color: "transparent"
              }}
            >
              Student Life Assistant
            </Box>
          </Typography>
          <Typography
            variant="h6"
            sx={{
              color: "rgba(255,255,255,0.9)",
              maxWidth: 900,
              mx: "auto",
              mb: 4
            }}
          >
            S.L.A.P combines intelligent scheduling, task management, productivity
            tracking, and financial planning into one powerful platform designed
            for students.
          </Typography>
          <Stack
            direction={{ xs: "column", sm: "row" }}
            spacing={2}
            justifyContent="center"
          >
            <Button
              onClick={() => scrollToSection("features")}
              variant="contained"
              endIcon={<ArrowRightIcon />}
              sx={{
                px: 4,
                py: 1.5,
                fontWeight: 700,
                borderRadius: 999,
                bgcolor: "#fff",
                color: "#2563eb",
                "&:hover": { bgcolor: "#eff6ff" }
              }}
            >
              Explore Features
            </Button>
            <Button
              onClick={() => router.push("/signup")}
              variant="outlined"
              sx={{
                px: 4,
                py: 1.5,
                fontWeight: 700,
                borderRadius: 999,
                borderColor: "#fff",
                color: "#fff",
                "&:hover": { bgcolor: "rgba(255,255,255,0.1)", borderColor: "#fff" }
              }}
            >
              Get Started
            </Button>
          </Stack>
        </Container>

        {/* Floating elements */}
        <Box
          sx={{
            position: "absolute",
            top: "25%",
            left: "25%",
            width: 64,
            height: 64,
            bgcolor: "#f59e0b",
            borderRadius: "50%",
            opacity: 0.6,
            filter: "blur(0.3px)"
          }}
        />
        <Box
          sx={{
            position: "absolute",
            top: "75%",
            right: "25%",
            width: 48,
            height: 48,
            bgcolor: "#14b8a6",
            borderRadius: "50%",
            opacity: 0.6
          }}
        />
        <Box
          sx={{
            position: "absolute",
            bottom: "25%",
            left: "33%",
            width: 32,
            height: 32,
            bgcolor: "#a855f7",
            borderRadius: "50%",
            opacity: 0.6
          }}
        />
      </Box>

      {/* Core Features */}
      <Box id="features" sx={{ py: { xs: 8, md: 12 }, bgcolor: "#f9fafb" }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 6 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, color: "#111827", mb: 1 }}>
              Everything You Need in One Place
            </Typography>
            <Typography variant="h6" sx={{ color: "#4b5563", maxWidth: 900, mx: "auto" }}>
              S.L.A.P integrates essential tools into a seamless experience to keep you organized,
              productive, and financially responsible.
            </Typography>
          </Box>

          <Grid container spacing={3}>
            {coreFeatures.map((f, i) => (
              <Grid key={i} size={{ xs: 12, md: 6, lg: 3 }}>
                <Paper
                  onClick={() => router.push(f.href)}
                  sx={{
                    p: 3,
                    borderRadius: 3,
                    cursor: "pointer",
                    transition: "transform .2s, box-shadow .2s",
                    "&:hover": { transform: "scale(1.02)", boxShadow: 6 }
                  }}
                  elevation={3}
                >
                  <Box
                    sx={{
                      width: 64,
                      height: 64,
                      borderRadius: 3,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#fff",
                      mb: 2,
                      background: f.gradient
                    }}
                  >
                    {f.icon}
                  </Box>
                  <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
                    {f.title}
                  </Typography>
                  <Typography sx={{ color: "#6b7280", mb: 1.5 }}>
                    {f.description}
                  </Typography>
                  <Stack direction="row" alignItems="center" sx={{ color: "#2563eb", fontWeight: 700 }}>
                    Try it now <ChevronRightIcon fontSize="small" sx={{ ml: 0.5 }} />
                  </Stack>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* Additional Features */}
      <Box sx={{ py: { xs: 8, md: 12 }, bgcolor: "#fff" }}>
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 6 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, color: "#111827", mb: 1 }}>
              Built for Student Success
            </Typography>
            <Typography variant="h6" sx={{ color: "#4b5563", maxWidth: 900, mx: "auto" }}>
              Beyond the core features, S.L.A.P offers advanced tools and insights to help you excel.
            </Typography>
          </Box>

          <Grid container spacing={3}>
            {extraFeatures.map((f, i) => (
              <Grid key={i} size={{ xs: 12, md: 6, lg: 4 }}>
                <Paper
                  elevation={0}
                  sx={{
                    p: 3,
                    textAlign: "center",
                    borderRadius: 3,
                    transition: "background-color .2s",
                    "&:hover": { bgcolor: "#f9fafb" }
                  }}
                >
                  <Box
                    sx={{
                      width: 64,
                      height: 64,
                      borderRadius: "50%",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#fff",
                      mx: "auto",
                      mb: 2,
                      background: "linear-gradient(135deg, #3b82f6 0%, #7c3aed 100%)"
                    }}
                  >
                    {f.icon}
                  </Box>
                  <Typography variant="h6" sx={{ fontWeight: 800, mb: 1 }}>
                    {f.title}
                  </Typography>
                  <Typography sx={{ color: "#6b7280" }}>{f.description}</Typography>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* Statistics */}
      <Box
        sx={{
          py: { xs: 8, md: 12 },
          background:
            "linear-gradient(90deg, #2563eb 0%, #7c3aed 50%, #0d9488 100%)"
        }}
      >
        <Container maxWidth="lg">
          <Box sx={{ textAlign: "center", mb: 4 }}>
            <Typography variant="h3" sx={{ fontWeight: 800, color: "#fff" }}>
              Trusted by Students Worldwide
            </Typography>
          </Box>
          <Grid container spacing={3}>
            {stats.map((s, i) => (
              <Grid key={i} size={{ xs: 12, md: 3 }}>
                <Box sx={{ textAlign: "center" }}>
                  <Typography variant="h4" sx={{ fontWeight: 800, color: "#fff", mb: 0.5 }}>
                    {s.number}
                  </Typography>
                  <Typography sx={{ color: "rgba(255,255,255,0.85)", fontSize: 18 }}>
                    {s.label}
                  </Typography>
                </Box>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Box>

      {/* CTA */}
      <Box sx={{ py: { xs: 8, md: 12 }, bgcolor: "#111827" }}>
        <Container maxWidth="md" sx={{ textAlign: "center" }}>
          <Typography variant="h3" sx={{ fontWeight: 800, color: "#fff", mb: 1 }}>
            Ready to Transform Your Student Life?
          </Typography>
          <Typography variant="h6" sx={{ color: "#d1d5db", mb: 3 }}>
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
              endIcon={<ChevronRightIcon />}
              sx={{
                px: 4,
                py: 1.5,
                fontWeight: 700,
                borderRadius: 999,
                bgcolor: "#2563eb",
                "&:hover": { bgcolor: "#1d4ed8" }
              }}
            >
              Get Started Now
            </Button>
            <Button
              onClick={() => router.push("/signup")}
              variant="outlined"
              sx={{
                px: 4,
                py: 1.5,
                fontWeight: 700,
                borderRadius: 999,
                borderColor: "#d1d5db",
                color: "#d1d5db",
                "&:hover": { bgcolor: "#d1d5db", color: "#111827", borderColor: "#d1d5db" }
              }}
            >
              Create Account
            </Button>
          </Stack>
        </Container>
      </Box>

      {/* Footer */}
      <Box sx={{ bgcolor: "#000", py: 4 }}>
        <Container
          sx={{
            display: "flex",
            flexDirection: { xs: "column", md: "row" },
            alignItems: { xs: "flex-start", md: "center" },
            justifyContent: "space-between",
            gap: 2
          }}
        >
          <Stack direction="row" alignItems="center" spacing={1}>
            <SchoolIcon sx={{ color: "#3b82f6" }} />
            <Typography sx={{ color: "#fff", fontWeight: 800, fontSize: 20 }}>
              S.L.A.P
            </Typography>
          </Stack>
          <Box sx={{ color: "#9ca3af", textAlign: { xs: "left", md: "right" } }}>
            <Typography>© 2025 Student Life Assistant Project. All rights reserved.</Typography>
            <Typography sx={{ mt: 0.5 }}>
              Empowering students, one feature at a time.
            </Typography>
          </Box>
        </Container>
      </Box>
    </Box>
  );
}