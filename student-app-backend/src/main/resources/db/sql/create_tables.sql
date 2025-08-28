IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' AND xtype='U')
CREATE TABLE users (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='calendar_events' AND xtype='U')
CREATE TABLE calendar_events (
    id VARCHAR(36) PRIMARY KEY,
    user_id UNIQUEIDENTIFIER NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    description TEXT,
    event_type VARCHAR(20),
    meeting_links VARCHAR(512),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Finance related tables for budget tracking

-- Categories for expenses
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='expense_categories' AND xtype='U')
CREATE TABLE expense_categories (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    color VARCHAR(7), -- For UI color coding (hex color)
    icon VARCHAR(50), -- Icon name for UI
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Expenses table
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='expenses' AND xtype='U')
CREATE TABLE expenses (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    category_id UNIQUEIDENTIFIER NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    amount DECIMAL(10,2) NOT NULL,
    expense_date DATE NOT NULL,
    payment_method VARCHAR(50), -- cash, card, digital_wallet, etc.
    location VARCHAR(200),
    receipt_url VARCHAR(500), -- Optional receipt attachment
    tags VARCHAR(500), -- Comma-separated tags for additional categorization
    is_recurring BIT DEFAULT 0,
    recurring_frequency VARCHAR(20), -- weekly, monthly, yearly
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id) ON DELETE NO ACTION
);

-- Budget limits per category per month
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='budget_limits' AND xtype='U')
CREATE TABLE budget_limits (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    category_id UNIQUEIDENTIFIER NOT NULL,
    budget_month INT NOT NULL, -- 1-12
    budget_year INT NOT NULL,
    limit_amount DECIMAL(10,2) NOT NULL,
    alert_threshold DECIMAL(5,2) DEFAULT 80.0, -- Alert when X% of budget is used
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES expense_categories(id) ON DELETE NO ACTION,
    UNIQUE (user_id, category_id, budget_month, budget_year)
);

-- Financial goals/savings targets
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='financial_goals' AND xtype='U')
CREATE TABLE financial_goals (
    id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    goal_name VARCHAR(200) NOT NULL,
    description TEXT,
    target_amount DECIMAL(10,2) NOT NULL,
    current_amount DECIMAL(10,2) DEFAULT 0.0,
    target_date DATE,
    goal_type VARCHAR(50), -- savings, debt_payoff, purchase, etc.
    is_active BIT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better performance
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_expenses_user_date' AND object_id = OBJECT_ID('expenses'))
CREATE INDEX idx_expenses_user_date ON expenses(user_id, expense_date);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_expenses_category' AND object_id = OBJECT_ID('expenses'))
CREATE INDEX idx_expenses_category ON expenses(category_id);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_budget_limits_user_period' AND object_id = OBJECT_ID('budget_limits'))
CREATE INDEX idx_budget_limits_user_period ON budget_limits(user_id, budget_year, budget_month);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'idx_expense_categories_user' AND object_id = OBJECT_ID('expense_categories'))
CREATE INDEX idx_expense_categories_user ON expense_categories(user_id);
