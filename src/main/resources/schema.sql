DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS addresses;

CREATE TABLE members
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name          VARCHAR(100) NOT NULL
);

CREATE TABLE addresses
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id        BIGINT       NOT NULL,
    recipient_name   VARCHAR(100) NOT NULL,
    recipient_phone  VARCHAR(20)  NOT NULL,
    address          VARCHAR(255) NOT NULL,
    delivery_request VARCHAR(255) NOT NULL,
    is_default       BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_addresses_member FOREIGN KEY (member_id) REFERENCES members (id)
);