-- Ensure duplicate data is removed
DELETE FROM album;
DELETE FROM music_band;
DELETE FROM app_user;
DELETE FROM label;

-- Step 1: Insert Users
INSERT INTO app_user (id, username, password_hash, role, registered_at)
VALUES
(1, 'test_user', 'hashed_password', 'USER', CURRENT_TIMESTAMP),
(2, 'test_admin', 'hashed_password_admin', 'ADMIN', CURRENT_TIMESTAMP);

-- Step 2: Temporarily Drop NOT NULL Constraint on best_album_id
ALTER TABLE music_band ALTER COLUMN best_album_id DROP NOT NULL;

-- Step 1: Insert into album table
INSERT INTO album (id, name, sales, music_band_id)
VALUES (1, 'Sticky Fingers', 500000, 1),
       (2, 'Abbey Road', 600000, 2);

-- Step 2: Insert into music_band table (use valid genre and existing best_album_id)
INSERT INTO music_band (id, name, coordinates_x, coordinates_y, genre, number_of_participants, albums_count, best_album_id, label_id, singles_count, description, establishment_date, created_by, creation_date, created_at)
VALUES (1, 'The Rolling Stones', 123, 45.67, 'PSYCHEDELIC_ROCK', 5, 10, 1, 1, 2, 'Legendary British rock band', '1960-03-01', 1, NOW(), NOW()),
       (2, 'The Beatles', 789, 123.45, 'SOUL', 4, 8, 2, 1, 2, 'Legendary British rock band', '1960-03-01', 1, NOW(), NOW());

-- Step 5: Update best_album_id in Music Bands
UPDATE music_band SET best_album_id = 1 WHERE id = 1;
UPDATE music_band SET best_album_id = 2 WHERE id = 2;

-- Step 6: Reapply NOT NULL Constraint on best_album_id
ALTER TABLE music_band ALTER COLUMN best_album_id SET NOT NULL;
