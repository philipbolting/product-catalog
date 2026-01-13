CREATE SEQUENCE seq_category START 1 INCREMENT 1;

CREATE TABLE category (
    id BIGINT NOT NULL,
    parent_id BIGINT REFERENCES category(id),
    slug VARCHAR(50) UNIQUE,
    name VARCHAR(50) UNIQUE,
    description TEXT,
    created TIMESTAMP WITH TIME ZONE,
    last_modified TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);