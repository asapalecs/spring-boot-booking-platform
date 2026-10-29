INSERT INTO users (id, first_name, last_name, email, password, phone_number, role, enabled, created_at, updated_at) VALUES
(1, 'Admin', 'System', 'admin@airbnb.com', '$2a$10$.nYPKKoZLlGBxstJcE.wyetqqoI0AX7fxZpChfFlOfEn7LDmncCu6', '+40700000001', 'ADMIN', true, NOW(), NOW()),
(2, 'Ion', 'Popescu', 'ion.popescu@example.com', '$2a$10$.nYPKKoZLlGBxstJcE.wyetqqoI0AX7fxZpChfFlOfEn7LDmncCu6', '+40700000002', 'HOST', true, NOW(), NOW()),
(3, 'Maria', 'Ionescu', 'maria.ionescu@example.com', '$2a$10$.nYPKKoZLlGBxstJcE.wyetqqoI0AX7fxZpChfFlOfEn7LDmncCu6', '+40700000003', 'GUEST', true, NOW(), NOW());

INSERT INTO properties (id, name, description, address, city, country, price_per_night, max_guests, property_type, available, host_id, created_at, updated_at) VALUES
(1, 'Apartament Central Bucuresti', 'Apartament modern in centrul Bucurestiului, aproape de toate atractiile.', 'Calea Victoriei 10', 'Bucuresti', 'Romania', 150.00, 4, 'APARTMENT', true, 2, NOW(), NOW()),
(2, 'Vila la Mare Constanta', 'Vila spatioasa cu vedere la mare, piscina privata si gradina.', 'Bulevardul Mamaia 55', 'Constanta', 'Romania', 350.00, 8, 'VILLA', true, 2, NOW(), NOW()),
(3, 'Cabana Montana Sinaia', 'Cabana confortabila in munti, perfect pentru schi si drumetii.', 'Strada Carpatilor 22', 'Sinaia', 'Romania', 200.00, 6, 'HOUSE', true, 1, NOW(), NOW());

INSERT INTO rooms (id, room_number, room_type, capacity, price, available, property_id, created_at) VALUES
(1, 'Dormitor Principal', 'DOUBLE', 2, 80.00, true, 1, NOW()),
(2, 'Dormitor Secundar', 'SINGLE', 1, 50.00, true, 1, NOW()),
(3, 'Suite Ocean View', 'SUITE', 4, 200.00, true, 2, NOW()),
(4, 'Dormitor Oaspeti', 'DOUBLE', 2, 100.00, true, 2, NOW()),
(5, 'Camera Rustica', 'DOUBLE', 2, 90.00, true, 3, NOW());

INSERT INTO amenities (id, name, description, icon) VALUES
(1, 'WiFi', 'Internet de mare viteza', 'fa-wifi'),
(2, 'Aer Conditionat', 'Sistem complet de climatizare', 'fa-snowflake'),
(3, 'Piscina', 'Piscina exterioara incalzita', 'fa-swimming-pool'),
(4, 'Parcare', 'Parcare gratuita disponibila', 'fa-parking');

INSERT INTO property_amenity (property_id, amenity_id) VALUES
(1, 1), (1, 2), (1, 4),
(2, 1), (2, 2), (2, 3), (2, 4),
(3, 1), (3, 4);

INSERT INTO reservations (id, check_in_date, check_out_date, number_of_guests, total_price, status, guest_id, property_id, created_at) VALUES
(1, '2025-07-01', '2025-07-07', 2, 1050.00, 'CONFIRMED', 3, 1, NOW()),
(2, '2025-08-10', '2025-08-17', 4, 2450.00, 'COMPLETED', 3, 2, NOW());

INSERT INTO payments (id, amount, payment_method, status, transaction_id, payment_date, created_at, reservation_id) VALUES
(1, 1050.00, 'CREDIT_CARD', 'COMPLETED', 'TXN-001-2025', NOW(), NOW(), 1),
(2, 2450.00, 'PAYPAL', 'COMPLETED', 'TXN-002-2025', NOW(), NOW(), 2);

INSERT INTO reviews (id, rating, comment, created_at, user_id, property_id) VALUES
(1, 5, 'Apartament superb, curat si bine echipat. Gazda foarte comunicativa!', NOW(), 3, 1),
(2, 4, 'Vila minunata cu vedere la mare. Recomandam cu caldura!', NOW(), 3, 2);

ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE properties ALTER COLUMN id RESTART WITH 100;
ALTER TABLE rooms ALTER COLUMN id RESTART WITH 100;
ALTER TABLE amenities ALTER COLUMN id RESTART WITH 100;
ALTER TABLE reservations ALTER COLUMN id RESTART WITH 100;
ALTER TABLE payments ALTER COLUMN id RESTART WITH 100;
ALTER TABLE reviews ALTER COLUMN id RESTART WITH 100;
