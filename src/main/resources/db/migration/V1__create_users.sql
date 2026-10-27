CREATE TABLE chat_user (
    google_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL UNIQUE,
    picture TEXT,
    last_login TIMESTAMP WITH TIME ZONE NOT NULL
);
