-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create calendar_events table
CREATE TABLE IF NOT EXISTS calendar_events (
    id VARCHAR(36) PRIMARY KEY,
    user_id UUID NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    description TEXT,
    event_type VARCHAR(20),
    meeting_links VARCHAR(512),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Finance related tables for budget tracking

-- Categories for expenses
CREATE TABLE IF NOT EXISTS expense_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(7), -- For UI color coding (hex color)
    icon VARCHAR(50), -- Icon name for UI
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    category_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    amount DECIMAL(10,2) NOT NULL,
    expense_date DATE NOT NULL,
    payment_method VARCHAR(50), -- cash, card, digital_wallet, etc.
    location VARCHAR(200),
    receipt_url VARCHAR(500), -- Optional receipt attachment
    tags VARCHAR(500), -- Comma-separated tags for additional categorization
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_frequency VARCHAR(20), -- weekly, monthly, yearly
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id) ON DELETE NO ACTION
);

-- Budget limits per category per month
CREATE TABLE IF NOT EXISTS budget_limits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    category_id UUID NOT NULL,
    budget_month INTEGER NOT NULL, -- 1-12
    budget_year INTEGER NOT NULL,
    limit_amount DECIMAL(10,2) NOT NULL,
    alert_threshold DECIMAL(5,2) DEFAULT 80.0, -- Alert when X% of budget is used
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id) ON DELETE NO ACTION,
    UNIQUE (user_id, category_id, budget_month, budget_year)
);

-- Financial goals/savings targets
CREATE TABLE IF NOT EXISTS financial_goals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    goal_name VARCHAR(200) NOT NULL,
    description TEXT,
    target_amount DECIMAL(10,2) NOT NULL,
    current_amount DECIMAL(10,2) DEFAULT 0.0,
    target_date DATE,
    goal_type VARCHAR(50), -- savings, debt_payoff, purchase, etc.
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Task management tables

-- Task columns (customizable per user)
CREATE TABLE IF NOT EXISTS task_columns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    title VARCHAR(100) NOT NULL,
    color VARCHAR(7), -- hex color
    position INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION
);

-- Tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    column_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')) DEFAULT 'MEDIUM',
    assigned_to UUID, -- for future collaboration
    due_date TIMESTAMP,
    position INTEGER DEFAULT 0, -- for ordering within columns
    tags VARCHAR(1000), -- comma-separated tags
    project_id UUID, -- for future project grouping
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE NO ACTION,
    FOREIGN KEY (column_id) REFERENCES task_columns(id) ON DELETE NO ACTION,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE NO ACTION
);

-- Task attachments (future enhancement)
CREATE TABLE IF NOT EXISTS task_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    task_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_expenses_user_date ON expenses(user_id, expense_date);
CREATE INDEX IF NOT EXISTS idx_expenses_category ON expenses(category_id);
CREATE INDEX IF NOT EXISTS idx_budget_limits_user_period ON budget_limits(user_id, budget_year, budget_month);
CREATE INDEX IF NOT EXISTS idx_expense_categories_user ON expense_categories(user_id);

-- Task management indexes
CREATE INDEX IF NOT EXISTS idx_task_columns_user ON task_columns(user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_user_column ON tasks(user_id, column_id);
CREATE INDEX IF NOT EXISTS idx_tasks_due_date ON tasks(due_date);
CREATE INDEX IF NOT EXISTS idx_task_attachments_task ON task_attachments(task_id);
