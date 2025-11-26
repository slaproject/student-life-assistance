-- Create usage_tracking table for rate limiting
CREATE TABLE IF NOT EXISTS usage_tracking (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    year_month VARCHAR(7) NOT NULL,
    request_count INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_usage_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_month UNIQUE (user_id, year_month)
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_usage_user_month ON usage_tracking(user_id, year_month);
