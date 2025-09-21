-- Mock data for student-life-assistance application

-- Insert mock users
IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.doe@example.com')
INSERT INTO users (username, email, password) VALUES ('john_doe', 'john.doe@example.com', 'hashed_password_1');

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane.smith@example.com')
INSERT INTO users (username, email, password) VALUES ('jane_smith', 'jane.smith@example.com', 'hashed_password_2');

IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice.wonder@example.com')
INSERT INTO users (username, email, password) VALUES ('alice_wonder', 'alice.wonder@example.com', 'hashed_password_3');

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

-- Insert sample expenses
IF NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Lunch at Campus Cafeteria')
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT TOP 1 id FROM expense_categories WHERE name = 'Food & Dining'), 'Lunch at Campus Cafeteria', 'Daily lunch', 12.50, GETDATE(), 'card');

-- Insert default task columns for the first user
IF NOT EXISTS (SELECT 1 FROM task_columns WHERE title = 'To Do' AND user_id = (SELECT TOP 1 id FROM users))
INSERT INTO task_columns (id, user_id, title, color, position) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'To Do', '#e3f2fd', 0);

IF NOT EXISTS (SELECT 1 FROM task_columns WHERE title = 'In Progress' AND user_id = (SELECT TOP 1 id FROM users))
INSERT INTO task_columns (id, user_id, title, color, position) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'In Progress', '#fff3e0', 1);

IF NOT EXISTS (SELECT 1 FROM task_columns WHERE title = 'Done' AND user_id = (SELECT TOP 1 id FROM users))
INSERT INTO task_columns (id, user_id, title, color, position) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), 'Done', '#e8f5e8', 2);

-- Insert sample tasks in To Do column
IF NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Complete Math Assignment' AND user_id = (SELECT TOP 1 id FROM users))
INSERT INTO tasks (id, user_id, column_id, title, description, priority, due_date, position, tags) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT id FROM task_columns WHERE title = 'To Do' AND user_id = (SELECT TOP 1 id FROM users)), 'Complete Math Assignment', 'Solve problems 1-20 from Chapter 5', 'HIGH', DATEADD(day, 2, GETDATE()), 0, 'homework,math');

IF NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Read History Chapter' AND user_id = (SELECT TOP 1 id FROM users))
INSERT INTO tasks (id, user_id, column_id, title, description, priority, due_date, position, tags) VALUES
(NEWID(), (SELECT TOP 1 id FROM users), (SELECT id FROM task_columns WHERE title = 'To Do' AND user_id = (SELECT TOP 1 id FROM users)), 'Read History Chapter', 'Read Chapter 12: World War II', 'MEDIUM', DATEADD(day, 5, GETDATE()), 1, 'reading,history');

