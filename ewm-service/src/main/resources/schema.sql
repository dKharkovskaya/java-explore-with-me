CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE CHECK (name <> ''),
    CONSTRAINT uq_category_name UNIQUE (name)
);

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
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE,
    FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
    requester_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED')),
    CONSTRAINT uq_requester_event UNIQUE (requester_id, event_id),
    FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(50) NOT NULL UNIQUE CHECK (title <> ''),
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_compilations_title UNIQUE (title)
);

CREATE TABLE IF NOT EXISTS event_compilation (
    event_id BIGINT REFERENCES events(id) ON DELETE CASCADE,
    compilation_id BIGINT REFERENCES compilations(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);