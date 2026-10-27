CREATE TABLE chat_room (
    id UUID PRIMARY KEY,
    room_type VARCHAR(16) NOT NULL CHECK (room_type IN ('PRIVATE', 'GROUP')),
    conversation_key VARCHAR(600) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE chat_room_participant (
    room_id UUID NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
    user_id VARCHAR(255) NOT NULL REFERENCES chat_user(google_id) ON DELETE CASCADE,
    PRIMARY KEY (room_id, user_id)
);

CREATE INDEX idx_chat_room_participant_user ON chat_room_participant(user_id, room_id);

CREATE TABLE chat_message (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
    sender_id VARCHAR(255) NOT NULL REFERENCES chat_user(google_id),
    content TEXT NOT NULL CHECK (length(trim(content)) > 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(16) NOT NULL CHECK (status IN ('SENT', 'DELIVERED', 'READ'))
);

CREATE INDEX idx_chat_message_room_created ON chat_message(room_id, created_at DESC, id DESC);
CREATE INDEX idx_chat_message_unread ON chat_message(sender_id, status, room_id);
