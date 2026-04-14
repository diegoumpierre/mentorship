--liquibase formatted sql

--changeset diego:001-create-venue
CREATE TABLE tb_venue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    capacity INT
);

--changeset diego:002-create-zone
CREATE TABLE tb_zone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    capacity INT,
    venue_id BIGINT,
    CONSTRAINT fk_zone_venue FOREIGN KEY (venue_id) REFERENCES tb_venue(id)
);

--changeset diego:003-create-user
CREATE TABLE tb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);

--changeset diego:004-create-show
CREATE TABLE tb_show (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    maximum_capacity INT,
    venue_id BIGINT,
    date TIMESTAMP,
    CONSTRAINT fk_show_venue FOREIGN KEY (venue_id) REFERENCES tb_venue(id)
);

--changeset diego:005-create-seat
CREATE TABLE tb_seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sold BOOLEAN DEFAULT FALSE,
    number INT,
    zone_id BIGINT,
    show_id BIGINT,
    user_id BIGINT,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_seat_zone FOREIGN KEY (zone_id) REFERENCES tb_zone(id),
    CONSTRAINT fk_seat_show FOREIGN KEY (show_id) REFERENCES tb_show(id),
    CONSTRAINT fk_seat_user FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

--changeset diego:006-seed-venues
INSERT INTO tb_venue (id, name, address, capacity) VALUES (1, 'Allianz Parque', 'Av. Francisco Matarazzo, 1705', 43000);
INSERT INTO tb_venue (id, name, address, capacity) VALUES (2, 'Espaço Unimed', 'Rua Tagipuru, 795', 8000);

--changeset diego:007-seed-zones
INSERT INTO tb_zone (id, name, capacity, venue_id) VALUES (1, 'Pista', 2, 1);
INSERT INTO tb_zone (id, name, capacity, venue_id) VALUES (2, 'Cadeira Superior', 2, 1);

--changeset diego:008-seed-users
INSERT INTO tb_user (id, name, email, password) VALUES (1, 'Diego', 'diego@test.com', '123');
INSERT INTO tb_user (id, name, email, password) VALUES (2, 'Ana', 'ana@test.com', '123');

--changeset diego:009-seed-shows
INSERT INTO tb_show (id, name, maximum_capacity, venue_id, date) VALUES (1, 'Metallica - M72 Tour', 4, 1, '2026-06-15 21:00:00');
INSERT INTO tb_show (id, name, maximum_capacity, venue_id, date) VALUES (2, 'Iron Maiden - Future Past', 4, 2, '2026-08-20 20:30:00');

--changeset diego:010-seed-seats
INSERT INTO tb_seat (id, sold, number, zone_id, show_id, version) VALUES (1, FALSE, 1, 1, 1, 0);
INSERT INTO tb_seat (id, sold, number, zone_id, show_id, version) VALUES (2, FALSE, 2, 1, 1, 0);
INSERT INTO tb_seat (id, sold, number, zone_id, show_id, version) VALUES (3, FALSE, 1, 2, 1, 0);
INSERT INTO tb_seat (id, sold, number, zone_id, show_id, user_id, version) VALUES (4, TRUE, 2, 2, 1, 2, 1);

--changeset diego:011-add-seat-reservation
ALTER TABLE tb_seat ADD COLUMN reserved_by_user_id BIGINT;
ALTER TABLE tb_seat ADD COLUMN reserved_until TIMESTAMP;
ALTER TABLE tb_seat ADD CONSTRAINT fk_seat_reserved_by FOREIGN KEY (reserved_by_user_id) REFERENCES tb_user(id);
