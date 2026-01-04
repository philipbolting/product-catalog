CREATE TABLE brand (
    id SERIAL PRIMARY KEY,
    name varchar(50) UNIQUE,
    created TIMESTAMP WITH TIME ZONE,
    last_modified TIMESTAMP WITH TIME ZONE
);