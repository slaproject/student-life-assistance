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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);