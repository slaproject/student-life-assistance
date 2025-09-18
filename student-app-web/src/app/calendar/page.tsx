"use client";
import React, { useEffect, useMemo, useState } from "react";
import {
  Box,
  Button,
  Typography,
  Paper,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Stack,
  Alert,
  Chip,
  Tooltip,
  CircularProgress,
} from "@mui/material";
import { ArrowBack, ArrowForward, Delete } from "@mui/icons-material";
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
function formatMonthInput(d: Date) { return `${d.getFullYear()}-${pad(d.getMonth() + 1)}`; }

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
  const [tempYearMonth, setTempYearMonth] = useState<{year: number; month: number}>(() => {
    return {
      year: currentMonth.getFullYear(),
      month: currentMonth.getMonth()
    };
  });

  const loadEvents = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await api.get<CalendarEvent[]>("/api/calendar/events");
      setEvents(res.data || []);
    } catch (e: any) {
      setError(e.response?.data?.message || e.message || "Failed to load events");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadEvents(); }, []);

  const monthLabel = useMemo(
    () => currentMonth.toLocaleString(undefined, { month: "long", year: "numeric" }),
    [currentMonth]
  );

  const eventsByDay = useMemo(() => {
    const map = new Map<string, CalendarEvent>();
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
    // Get first day of the month
    const first = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
    const start = new Date(first);
    // Get the day of week for first day (0 = Sunday)
    const weekday = start.getDay();
    // Move to the start of the first week
    start.setDate(start.getDate() - weekday);

    // Get last day of month
    const lastDay = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);
    // Calculate if we need 5 or 6 rows
    // First, count days from start of first week to end of month
    const totalDays = weekday + lastDay.getDate();
    // Decide if we need 5 or 6 rows (weeks) - we need 6 rows if there are more than 35 days
    const rowCount = Math.ceil(totalDays / 7);
    // Generate calendar cells for either 5 or 6 rows
    return Array.from({ length: rowCount * 7 }, (_, i) => {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      return d;
    });
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
    } catch (e: any) { setError(e.response?.data?.message || e.message || "Failed to save event"); }
    finally { setLoading(false); }
  };

  const deleteEvent = async (id?: string) => {
    if (!id) return;
    try { setLoading(true); await api.delete(`/api/calendar/events/${id}`); setEvents((p) => p.filter((e) => e.id !== id)); }
    catch (e: any) { setError(e.response?.data?.message || e.message || "Failed to delete event"); }
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
    } catch (e: any) {
      setError(e.response?.data?.message || e.message || "Failed to update event");
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
      <Box className="calendar-container">
        <Stack direction="row" alignItems="center" spacing={2} className="calendar-header">
          <Typography variant="h5" className="calendar-month-title"
            onClick={() => {
              setTempYearMonth({
                year: currentMonth.getFullYear(),
                month: currentMonth.getMonth()
              });
              setYearView(false);
              setOpenMonthSelector(true);
            }}
            sx={{ cursor: 'pointer', '&:hover': { textDecoration: 'underline' } }}
          >
            {monthLabel}
          </Typography>
          <IconButton onClick={handlePrev}><ArrowBack /></IconButton>
          <Button
            variant="outlined"
            size="small"
            onClick={() => {
              setTempYearMonth({
                year: currentMonth.getFullYear(),
                month: currentMonth.getMonth()
              });
              setYearView(false);
              setOpenMonthSelector(true);
            }}
            sx={{
              borderRadius: 2,
              minWidth: '120px',
              textTransform: 'none',
              fontWeight: 'medium',
              '&:hover': {
                background: 'rgba(63, 81, 181, 0.04)'
              }
            }}
            startIcon={<Box component="span" sx={{ fontSize: '1.25rem' }}>📅</Box>}
          >
            {new Date(currentMonth).toLocaleString(undefined, { month: 'long' })}
          </Button>
          <IconButton onClick={handleNext}><ArrowForward /></IconButton>
          <Button variant="contained" onClick={() => openAddDialog(selectedDate ?? new Date())}>Add Event</Button>
        </Stack>

        {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

        <Paper variant="outlined" className="calendar-paper">
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
                  className={`day-cell${faded ? " faded" : ""}`}
                >
                  <div className="day-cell__header">
                    <Typography variant="body2" className="day-cell__date">{date.getDate()}</Typography>
                    {isToday && <Chip size="small" color="primary" label="Today" />}
                  </div>

                  <div className="event-list">
                    {dayEvents.slice(0, 3).map((e) => (
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
                    {dayEvents.length > 3 && (
                      <Typography variant="caption" color="text.secondary">+{dayEvents.length - 3} more</Typography>
                    )}
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
            sx: { borderRadius: '16px', overflow: 'hidden' }
          }}
        >
          <Box sx={{ p: 0, position: 'relative', overflow: 'hidden' }}>
            <Box
              sx={{
                bgcolor: 'primary.main',
                py: 3,
                px: 3,
                color: 'white',
                position: 'relative'
              }}
            >
              <IconButton
                onClick={closeDialog}
                sx={{
                  position: 'absolute',
                  top: 8,
                  right: 8,
                  color: 'rgba(255,255,255,0.8)'
                }}
              >
                <Box component="span" fontSize="1.5rem">&times;</Box>
              </IconButton>

              <Typography variant="h5" fontWeight="bold" mb={1}>
                {selectedDate ? `New Event on ${selectedDate.toLocaleDateString(undefined, { 
                  weekday: 'long', 
                  month: 'long', 
                  day: 'numeric' 
                })}` : 'New Event'}
              </Typography>

              <Typography variant="subtitle1" sx={{ opacity: 0.9 }}>
                Add details for your new calendar event
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
                onClick={closeDialog}
                color="inherit"
                variant="outlined"
                sx={{ borderRadius: 2 }}
              >
                Cancel
              </Button>
              <Button
                onClick={saveEvent}
                variant="contained"
                disabled={loading}
                sx={{
                  borderRadius: 2,
                  px: 3
                }}
                startIcon={loading ? <CircularProgress size={20} color="inherit" /> : null}
              >
                {loading ? "Saving..." : "Save Event"}
              </Button>
            </Box>
          </Box>
        </Dialog>

        {/* View Event Dialog */}
        <Dialog
          open={openViewDialog}
          onClose={closeViewDialog}
          fullWidth
          maxWidth="sm"
          PaperProps={{
            sx: { borderRadius: '16px', overflow: 'hidden' }
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
                    bgcolor: (theme) =>
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
            sx: { borderRadius: '16px', overflow: 'hidden' }
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
                    bgcolor: (theme) =>
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
            sx: { borderRadius: '16px', overflow: 'hidden' }
          }}
        >
          <Box sx={{ p: 0 }}>
            <Box
              sx={{
                bgcolor: 'primary.main',
                py: 2,
                px: 3,
                color: 'white',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}
            >
              <Typography variant="h6" fontWeight="bold">
                {yearView ? 'Select Year' : 'Select Month & Year'}
              </Typography>
              <IconButton
                onClick={() => setOpenMonthSelector(false)}
                sx={{ color: 'rgba(255,255,255,0.8)' }}
              >
                <Box component="span" fontSize="1.5rem">&times;</Box>
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

                <Box sx={{ p: 2 }}>
                  <Box
                    sx={{
                      display: 'grid',
                      gridTemplateColumns: 'repeat(3, 1fr)',
                      gap: 1.5
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
              <Box sx={{ p: 2 }}>
                <Box
                  sx={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(3, 1fr)',
                    gap: 1.5
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
                p: 2,
                pt: 0,
                gap: 1.5
              }}
            >
              <Button
                color="inherit"
                variant="outlined"
                onClick={() => setOpenMonthSelector(false)}
                sx={{ borderRadius: 2 }}
              >
                Cancel
              </Button>
              <Button
                variant="contained"
                onClick={() => {
                  setCurrentMonth(new Date(tempYearMonth.year, tempYearMonth.month, 1));
                  setOpenMonthSelector(false);
                }}
                sx={{ borderRadius: 2 }}
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
