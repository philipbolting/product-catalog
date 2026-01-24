CREATE SEQUENCE seq_product START 1 INCREMENT 1;

CREATE TABLE product (
    id BIGINT NOT NULL,
    brand_id BIGINT REFERENCES brand(id) NOT NULL,
    category_id BIGINT REFERENCES category(id) NOT NULL,
    slug VARCHAR(50) UNIQUE,
    name VARCHAR(50) UNIQUE,
    description TEXT,
    created TIMESTAMP WITH TIME ZONE,
    last_modified TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);