CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL CHECK (name <> ''),
    email VARCHAR(254) NOT NULL UNIQUE CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name <> '')
);

CREATE INDEX idx_categories_name ON categories(name);

CREATE TABLE IF NOT EXISTS locations (
    id BIGSERIAL PRIMARY KEY,
    lat NUMERIC(8,6) NOT NULL CHECK (lat BETWEEN -90 AND 90),
    lon NUMERIC(9,6) NOT NULL CHECK (lon BETWEEN -180 AND 180)
);

CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    annotation TEXT NOT NULL CHECK (annotation <> ''),
    description TEXT NOT NULL CHECK (description <> ''),
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    location_lat NUMERIC(8,6) NOT NULL,
    location_lon NUMERIC(9,6) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit INTEGER NOT NULL DEFAULT 0 CHECK (participant_limit >= 0),
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    title VARCHAR(120) NOT NULL CHECK (title <> ''),
    state VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED')),
    initiator_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL
);

CREATE INDEX idx_events_initiator_id ON events(initiator_id);
CREATE INDEX idx_events_category_id ON events(category_id);
CREATE INDEX idx_events_event_date ON events(event_date);
CREATE INDEX idx_events_state ON events(state);

CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
    requester_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    UNIQUE (event_id, requester_id)
);

CREATE INDEX idx_requests_event_id ON participation_requests(event_id);
CREATE INDEX idx_requests_requester_id ON participation_requests(requester_id);
CREATE INDEX idx_requests_status ON participation_requests(status);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL UNIQUE CHECK (title <> ''),
    pinned BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_compilations_pinned ON compilations(pinned);

CREATE TABLE IF NOT EXISTS event_compilation (
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations(id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, compilation_id)
);

CREATE INDEX idx_event_compilation_event_id ON event_compilation(event_id);
CREATE INDEX idx_event_compilation_compilation_id ON event_compilation(compilation_id);