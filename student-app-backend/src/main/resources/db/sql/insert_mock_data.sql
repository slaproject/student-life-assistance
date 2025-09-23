-- Mock data for student-life-assistance application

-- Insert mock users
INSERT INTO users (username, email, password) 
SELECT 'john_doe', 'john.doe@example.com', 'hashed_password_1'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.doe@example.com');

INSERT INTO users (username, email, password) 
SELECT 'jane_smith', 'jane.smith@example.com', 'hashed_password_2'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane.smith@example.com');

INSERT INTO users (username, email, password) 
SELECT 'alice_wonder', 'alice.wonder@example.com', 'hashed_password_3'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice.wonder@example.com');

-- Insert default expense categories for testing
INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Food & Dining', 'Restaurants, groceries, takeout', '#FF6B6B', 'restaurant'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Food & Dining');

INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Transportation', 'Gas, public transport, parking', '#4ECDC4', 'car'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Transportation');

INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Entertainment', 'Movies, games, subscriptions', '#45B7D1', 'movie'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Entertainment');

INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Education', 'Books, courses, supplies', '#96CEB4', 'book'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Education');

INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Health & Fitness', 'Gym, medical, pharmacy', '#FFEAA7', 'health'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Health & Fitness');

INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Shopping', 'Clothes, electronics, misc', '#DDA0DD', 'shopping'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Shopping');

INSERT INTO expense_categories (id, user_id, name, description, color, icon) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Utilities', 'Phone, internet, electricity', '#FFB347', 'utility'
WHERE NOT EXISTS (SELECT 1 FROM expense_categories WHERE name = 'Utilities');

-- Insert sample expenses
INSERT INTO expenses (id, user_id, category_id, title, description, amount, expense_date, payment_method) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), (SELECT id FROM expense_categories WHERE name = 'Food & Dining' LIMIT 1), 'Lunch at Campus Cafeteria', 'Daily lunch', 12.50, CURRENT_DATE, 'card'
WHERE NOT EXISTS (SELECT 1 FROM expenses WHERE title = 'Lunch at Campus Cafeteria');

-- Insert default task columns for the first user
INSERT INTO task_columns (id, user_id, title, color, position) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'To Do', '#e3f2fd', 0
WHERE NOT EXISTS (SELECT 1 FROM task_columns WHERE title = 'To Do' AND user_id = (SELECT id FROM users LIMIT 1));

INSERT INTO task_columns (id, user_id, title, color, position) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'In Progress', '#fff3e0', 1
WHERE NOT EXISTS (SELECT 1 FROM task_columns WHERE title = 'In Progress' AND user_id = (SELECT id FROM users LIMIT 1));

INSERT INTO task_columns (id, user_id, title, color, position) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), 'Done', '#e8f5e8', 2
WHERE NOT EXISTS (SELECT 1 FROM task_columns WHERE title = 'Done' AND user_id = (SELECT id FROM users LIMIT 1));

-- Insert sample tasks in To Do column
INSERT INTO tasks (id, user_id, column_id, title, description, priority, due_date, position, tags) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), (SELECT id FROM task_columns WHERE title = 'To Do' AND user_id = (SELECT id FROM users LIMIT 1)), 'Complete Math Assignment', 'Solve problems 1-20 from Chapter 5', 'HIGH', CURRENT_TIMESTAMP + INTERVAL '2 days', 0, 'homework,math'
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Complete Math Assignment' AND user_id = (SELECT id FROM users LIMIT 1));

INSERT INTO tasks (id, user_id, column_id, title, description, priority, due_date, position, tags) 
SELECT uuid_generate_v4(), (SELECT id FROM users LIMIT 1), (SELECT id FROM task_columns WHERE title = 'To Do' AND user_id = (SELECT id FROM users LIMIT 1)), 'Read History Chapter', 'Read Chapter 12: World War II', 'MEDIUM', CURRENT_TIMESTAMP + INTERVAL '5 days', 1, 'reading,history'
WHERE NOT EXISTS (SELECT 1 FROM tasks WHERE title = 'Read History Chapter' AND user_id = (SELECT id FROM users LIMIT 1));

