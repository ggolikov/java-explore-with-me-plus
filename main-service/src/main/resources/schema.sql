CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    category_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE events (
    event_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    annotation TEXT,
    description TEXT,
    category_id BIGINT NOT NULL REFERENCES categories(category_id),
    initiator_id BIGINT NOT NULL REFERENCES users(user_id),
    event_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    paid BOOLEAN DEFAULT FALSE,
    participant_limit INT DEFAULT 0,
    state VARCHAR(50) DEFAULT 'PENDING',
    views INT DEFAULT 0
);

CREATE TABLE event_requests (
    request_id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    requester_id BIGINT NOT NULL REFERENCES users(user_id),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE compilations (
    compilation_id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN DEFAULT FALSE
);

-- Link events to compilations
CREATE TABLE compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(compilation_id),
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    PRIMARY KEY (compilation_id, event_id)
);
