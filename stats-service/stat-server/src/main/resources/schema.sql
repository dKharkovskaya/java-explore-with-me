drop table if exists stats cascade;

CREATE TABLE IF NOT EXISTS stats (
    id BIGSERIAL PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    uri VARCHAR(255) NOT NULL,
    ip VARCHAR(255) NOT NULL,
    time_stamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);