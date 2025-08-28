IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.doe@example.com')
INSERT INTO users (username, email, password) VALUES ('john_doe', 'john.doe@example.com', 'hashed_password_1');

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane.smith@example.com')
INSERT INTO users (username, email, password) VALUES ('jane_smith', 'jane.smith@example.com', 'hashed_password_2');

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice.wonder@example.com')
INSERT INTO users (username, email, password) VALUES ('alice_wonder', 'alice.wonder@example.com', 'hashed_password_3');


-- Remove or update the below after replacing with real UUIDs:
-- INSERT INTO calendar_events (user_id, title, description, start_time, end_time) VALUES
-- (1, 'Meeting with team', 'Discuss project updates', '2025-07-03 10:00:00', '2025-07-03 11:00:00'),
-- (2, 'Doctor Appointment', 'Routine check-up', '2025-07-04 15:00:00', '2025-07-04 16:00:00'),
-- (3, 'Lunch with friend', 'Catch up with an old friend', '2025-07-05 12:30:00', '2025-07-05 13:30:00');


-- Insert mock data for finance functionality
-- Note: These inserts assume you have existing users in the users table

-- Insert default expense categories for testing
IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Food & Dining')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Food & Dining', 'Restaurants, groceries, takeout', '#FF6B6B', 'restaurant');

IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Transportation')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Transportation', 'Gas, public transport, parking', '#4ECDC4', 'car');

IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Entertainment')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Entertainment', 'Movies, games, subscriptions', '#45B7D1', 'movie');

IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Education')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Education', 'Books, courses, supplies', '#96CEB4', 'book');

IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Health & Fitness')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Health & Fitness', 'Gym, medical, pharmacy', '#FFEAA7', 'health');

IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Shopping')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Shopping', 'Clothes, electronics, misc', '#DDA0DD', 'shopping');

IF NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Utilities')
INSERT INTO expense_categories (id, user_id, name, description, color, icon) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Utilities', 'Phone, internet, electricity', '#FFB347', 'utility');

-- Insert some sample expenses for the current month
IF NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Lunch at Campus Cafeteria')
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Food & Dining'), 'Lunch at Campus Cafeteria', 'Daily lunch', 12.50, GETDATE(), 'card');

IF NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Grocery Shopping')
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Food & Dining'), 'Grocery Shopping', 'Weekly groceries', 45.30, DATEADD(day, -2, GETDATE()), 'card');

IF NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Bus Pass')
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Transportation'), 'Bus Pass', 'Monthly bus pass', 85.00, DATEADD(day, -5, GETDATE()), 'cash');

IF NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Netflix Subscription')
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Entertainment'), 'Netflix Subscription', 'Monthly streaming', 15.99, DATEADD(day, -10, GETDATE()), 'card');

IF NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Coffee Shop')
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Food & Dining'), 'Coffee Shop', 'Study session coffee', 4.75, DATEADD(day, -1, GETDATE()), 'digital_wallet');

-- Insert budget limits for current month
IF NOT EXISTS (SELECT 1 FROM budget_limits WHERE user_id = (SELECT TOP 1 id FROM users) AND category_id = (SELECT TOP 1 id FROM expense_categories WHERE name = 'Food & Dining') AND budget_month = MONTH(GETDATE()) AND budget_year = YEAR(GETDATE()))
INSERT INTO budget_limits (id, user_id, category_id, budget_month, budget_year, limit_amount, alert_threshold) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Food & Dining'), MONTH(GETDATE()), YEAR(GETDATE()), 200.00, 75.0);

IF NOT EXISTS (SELECT 1 FROM budget_limits WHERE user_id = (SELECT TOP 1 id FROM users) AND category_id = (SELECT TOP 1 id FROM expense_categories WHERE name = 'Transportation') AND budget_month = MONTH(GETDATE()) AND budget_year = YEAR(GETDATE()))
INSERT INTO budget_limits (id, user_id, category_id, budget_month, budget_year, limit_amount, alert_threshold) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Transportation'), MONTH(GETDATE()), YEAR(GETDATE()), 100.00, 80.0);

IF NOT EXISTS (SELECT 1 FROM budget_limits WHERE user_id = (SELECT TOP 1 id FROM users) AND category_id = (SELECT TOP 1 id FROM expense_categories WHERE name = 'Entertainment') AND budget_month = MONTH(GETDATE()) AND budget_year = YEAR(GETDATE()))
INSERT INTO budget_limits (id, user_id, category_id, budget_month, budget_year, limit_amount, alert_threshold) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Entertainment'), MONTH(GETDATE()), YEAR(GETDATE()), 50.00, 85.0);

-- Insert a financial goal
IF NOT EXISTS (SELECT 1 FROM financial_goals WHERE goal_name = 'Emergency Fund')
INSERT INTO financial_goals (id, user_id, goal_name, description, target_amount, current_amount, target_date, goal_type) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Emergency Fund', 'Save for unexpected expenses', 1000.00, 250.00, DATEADD(month, 6, GETDATE()), 'savings');

IF NOT EXISTS (SELECT 1 FROM financial_goals WHERE goal_name = 'New Laptop')
INSERT INTO financial_goals (id, user_id, goal_name, description, target_amount, current_amount, target_date, goal_type) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'New Laptop', 'Save for a new laptop for studies', 800.00, 150.00, DATEADD(month, 4, GETDATE()), 'purchase');
