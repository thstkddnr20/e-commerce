DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS sku;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category_closure;
DROP TABLE IF EXISTS category;

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

CREATE TABLE category
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE category_closure
(
    ancestor   BIGINT NOT NULL,
    descendant BIGINT NOT NULL,
    depth      INT    NOT NULL,
    PRIMARY KEY (ancestor, descendant),
    CONSTRAINT fk_closure_ancestor FOREIGN KEY (ancestor) REFERENCES category (id),
    CONSTRAINT fk_closure_descendant FOREIGN KEY (descendant) REFERENCES category (id)
);

CREATE TABLE product
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT        NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    description VARCHAR(1000) NOT NULL,
    status      VARCHAR(20)   NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE sku
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id  BIGINT       NOT NULL,
    option_name VARCHAR(100) NOT NULL,
    price       INT          NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    CONSTRAINT fk_sku_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT uk_sku_option UNIQUE (product_id, option_name)
);