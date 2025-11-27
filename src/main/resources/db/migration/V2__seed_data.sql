-- User #1
INSERT INTO users (user_id, pwd, email, birth_ymd, nickname, blog_name, bio)
VALUES
('user123', 'password123', 'user123@example.com', '19951012', 'TravelerTom', 'cutecute blog cute', 'Loves exploring new cities and sharing photos')
ON CONFLICT (user_id) DO NOTHING;

-- Posts for user123
INSERT INTO posts (user_id, title, og_text, ai_gen_text, password)
VALUES
((SELECT id FROM users WHERE user_id = 'user123'),
 'Seoul Night Lights', 'The vibrant lights of Seoul city after sunset.', 'AI: Seoul shines like a digital galaxy.', '1234'),
((SELECT id FROM users WHERE user_id = 'user123'),
 'Vancouver Peaks', 'A quiet hike through misty pines.', 'AI: Snow-capped peaks cradle the city below.', '1234'),
((SELECT id FROM users WHERE user_id = 'user123'),
 'Jeju Morning Calm', 'Golden sands and calm waves in Jeju.', 'AI: A sunrise woven with tangerine hues.', '1234'),
((SELECT id FROM users WHERE user_id = 'user123'),
 'New York Reflections', 'The energy of Manhattan streets.', 'AI: A rhythm of footsteps and yellow cabs.', '1234')
ON CONFLICT DO NOTHING;

-- Images for user123 posts
INSERT INTO images (post_id, img_path, img_file_name, geo_lat, geo_long, img_dtm, thumb_yn)
VALUES
((SELECT post_id FROM posts WHERE title = 'Seoul Night Lights' AND user_id = (SELECT id FROM users WHERE user_id = 'user123')),
 '/uploads/seoul_night_1.jpg', 'seoul_night_1.jpg', '37.5665', '126.9780', CURRENT_TIMESTAMP, 'Y'),
((SELECT post_id FROM posts WHERE title = 'Seoul Night Lights' AND user_id = (SELECT id FROM users WHERE user_id = 'user123')),
 '/uploads/seoul_night_2.jpg', 'seoul_night_1.jpg', '37.5651', '126.9890', CURRENT_TIMESTAMP, 'N'),
((SELECT post_id FROM posts WHERE title = 'Vancouver Peaks' AND user_id = (SELECT id FROM users WHERE user_id = 'user123')),
 '/uploads/vancouver_peak_1.jpg', 'seoul_night_1.jpg', '49.2827', '-123.1207', CURRENT_TIMESTAMP, 'Y'),
((SELECT post_id FROM posts WHERE title = 'Vancouver Peaks' AND user_id = (SELECT id FROM users WHERE user_id = 'user123')),
 '/uploads/vancouver_peak_2.jpg', 'seoul_night_1.jpg', '49.2900', '-123.1300', CURRENT_TIMESTAMP, 'N');

-- User #2 (test account)
INSERT INTO users (user_id, pwd, email, birth_ymd, nickname, profile_img, blog_name, bio,
                   rgst_dtm, chng_dtm, last_login_dtm)
VALUES (
    'test12345',
    '$2a$10$6VgbR0pdXJ/Xl/IQH4O8JecZWB2/q2cFPiGkNMzvlVahKNGl/s74K',
    'test1234@gmail.com',
    '19991111',
    'cutiepie',
    'profiles/1763882165078_IMG_5717.JPG',
    'cutest girls''s blog',
    'ahaahh ahahhah lol',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
)
ON CONFLICT (user_id) DO NOTHING;

-- Posts for test user
INSERT INTO posts (user_id, title, og_text, ai_gen_text, rgst_dtm, chng_dtm)
VALUES
((SELECT id FROM users WHERE user_id = 'test12345'),
 'Seoul Night Lights',
 'The vibrant lights of Seoul city after sunset.',
 'AI says: Seoul glows like a digital galaxy.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM users WHERE user_id = 'test12345'),
 'Vancouver Peaks',
 'Hiking through the misty pines of Vancouver.',
 'AI says: Mountains cradle the city like a guardian.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Images for test user
INSERT INTO images (post_id, img_path, img_file_name, geo_lat, geo_long, thumb_yn, img_dtm)
VALUES
((SELECT post_id FROM posts WHERE title = 'Seoul Night Lights' AND user_id = (SELECT id FROM users WHERE user_id = 'test12345')),
 '/uploads/seoul_night_1.jpg', 'seoul_night_1.jpg', '37.5665', '126.9780', 'Y', CURRENT_TIMESTAMP),
((SELECT post_id FROM posts WHERE title = 'Vancouver Peaks' AND user_id = (SELECT id FROM users WHERE user_id = 'test12345')),
 '/uploads/vancouver_peak_1.jpg', 'vancouver_peak_1.jpg', '49.2827', '-123.1207', 'Y', CURRENT_TIMESTAMP);


-- =====================================================
-- User 1: Seoul area (approx 37.50 ~ 37.60N, 126.90 ~ 127.00E)
-- =====================================================
INSERT INTO posts (user_id, title, og_text, ai_gen_text, password, rgst_dtm, chng_dtm)
SELECT
    ((select id from users where user_id='user123')),
    'User1 Post ' || i,
    'Original text for User1 Post ' || i,
    'AI generated content for User1 Post ' || i,
    'password1234',
    NOW(), NOW()
FROM generate_series(1, 20) AS i;

INSERT INTO images (post_id, geo_lat, geo_long, img_path, img_file_name, img_dtm, rgst_dtm, thumb_yn)
SELECT
    p.post_id,
    37.50 + ((i-1) * 0.005) + random() * 0.002,  -- scattered around Seoul
    126.90 + ((i-1) * 0.005) + random() * 0.002,
    'profiles/user1_thumb_' || p.post_id || '.jpg',
    'user1_thumb_' || p.post_id || '.jpg',
    NOW(), NOW(), 'Y'
FROM posts p
JOIN generate_series(1, 20) AS i ON p.post_id = i;

-- =====================================================
-- User 2: Vancouver area (approx 49.25 ~ 49.35N, -123.15 ~ -123.05E)
-- =====================================================
INSERT INTO posts (user_id, title, og_text, ai_gen_text, password, rgst_dtm, chng_dtm)
SELECT
    (select id from users where user_id='test12345'),
    'User2 Post ' || i,
    'Original text for User2 Post ' || i,
    'AI generated content for User2 Post ' || i,
    'password1234',
    NOW(), NOW()
FROM generate_series(21, 40) AS i;

INSERT INTO images (post_id, geo_lat, geo_long, img_path, img_file_name, img_dtm, rgst_dtm, thumb_yn)
SELECT
    p.post_id,
    49.25 + ((i-21) * 0.005) + random() * 0.002,  -- scattered around Vancouver
    -123.15 + ((i-21) * 0.005) + random() * 0.002,
    'profiles/user2_thumb_' || p.post_id || '.jpg',
    'user2_thumb_' || p.post_id || '.jpg',
    NOW(), NOW(), 'Y'
FROM posts p
JOIN generate_series(21, 40) AS i ON p.post_id = i;
