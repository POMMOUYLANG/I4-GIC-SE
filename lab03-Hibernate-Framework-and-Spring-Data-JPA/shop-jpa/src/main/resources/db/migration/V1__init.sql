-- 5 Sequences with allocationSize = 50 matching Hibernate generators
CREATE SEQUENCE customer_seq   START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE product_seq    START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE tag_seq        START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE orders_seq     START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE order_line_seq START WITH 1 INCREMENT BY 50;

-- Table: customer
CREATE TABLE customer (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    street      VARCHAR(150) NOT NULL,
    city        VARCHAR(80)  NOT NULL,
    zip         VARCHAR(20)  NOT NULL,
    ship_street VARCHAR(150) NOT NULL,
    ship_city   VARCHAR(80)  NOT NULL,
    ship_zip    VARCHAR(20)  NOT NULL
);

-- Table: product
CREATE TABLE product (
    id          BIGINT PRIMARY KEY,
    sku         VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    amount      DECIMAL(12,2) NOT NULL,
    currency    CHAR(3)      NOT NULL,
    category    VARCHAR(20)  NOT NULL,
    stock       INT          NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

-- Table: tag
CREATE TABLE tag (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE
);

-- Table: product_tag (join table)
CREATE TABLE product_tag (
    product_id  BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    PRIMARY KEY (product_id, tag_id),
    CONSTRAINT fk_product_tag_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    CONSTRAINT fk_product_tag_tag     FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

-- Table: orders (escaped name)
CREATE TABLE orders (
    id          BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status      VARCHAR(20) NOT NULL,
    placed_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    version     BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- Table: order_line
CREATE TABLE order_line (
    id          BIGINT PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT    NOT NULL CHECK (quantity > 0),
    amount      DECIMAL(12,2) NOT NULL,
    currency    CHAR(3) NOT NULL,
    CONSTRAINT fk_order_line_orders  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_line_product FOREIGN KEY (product_id) REFERENCES product(id)
);
