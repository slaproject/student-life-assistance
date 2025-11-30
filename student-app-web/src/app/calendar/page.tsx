"use client";
import React, { useEffect, useMemo, useState, useCallback } from "react";
import {
  Box,
  Button,
  Typography,
  Paper,
  IconButton,
  Dialog,
  TextField,
  MenuItem,
  Stack,
  Alert,
  Chip,
  Tooltip,
  CircularProgress,
} from "@mui/material";
import { ArrowBack, ArrowForward, Delete, Close } from "@mui/icons-material";
import ProtectedRoute from "../dashboard/ProtectedRoute";
import { getApiClient } from "../lib/api";
import { DateTimePicker } from "@mui/x-date-pickers/DateTimePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs, { Dayjs } from "dayjs";

type EventType = "MEETING" | "PERSONAL" | "FINANCIAL" | "APPOINTMENT" | "OTHER";

interface CalendarEvent {
  id?: string;
  eventName: string;
  description?: string;
  startTime: string;
  endTime: string;
  meetingLinks?: string;
  eventType?: EventType;
}

function pad(n: number) { return n.toString().padStart(2, "0"); }
function formatDateKey(d: Date) { return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`; }

export default function CalendarPage() {
  const api = useMemo(() => getApiClient(), []);
  const [events, setEvents] = useState<CalendarEvent[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>("");

  const [currentMonth, setCurrentMonth] = useState(() => {
    const d = new Date();
    return new Date(d.getFullYear(), d.getMonth(), 1);
  });

  const [openDialog, setOpenDialog] = useState(false);
  const [openViewDialog, setOpenViewDialog] = useState(false);
  const [viewEvent, setViewEvent] = useState<CalendarEvent | null>(null);
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [form, setForm] = useState<CalendarEvent>({
    eventName: "",
    description: "",
    startTime: "",
    endTime: "",
    meetingLinks: "",
    eventType: "OTHER",
  });
  const [startValue, setStartValue] = useState<Dayjs | null>(null);
  const [endValue, setEndValue] = useState<Dayjs | null>(null);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editingEvent, setEditingEvent] = useState<CalendarEvent | null>(null);
  const [openMonthSelector, setOpenMonthSelector] = useState(false);
  const [yearView, setYearView] = useState(false);
  const [tempYearMonth, setTempYearMonth] = useState<{ year: number; month: number }>(() => {
    return {
      year: currentMonth.getFullYear(),
      month: currentMonth.getMonth()
    };
  });

  const loadEvents = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get<CalendarEvent[]>("/api/calendar/events");
      setEvents(res.data || []);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error
        ? e.message
        : (e as { response?: { data?: { message?: string } } })?.response?.data?.message || "Failed to load events";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, [api]);

  useEffect(() => { loadEvents(); }, [loadEvents]);

  const monthLabel = useMemo(
    () => currentMonth.toLocaleString(undefined, { month: "long", year: "numeric" }),
    [currentMonth]
  );

  const eventsByDay = useMemo(() => {
    const map = new Map<string, CalendarEvent[]>();
    for (const ev of events) {
      const d = new Date(ev.startTime);
      const key = formatDateKey(d);
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(ev);
    }
    for (const v of map.values()) v.sort((a, b) => +new Date(a.startTime) - +new Date(b.startTime));
    return map;
  }, [events]);

  const calendarCells = useMemo(() => {
    // Get first day of the current month
    const firstDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
    // Get last day of the current month
    const lastDayOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);

    // Calculate how many days we need to show
    const daysInMonth = lastDayOfMonth.getDate();

    // Start from the Sunday of the week containing the first day of the month
    const startDate = new Date(firstDayOfMonth);
    const weekday = startDate.getDay(); // 0 = Sunday
    startDate.setDate(startDate.getDate() - weekday);

    // Calculate how many weeks we need to cover all days of the current month
    const endDate = new Date(lastDayOfMonth);
    const endWeekday = endDate.getDay(); // 0 = Sunday
    endDate.setDate(endDate.getDate() + (6 - endWeekday)); // Move to Saturday of the last week

    // Calculate total days needed (from start Sunday to end Saturday)
    const totalDays = Math.ceil((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;

    // Generate cells - use the calculated total days, not limited to 35
    const cells = Array.from({ length: totalDays }, (_, i) => {
      const d = new Date(startDate);
      d.setDate(startDate.getDate() + i);
      return d;
    });

    console.log('Calendar cells generated:', cells.length);
    console.log('First cell:', cells[0]);
    console.log('Last cell:', cells[cells.length - 1]);
    console.log('Current month:', currentMonth.getMonth() + 1, currentMonth.getFullYear());
    console.log('Days in current month:', daysInMonth);
    console.log('Total cells needed:', totalDays);

    return cells;
  }, [currentMonth]);

  const handlePrev = () => setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
  const handleNext = () => setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));

  const openAddDialog = (date?: Date) => {
    const base = date || new Date();
    const start = dayjs(base).hour(9).minute(0).second(0).millisecond(0);
    const end = dayjs(base).hour(10).minute(0).second(0).millisecond(0);
    setSelectedDate(base);
    setStartValue(start);
    setEndValue(end);
    setForm({
      eventName: "",
      description: "",
      startTime: start.format("YYYY-MM-DDTHH:mm:ss"),
      endTime: end.format("YYYY-MM-DDTHH:mm:ss"),
      meetingLinks: "",
      eventType: "OTHER",
    });
    setOpenDialog(true);
  };
  const closeDialog = () => setOpenDialog(false);

  const saveEvent = async () => {
    if (!form.eventName || !startValue || !endValue) { setError("Please fill event name, start and end time"); return; }
    try {
      setLoading(true); setError("");
      const payload: CalendarEvent = {
        ...form,
        startTime: startValue.format("YYYY-MM-DDTHH:mm:ss"),
        endTime: endValue.format("YYYY-MM-DDTHH:mm:ss"),
      };
      const res = await api.post<CalendarEvent>("/api/calendar/events", payload);
      setEvents((prev) => [...prev, res.data]);
      setOpenDialog(false);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error
        ? e.message
        : (e as { response?: { data?: { message?: string } } })?.response?.data?.message || "Failed to save event";
      setError(errorMessage);
    }
    finally { setLoading(false); }
  };

  const deleteEvent = async (id?: string) => {
    if (!id) return;
    try { setLoading(true); await api.delete(`/api/calendar/events/${id}`); setEvents((p) => p.filter((e) => e.id !== id)); }
    catch (e: unknown) {
      const errorMessage = e instanceof Error
        ? e.message
        : (e as { response?: { data?: { message?: string } } })?.response?.data?.message || "Failed to delete event";
      setError(errorMessage);
    }
    finally { setLoading(false); }
  };

  const openViewEventDialog = (event: CalendarEvent) => {
    console.log("Opening view dialog for event:", event);
    setViewEvent(event);
    setOpenViewDialog(true);
  };

  const closeViewDialog = () => {
    setOpenViewDialog(false);
    setViewEvent(null);
  };

  const openEditEventDialog = (event: CalendarEvent) => {
    setEditingEvent(event);
    setStartValue(dayjs(event.startTime));
    setEndValue(dayjs(event.endTime));
    setForm({
      ...event
    });
    setOpenEditDialog(true);
    setOpenViewDialog(false);
  };

  const closeEditDialog = () => {
    setOpenEditDialog(false);
    setEditingEvent(null);
  };

  const updateEvent = async () => {
    if (!editingEvent?.id || !form.eventName || !startValue || !endValue) {
      setError("Please fill event name, start and end time");
      return;
    }
    try {
      setLoading(true);
      setError("");
      const payload: CalendarEvent = {
        ...form,
        startTime: startValue.format("YYYY-MM-DDTHH:mm:ss"),
        endTime: endValue.format("YYYY-MM-DDTHH:mm:ss"),
      };
      const res = await api.put<CalendarEvent>(`/api/calendar/events/${editingEvent.id}`, payload);
      setEvents((prev) => prev.map(e => e.id === editingEvent.id ? res.data : e));
      setOpenEditDialog(false);
    } catch (e: unknown) {
      const errorMessage = e instanceof Error
        ? e.message
        : (e as { response?: { data?: { message?: string } } })?.response?.data?.message || "Failed to update event";
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const weekdayHeaders = useMemo(
    () => Array.from({ length: 7 }).map((_, i) => new Date(2023, 0, i + 1).toLocaleDateString(undefined, { weekday: "short" })),
    []
  );

  const inCurrentMonth = (d: Date) => d.getMonth() === currentMonth.getMonth();

  return (
    <ProtectedRoute>
      <Box className="calendar-container" sx={{ bgcolor: "#000000", minHeight: "100vh", color: "#ffffff", p: 4 }}>
        <Box className="calendar-header">
          <Box className="calendar-header-left">
            <Typography 
              variant="h5" 
              className="calendar-month-title"
              onClick={() => {
                setTempYearMonth({
                  year: currentMonth.getFullYear(),
                  month: currentMonth.getMonth()
                });
                setYearView(false);
                setOpenMonthSelector(true);
              }}
            >
              {monthLabel}
            </Typography>
            <Box className="calendar-nav-group">
              <IconButton 
                onClick={handlePrev} 
                className="calendar-nav-button"
                size="small"
                aria-label="Previous month"
              >
                <ArrowBack />
              </IconButton>
              <Button
                variant="outlined"
                size="small"
                className="calendar-month-button"
                onClick={() => {
                  setTempYearMonth({
                    year: currentMonth.getFullYear(),
                    month: currentMonth.getMonth()
                  });
                  setYearView(false);
                  setOpenMonthSelector(true);
                }}
                startIcon={
                  <Box 
                    component="span" 
                    className="calendar-icon"
                    sx={{ 
                      fontSize: { xs: '1rem', sm: '1.1rem' },
                      display: 'flex',
                      alignItems: 'center'
                    }}
                  >
                    📅
                  </Box>
                }
              >
                {new Date(currentMonth).toLocaleString(undefined, { month: 'long' })}
              </Button>
              <IconButton 
                onClick={handleNext} 
                className="calendar-nav-button"
                size="small"
                aria-label="Next month"
              >
                <ArrowForward />
              </IconButton>
            </Box>
          </Box>
          <Box className="calendar-header-right">
            <Button
              variant="contained"
              className="calendar-add-button"
              onClick={() => openAddDialog(selectedDate ?? new Date())}
              size="medium"
            >
              Add Event
            </Button>
          </Box>
        </Box>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <Paper variant="outlined" className="calendar-paper" sx={{ bgcolor: "#0a0a0a", borderColor: "#1e293b", color: "#ffffff" }}>
          <Box className="weekday-row">
            {weekdayHeaders.map((w) => (
              <Box key={w} className="weekday-label">{w}</Box>
            ))}
          </Box>

          <Box className="month-grid">
            {calendarCells.map((date, idx) => {
              const key = formatDateKey(date);
              const dayEvents = eventsByDay.get(key) || [];
              const isToday = formatDateKey(date) === formatDateKey(new Date());
              const faded = !inCurrentMonth(date);
              return (
                <Box
                  key={idx}
                  onClick={() => { openAddDialog(date); }}
                  className={`day-cell${faded ? " faded" : ""}${isToday ? " today" : ""}`}
                >
                  <div className="day-cell__header">
                    <Typography variant="body2" className="day-cell__date">{date.getDate()}</Typography>
                    {isToday && <Chip size="small" color="primary" label="Today" />}
                  </div>

                  <div className="event-list">
                    {dayEvents.map((e) => (
                      <Tooltip key={e.id || e.startTime} title={e.description || e.eventName} placement="top-start">
                        <div
                          className="event-chip"
                          data-type={e.eventType || "OTHER"}
                          onClick={(ev) => {
                            ev.stopPropagation();
                            openViewEventDialog(e);
                          }}
                        >
                          <span className="event-chip__time">
                            {new Date(e.startTime).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
                          </span>
                          <span className="event-chip__title">{e.eventName}</span>
                          <IconButton
                            size="small"
                            className="event-chip__delete"
                            onClick={(ev) => {
                              ev.stopPropagation();
                              deleteEvent(e.id);
                            }}
                          >
                            <Delete fontSize="inherit" />
                          </IconButton>
                        </div>
                      </Tooltip>
                    ))}
                  </div>
                </Box>
              );
            })}
          </Box>
        </Paper>

        {/* Add Event Dialog */}
        <Dialog
          open={openDialog}
          onClose={closeDialog}
          fullWidth
          maxWidth="sm"
          PaperProps={{
            sx: {
              borderRadius: 4,
              bgcolor: '#1e293b',
              color: '#ffffff',
              border: '1px solid #334155'
            }
          }}
        >
          <Box sx={{ p: 3, pb: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h5" sx={{ fontWeight: 700, color: '#ffffff' }}>
              {selectedDate ? `New Event on ${selectedDate.toLocaleDateString(undefined, {
                weekday: 'short',
                month: 'short',
                day: 'numeric'
              })}` : 'New Event'}
            </Typography>
            <IconButton onClick={closeDialog} size="small" sx={{ color: '#94a3b8', '&:hover': { color: '#ffffff', bgcolor: 'rgba(255,255,255,0.1)' } }}>
              <Close />
            </IconButton>
          </Box>

          <Box sx={{ px: 3, pb: 3, pt: 1 }}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <Stack spacing={3}>
                <TextField
                  label="Event Name"
                  value={form.eventName}
                  onChange={(e) => setForm((f) => ({ ...f, eventName: e.target.value }))}
                  required
                  fullWidth
                  variant="outlined"
                  placeholder="Enter event title"
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& fieldset': { borderColor: '#334155' },
                      '&:hover fieldset': { borderColor: '#475569' },
                      '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                    },
                    '& .MuiInputLabel-root': { color: '#94a3b8' },
                    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
                  }}
                />

                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' },
                    gap: 2
                  }}
                >
                  <DateTimePicker
                    label="Start time"
                    value={startValue}
                    onChange={(v) => {
                      setStartValue(v);
                      setEndValue(v ? v.add(1, "hour") : null);
                    }}
                    slotProps={{
                      textField: {
                        fullWidth: true,
                        required: true,
                        sx: {
                          '& .MuiOutlinedInput-root': {
                            color: '#ffffff',
                            bgcolor: 'rgba(255,255,255,0.05)',
                            '& fieldset': { borderColor: '#334155' },
                            '&:hover fieldset': { borderColor: '#475569' },
                            '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                          },
                          '& .MuiInputLabel-root': { color: '#94a3b8' },
                          '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' },
                          '& .MuiSvgIcon-root': { color: '#94a3b8' }
                        }
                      },
                      desktopPaper: {
                        sx: {
                          borderRadius: 2,
                          bgcolor: '#1e293b',
                          color: '#ffffff',
                          border: '1px solid #334155',
                          '& .MuiPickersDay-root': {
                            color: '#ffffff',
                            '&:hover': { bgcolor: 'rgba(255,255,255,0.1)' },
                            '&.Mui-selected': { bgcolor: '#3b82f6' }
                          },
                          '& .MuiDayCalendar-weekDayLabel': { color: '#94a3b8' },
                          '& .MuiPickersCalendarHeader-label': { color: '#ffffff' },
                          '& .MuiIconButton-root': { color: '#ffffff' }
                        }
                      }
                    }}
                  />

                  <DateTimePicker
                    label="End time"
                    value={endValue}
                    onChange={(v) => setEndValue(v)}
                    slotProps={{
                      textField: {
                        fullWidth: true,
                        required: true,
                        sx: {
                          '& .MuiOutlinedInput-root': {
                            color: '#ffffff',
                            bgcolor: 'rgba(255,255,255,0.05)',
                            '& fieldset': { borderColor: '#334155' },
                            '&:hover fieldset': { borderColor: '#475569' },
                            '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                          },
                          '& .MuiInputLabel-root': { color: '#94a3b8' },
                          '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' },
                          '& .MuiSvgIcon-root': { color: '#94a3b8' }
                        }
                      },
                      desktopPaper: {
                        sx: {
                          borderRadius: 2,
                          bgcolor: '#1e293b',
                          color: '#ffffff',
                          border: '1px solid #334155',
                          '& .MuiPickersDay-root': {
                            color: '#ffffff',
                            '&:hover': { bgcolor: 'rgba(255,255,255,0.1)' },
                            '&.Mui-selected': { bgcolor: '#3b82f6' }
                          },
                          '& .MuiDayCalendar-weekDayLabel': { color: '#94a3b8' },
                          '& .MuiPickersCalendarHeader-label': { color: '#ffffff' },
                          '& .MuiIconButton-root': { color: '#ffffff' }
                        }
                      }
                    }}
                  />
                </Box>

                <TextField
                  select
                  label="Event Type"
                  value={form.eventType}
                  onChange={(e) => setForm((f) => ({ ...f, eventType: e.target.value as EventType }))}
                  fullWidth
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& fieldset': { borderColor: '#334155' },
                      '&:hover fieldset': { borderColor: '#475569' },
                      '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                    },
                    '& .MuiInputLabel-root': { color: '#94a3b8' },
                    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' },
                    '& .MuiSvgIcon-root': { color: '#94a3b8' }
                  }}
                  SelectProps={{
                    MenuProps: {
                      PaperProps: {
                        sx: {
                          bgcolor: '#1e293b',
                          border: '1px solid #334155',
                          '& .MuiMenuItem-root': {
                            color: '#ffffff',
                            '&:hover': { bgcolor: 'rgba(255,255,255,0.05)' },
                            '&.Mui-selected': { bgcolor: 'rgba(59, 130, 246, 0.2)' }
                          }
                        }
                      }
                    }
                  }}
                >
                  {[
                    { value: "MEETING", label: "Meeting", color: "#3949ab" },
                    { value: "PERSONAL", label: "Personal", color: "#8e24aa" },
                    { value: "FINANCIAL", label: "Financial", color: "#00897b" },
                    { value: "APPOINTMENT", label: "Appointment", color: "#d81b60" },
                    { value: "OTHER", label: "Other", color: "#5c6bc0" }
                  ].map((option) => (
                    <MenuItem
                      key={option.value}
                      value={option.value}
                    >
                      <Box sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 1.5
                      }}>
                        <Box
                          sx={{
                            width: 14,
                            height: 14,
                            borderRadius: '50%',
                            bgcolor: option.color
                          }}
                        />
                        {option.label}
                      </Box>
                    </MenuItem>
                  ))}
                </TextField>

                <TextField
                  label="Description"
                  value={form.description}
                  onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                  multiline
                  minRows={3}
                  placeholder="Add additional details"
                  fullWidth
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& fieldset': { borderColor: '#334155' },
                      '&:hover fieldset': { borderColor: '#475569' },
                      '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                    },
                    '& .MuiInputLabel-root': { color: '#94a3b8' },
                    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
                  }}
                />

                <TextField
                  label="Meeting Link"
                  value={form.meetingLinks}
                  onChange={(e) => setForm((f) => ({ ...f, meetingLinks: e.target.value }))}
                  placeholder="Add optional meeting URL"
                  fullWidth
                  sx={{
                    '& .MuiOutlinedInput-root': {
                      color: '#ffffff',
                      bgcolor: 'rgba(255,255,255,0.05)',
                      '& fieldset': { borderColor: '#334155' },
                      '&:hover fieldset': { borderColor: '#475569' },
                      '&.Mui-focused fieldset': { borderColor: '#3b82f6' }
                    },
                    '& .MuiInputLabel-root': { color: '#94a3b8' },
                    '& .MuiInputLabel-root.Mui-focused': { color: '#3b82f6' }
                  }}
                />
              </Stack>
            </LocalizationProvider>
          </Box>

          <Box sx={{ p: 3, pt: 0, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button
              onClick={closeDialog}
              sx={{ color: '#94a3b8', '&:hover': { color: '#ffffff', bgcolor: 'rgba(255,255,255,0.05)' } }}
            >
              Cancel
            </Button>
            <Button
              onClick={saveEvent}
              variant="contained"
              disabled={loading}
              sx={{
                bgcolor: '#3b82f6',
                '&:hover': { bgcolor: '#2563eb' },
                minWidth: 100
              }}
              startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
            >
              {loading ? "Saving..." : "Save Event"}
            </Button>
          </Box>
        </Dialog>

        {/* View Event Dialog */}
        <Dialog
          open={openViewDialog}
          onClose={closeViewDialog}
          fullWidth
          maxWidth="sm"
          PaperProps={{
            sx: {
              borderRadius: { xs: '12px', sm: '16px' },
              overflow: 'hidden',
              width: { xs: '95vw', sm: '500px' },
              maxWidth: { xs: '95vw', sm: '500px' },
              margin: { xs: '8px', sm: '32px' },
              bgcolor: "#0a0a0a",
              color: "#ffffff",
              border: "1px solid #1e293b"
            }
          }}
        >
          {viewEvent && (
            <>
              <Box
                sx={{
                  p: 0,
                  position: 'relative',
                  overflow: 'hidden'
                }}
              >
                <Box
                  sx={{
                    bgcolor:
                      viewEvent.eventType === 'MEETING' ? '#3949ab' :
                        viewEvent.eventType === 'PERSONAL' ? '#8e24aa' :
                          viewEvent.eventType === 'FINANCIAL' ? '#00897b' :
                            viewEvent.eventType === 'APPOINTMENT' ? '#d81b60' :
                              '#5c6bc0',
                    py: 3,
                    px: 3,
                    color: 'white',
                    position: 'relative'
                  }}
                >
                  <IconButton
                    onClick={closeViewDialog}
                    sx={{
                      position: 'absolute',
                      top: 8,
                      right: 8,
                      color: 'rgba(255,255,255,0.8)'
                    }}
                  >
                    <Box component="span" fontSize="1.5rem">&times;</Box>
                  </IconButton>

                  <Chip
                    label={viewEvent.eventType}
                    size="small"
                    sx={{
                      mb: 1.5,
                      bgcolor: 'rgba(255,255,255,0.2)',
                      color: 'white',
                      fontWeight: 500
                    }}
                  />

                  <Typography variant="h5" fontWeight="bold" mb={1}>
                    {viewEvent.eventName}
                  </Typography>
                </Box>

                <Box sx={{ p: 3, pt: 2 }}>
                  <Box
                    sx={{
                      display: 'flex',
                      flexDirection: 'column',
                      gap: 2,
                      mt: 1
                    }}
                  >
                    <Box>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Date & Time
                      </Typography>
                      <Typography variant="body1">
                        {new Date(viewEvent.startTime).toLocaleDateString(undefined, {
                          weekday: 'long',
                          month: 'long',
                          day: 'numeric',
                          year: 'numeric'
                        })}
                      </Typography>
                      <Typography variant="body1">
                        {new Date(viewEvent.startTime).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit'
                        })} - {new Date(viewEvent.endTime).toLocaleTimeString([], {
                          hour: '2-digit',
                          minute: '2-digit'
                        })}
                      </Typography>
                    </Box>

                    {viewEvent.description && (
                      <Box>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          Description
                        </Typography>
                        <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                          {viewEvent.description}
                        </Typography>
                      </Box>
                    )}

                    {viewEvent.meetingLinks && (
                      <Box>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          Meeting Link
                        </Typography>
                        <Box
                          component="a"
                          href={viewEvent.meetingLinks.startsWith('http') ?
                            viewEvent.meetingLinks : `https://${viewEvent.meetingLinks}`}
                          target="_blank"
                          rel="noopener noreferrer"
                          sx={{
                            display: 'block',
                            color: 'primary.main',
                            textDecoration: 'none',
                            wordBreak: 'break-all',
                            '&:hover': {
                              textDecoration: 'underline'
                            }
                          }}
                        >
                          {viewEvent.meetingLinks}
                        </Box>
                      </Box>
                    )}
                  </Box>
                </Box>

                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    p: 2,
                    pt: 0,
                    gap: 1.5
                  }}
                >
                  <Button
                    onClick={closeViewDialog}
                    color="inherit"
                    variant="outlined"
                    sx={{ borderRadius: 2 }}
                  >
                    Close
                  </Button>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => {
                      if (viewEvent) {
                        openEditEventDialog(viewEvent);
                      }
                    }}
                    sx={{ borderRadius: 2 }}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="outlined"
                    color="error"
                    onClick={() => {
                      if (viewEvent.id) {
                        deleteEvent(viewEvent.id);
                        closeViewDialog();
                      }
                    }}
                    sx={{ borderRadius: 2 }}
                  >
                    Delete
                  </Button>
                </Box>
              </Box>
            </>
          )}
        </Dialog>

        {/* Edit Event Dialog */}
        <Dialog
          open={openEditDialog}
          onClose={closeEditDialog}
          fullWidth
          maxWidth="sm"
          PaperProps={{
            sx: {
              borderRadius: { xs: '12px', sm: '16px' },
              overflow: 'hidden',
              width: { xs: '95vw', sm: '500px' },
              maxWidth: { xs: '95vw', sm: '500px' },
              margin: { xs: '8px', sm: '32px' }
            }
          }}
        >
          {editingEvent && (
            <>
              <Box
                sx={{
                  p: 0,
                  position: 'relative',
                  overflow: 'hidden'
                }}
              >
                <Box
                  sx={{
                    bgcolor:
                      editingEvent.eventType === 'MEETING' ? '#3949ab' :
                        editingEvent.eventType === 'PERSONAL' ? '#8e24aa' :
                          editingEvent.eventType === 'FINANCIAL' ? '#00897b' :
                            editingEvent.eventType === 'APPOINTMENT' ? '#d81b60' :
                              '#5c6bc0',
                    py: 3,
                    px: 3,
                    color: 'white',
                    position: 'relative'
                  }}
                >
                  <IconButton
                    onClick={closeEditDialog}
                    sx={{
                      position: 'absolute',
                      top: 8,
                      right: 8,
                      color: 'rgba(255,255,255,0.8)'
                    }}
                  >
                    <Box component="span" fontSize="1.5rem">&times;</Box>
                  </IconButton>

                  <Chip
                    label={editingEvent.eventType}
                    size="small"
                    sx={{
                      mb: 1.5,
                      bgcolor: 'rgba(255,255,255,0.2)',
                      color: 'white',
                      fontWeight: 500
                    }}
                  />

                  <Typography variant="h5" fontWeight="bold" mb={1}>
                    Edit Event
                  </Typography>
                </Box>

                <Box sx={{ p: 3 }}>
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <Stack spacing={2.5}>
                      <TextField
                        label="Event Name"
                        value={form.eventName}
                        onChange={(e) => setForm((f) => ({ ...f, eventName: e.target.value }))}
                        required
                        fullWidth
                        variant="outlined"
                        placeholder="Enter event title"
                        InputProps={{
                          sx: { borderRadius: 1.5 }
                        }}
                      />

                      <Box
                        sx={{
                          display: 'grid',
                          gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr' },
                          gap: 2
                        }}
                      >
                        <DateTimePicker
                          label="Start time"
                          value={startValue}
                          onChange={(v) => {
                            setStartValue(v);
                            setEndValue(v ? v.add(1, "hour") : null);
                          }}
                          slotProps={{
                            textField: {
                              fullWidth: true,
                              required: true,
                              InputProps: {
                                sx: {
                                  borderRadius: 1.5,
                                  '& .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'rgba(0, 0, 0, 0.12)',
                                  },
                                  '&:hover .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'rgba(0, 0, 0, 0.38)',
                                  },
                                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'primary.main',
                                    borderWidth: 2
                                  }
                                }
                              }
                            },
                            desktopPaper: {
                              sx: {
                                borderRadius: 2,
                                boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
                                '& .MuiPickersDay-root': {
                                  borderRadius: '50%',
                                  '&.Mui-selected': {
                                    backgroundColor: 'primary.main',
                                    color: 'white',
                                    fontWeight: 'bold'
                                  }
                                }
                              }
                            }
                          }}
                          sx={{
                            '& .MuiInputLabel-root.Mui-focused': {
                              color: 'primary.main',
                            }
                          }}
                        />

                        <DateTimePicker
                          label="End time"
                          value={endValue}
                          onChange={(v) => setEndValue(v)}
                          slotProps={{
                            textField: {
                              fullWidth: true,
                              required: true,
                              InputProps: {
                                sx: {
                                  borderRadius: 1.5,
                                  '& .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'rgba(0, 0, 0, 0.12)',
                                  },
                                  '&:hover .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'rgba(0, 0, 0, 0.38)',
                                  },
                                  '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                                    borderColor: 'primary.main',
                                    borderWidth: 2
                                  }
                                }
                              }
                            },
                            desktopPaper: {
                              sx: {
                                borderRadius: 2,
                                boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
                                '& .MuiPickersDay-root': {
                                  borderRadius: '50%',
                                  '&.Mui-selected': {
                                    backgroundColor: 'primary.main',
                                    color: 'white',
                                    fontWeight: 'bold'
                                  }
                                }
                              }
                            }
                          }}
                          sx={{
                            '& .MuiInputLabel-root.Mui-focused': {
                              color: 'primary.main',
                            }
                          }}
                        />
                      </Box>

                      <TextField
                        select
                        label="Event Type"
                        value={form.eventType}
                        onChange={(e) => setForm((f) => ({ ...f, eventType: e.target.value as EventType }))}
                        fullWidth
                        InputProps={{
                          sx: { borderRadius: 1.5 }
                        }}
                      >
                        {[
                          { value: "MEETING", label: "Meeting", color: "#3949ab" },
                          { value: "PERSONAL", label: "Personal", color: "#8e24aa" },
                          { value: "FINANCIAL", label: "Financial", color: "#00897b" },
                          { value: "APPOINTMENT", label: "Appointment", color: "#d81b60" },
                          { value: "OTHER", label: "Other", color: "#5c6bc0" }
                        ].map((option) => (
                          <MenuItem
                            key={option.value}
                            value={option.value}
                            sx={{
                              "&:hover": { bgcolor: `${option.color}20` },
                              ...(form.eventType === option.value && {
                                bgcolor: `${option.color}20`,
                                fontWeight: 'bold'
                              })
                            }}
                          >
                            <Box sx={{
                              display: 'flex',
                              alignItems: 'center',
                              gap: 1.5
                            }}>
                              <Box
                                sx={{
                                  width: 14,
                                  height: 14,
                                  borderRadius: '50%',
                                  bgcolor: option.color
                                }}
                              />
                              {option.label}
                            </Box>
                          </MenuItem>
                        ))}
                      </TextField>

                      <TextField
                        label="Description"
                        value={form.description}
                        onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                        multiline
                        minRows={3}
                        placeholder="Add additional details"
                        fullWidth
                        InputProps={{
                          sx: { borderRadius: 1.5 }
                        }}
                      />

                      <TextField
                        label="Meeting Link"
                        value={form.meetingLinks}
                        onChange={(e) => setForm((f) => ({ ...f, meetingLinks: e.target.value }))}
                        placeholder="Add optional meeting URL"
                        fullWidth
                        InputProps={{
                          sx: { borderRadius: 1.5 }
                        }}
                      />
                    </Stack>
                  </LocalizationProvider>
                </Box>

                <Box
                  sx={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    p: 2,
                    bgcolor: 'background.default',
                    gap: 1.5
                  }}
                >
                  <Button
                    onClick={closeEditDialog}
                    color="inherit"
                    variant="outlined"
                    sx={{ borderRadius: 2 }}
                  >
                    Cancel
                  </Button>
                  <Button
                    onClick={updateEvent}
                    variant="contained"
                    disabled={loading}
                    sx={{
                      borderRadius: 2,
                      px: 3
                    }}
                    startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
                  >
                    {loading ? "Updating..." : "Update Event"}
                  </Button>
                </Box>
              </Box>
            </>
          )}
        </Dialog>

        {/* Month/Year Selector Dialog */}
        <Dialog
          open={openMonthSelector}
          onClose={() => setOpenMonthSelector(false)}
          maxWidth="xs"
          fullWidth
          PaperProps={{
            sx: {
              borderRadius: { xs: '12px', sm: '16px' },
              overflow: 'hidden',
              width: { xs: '95vw', sm: '400px' },
              maxWidth: { xs: '95vw', sm: '400px' },
              margin: { xs: '8px', sm: '32px' }
            }
          }}
        >
          <Box sx={{ p: 0 }}>
            <Box
              sx={{
                bgcolor: 'primary.main',
                py: { xs: 1.5, sm: 2 },
                px: { xs: 2, sm: 3 },
                color: 'white',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}
            >
              <Typography
                variant="h6"
                fontWeight="bold"
                sx={{ fontSize: { xs: '1rem', sm: '1.25rem' } }}
              >
                {yearView ? 'Select Year' : 'Select Month & Year'}
              </Typography>
              <IconButton
                onClick={() => setOpenMonthSelector(false)}
                sx={{
                  color: 'rgba(255,255,255,0.8)',
                  p: { xs: 0.5, sm: 1 }
                }}
              >
                <Box component="span" fontSize={{ xs: '1.25rem', sm: '1.5rem' }}>&times;</Box>
              </IconButton>
            </Box>

            {!yearView ? (
              <>
                <Box
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    px: 2,
                    py: 1.5,
                    borderBottom: '1px solid rgba(0,0,0,0.06)'
                  }}
                >
                  <IconButton onClick={() => {
                    setTempYearMonth(prev => ({
                      ...prev,
                      year: prev.year - 1
                    }));
                  }}>
                    <ArrowBack />
                  </IconButton>
                  <Button
                    onClick={() => setYearView(true)}
                    variant="text"
                    sx={{ fontWeight: 'bold', fontSize: '1rem' }}
                  >
                    {tempYearMonth.year}
                  </Button>
                  <IconButton onClick={() => {
                    setTempYearMonth(prev => ({
                      ...prev,
                      year: prev.year + 1
                    }));
                  }}>
                    <ArrowForward />
                  </IconButton>
                </Box>

                <Box sx={{ p: { xs: 1.5, sm: 2 } }}>
                  <Box
                    sx={{
                      display: 'grid',
                      gridTemplateColumns: { xs: 'repeat(2, 1fr)', sm: 'repeat(3, 1fr)' },
                      gap: { xs: 1, sm: 1.5 }
                    }}
                  >
                    {Array.from({ length: 12 }).map((_, month) => {
                      const isSelected = month === tempYearMonth.month;
                      const isCurrentMonth =
                        month === new Date().getMonth() &&
                        tempYearMonth.year === new Date().getFullYear();

                      return (
                        <Button
                          key={month}
                          variant={isSelected ? "contained" : "outlined"}
                          onClick={() => {
                            setTempYearMonth(prev => ({ ...prev, month }));
                          }}
                          sx={{
                            py: 1,
                            borderRadius: 2,
                            fontWeight: 'medium',
                            position: 'relative',
                            bgcolor: isSelected ? 'primary.main' : 'transparent',
                            borderColor: isCurrentMonth && !isSelected ? 'primary.main' : undefined,
                            color: isSelected ? 'white' : isCurrentMonth ? 'primary.main' : 'text.primary',
                            '&:hover': {
                              bgcolor: isSelected ? 'primary.dark' : 'rgba(0,0,0,0.04)'
                            }
                          }}
                        >
                          {new Date(2023, month, 1).toLocaleString(undefined, { month: 'short' })}
                          {isCurrentMonth && !isSelected && (
                            <Box
                              sx={{
                                width: 6,
                                height: 6,
                                bgcolor: 'primary.main',
                                borderRadius: '50%',
                                position: 'absolute',
                                bottom: 3,
                                left: '50%',
                                transform: 'translateX(-50%)'
                              }}
                            />
                          )}
                        </Button>
                      );
                    })}
                  </Box>
                </Box>
              </>
            ) : (
              <Box sx={{ p: { xs: 1.5, sm: 2 } }}>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: { xs: 'repeat(2, 1fr)', sm: 'repeat(3, 1fr)' },
                    gap: { xs: 1, sm: 1.5 }
                  }}
                >
                  {Array.from({ length: 12 }).map((_, i) => {
                    const year = tempYearMonth.year - 5 + i;
                    const isSelected = year === tempYearMonth.year;
                    const isCurrentYear = year === new Date().getFullYear();

                    return (
                      <Button
                        key={year}
                        variant={isSelected ? "contained" : "outlined"}
                        onClick={() => {
                          setTempYearMonth(prev => ({ ...prev, year }));
                          setYearView(false);
                        }}
                        sx={{
                          py: 1,
                          borderRadius: 2,
                          fontWeight: 'medium',
                          position: 'relative',
                          bgcolor: isSelected ? 'primary.main' : 'transparent',
                          borderColor: isCurrentYear && !isSelected ? 'primary.main' : undefined,
                          color: isSelected ? 'white' : isCurrentYear ? 'primary.main' : 'text.primary',
                          '&:hover': {
                            bgcolor: isSelected ? 'primary.dark' : 'rgba(0,0,0,0.04)'
                          }
                        }}
                      >
                        {year}
                        {isCurrentYear && !isSelected && (
                          <Box
                            sx={{
                              width: 6,
                              height: 6,
                              bgcolor: 'primary.main',
                              borderRadius: '50%',
                              position: 'absolute',
                              bottom: 3,
                              left: '50%',
                              transform: 'translateX(-50%)'
                            }}
                          />
                        )}
                      </Button>
                    );
                  })}
                </Box>
              </Box>
            )}

            <Box
              sx={{
                display: 'flex',
                justifyContent: 'flex-end',
                p: { xs: 1.5, sm: 2 },
                pt: 0,
                gap: { xs: 1, sm: 1.5 },
                flexDirection: { xs: 'column', sm: 'row' }
              }}
            >
              <Button
                color="inherit"
                variant="outlined"
                onClick={() => setOpenMonthSelector(false)}
                sx={{
                  borderRadius: 2,
                  width: { xs: '100%', sm: 'auto' },
                  order: { xs: 2, sm: 1 }
                }}
              >
                Cancel
              </Button>
              <Button
                variant="contained"
                onClick={() => {
                  setCurrentMonth(new Date(tempYearMonth.year, tempYearMonth.month, 1));
                  setOpenMonthSelector(false);
                }}
                sx={{
                  borderRadius: 2,
                  width: { xs: '100%', sm: 'auto' },
                  order: { xs: 1, sm: 2 }
                }}
              >
                Apply
              </Button>
            </Box>
          </Box>
        </Dialog>
      </Box>
    </ProtectedRoute>
  );
}
