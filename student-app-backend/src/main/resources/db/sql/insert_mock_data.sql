INSERT INTO users (username, email, password) VALUES
('john_doe', 'john.doe@example.com', 'hashed_password_1'),
('jane_smith', 'jane.smith@example.com', 'hashed_password_2'),
('alice_wonder', 'alice.wonder@example.com', 'hashed_password_3');

INSERT INTO calendar_events (user_id, title, description, start_time, end_time) VALUES
(1, 'Meeting with team', 'Discuss project updates', '2025-07-03 10:00:00', '2025-07-03 11:00:00'),
(2, 'Doctor Appointment', 'Routine check-up', '2025-07-04 15:00:00', '2025-07-04 16:00:00'),
(3, 'Lunch with friend', 'Catch up with an old friend', '2025-07-05 12:30:00', '2025-07-05 13:30:00');
