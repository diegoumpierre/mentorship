--liquibase formatted sql

--changeset diego:001-create-product
CREATE TABLE tb_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

--changeset diego:002-create-state
CREATE TABLE tb_state (
    code VARCHAR(2) PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

--changeset diego:003-create-tax-rate
CREATE TABLE tb_tax_rate (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    state_code VARCHAR(2) NOT NULL,
    percent DECIMAL(7,4) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    CONSTRAINT fk_tax_rate_product FOREIGN KEY (product_id) REFERENCES tb_product(id),
    CONSTRAINT fk_tax_rate_state FOREIGN KEY (state_code) REFERENCES tb_state(code)
);

--changeset diego:004-seed-products
INSERT INTO tb_product (id, name, price) VALUES (1, 'Laptop', 1500.00);
INSERT INTO tb_product (id, name, price) VALUES (2, 'T-shirt', 30.00);
INSERT INTO tb_product (id, name, price) VALUES (3, 'Bread', 5.00);

--changeset diego:005-seed-states
INSERT INTO tb_state (code, name) VALUES ('CA', 'California');
INSERT INTO tb_state (code, name) VALUES ('NY', 'New York');
INSERT INTO tb_state (code, name) VALUES ('TX', 'Texas');

--changeset diego:006-seed-tax-rates
-- Laptop: CA mudou de 7.25 pra 7.5 no inicio de 2025
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (1, 'CA', 7.2500, '2024-01-01', '2025-01-01');
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (1, 'CA', 7.5000, '2025-01-01', NULL);
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (1, 'NY', 8.8750, '2024-01-01', NULL);
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (1, 'TX', 6.2500, '2024-01-01', NULL);
-- T-shirt
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (2, 'CA', 7.2500, '2024-01-01', NULL);
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (2, 'NY', 8.8750, '2024-01-01', NULL);
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (2, 'TX', 6.2500, '2024-01-01', NULL);
-- Bread: TX isenta (0%), demais cobram normal
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (3, 'CA', 7.2500, '2024-01-01', NULL);
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (3, 'NY', 8.8750, '2024-01-01', NULL);
INSERT INTO tb_tax_rate (product_id, state_code, percent, effective_from, effective_to) VALUES (3, 'TX', 0.0000, '2024-01-01', NULL);
