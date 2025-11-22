INSERT INTO users (user_id, pwd, email, birth_ymd, nickname, blog_name, bio)
VALUES
('user123', 'password123', 'user123@example.com', '19951012', 'TravelerTom', 'cutecute blog cute', 'Loves exploring new cities and sharing photos')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO posts (user_id, title, og_text, ai_gen_text, password)
VALUES
(1, 'Seoul Night Lights', 'The vibrant lights of Seoul city after sunset.', 'AI: Seoul shines like a digital galaxy.', '1234'),
(1, 'Vancouver Peaks', 'A quiet hike through misty pines.', 'AI: Snow-capped peaks cradle the city below.', '1234'),
(1, 'Jeju Morning Calm', 'Golden sands and calm waves in Jeju.', 'AI: A sunrise woven with tangerine hues.', '1234'),
(1, 'New York Reflections', 'The energy of Manhattan streets.', 'AI: A rhythm of footsteps and yellow cabs.', '1234')
ON CONFLICT DO NOTHING;

INSERT INTO images (post_id, img_path, geo_lat, geo_long, img_dtm, thumb_yn)
VALUES
(1, '/uploads/seoul_night_1.jpg', '37.5665', '126.9780', CURRENT_TIMESTAMP, 'Y'),
(1, '/uploads/seoul_night_2.jpg', '37.5651', '126.9890', CURRENT_TIMESTAMP, 'N'),
(2, '/uploads/vancouver_peak_1.jpg', '49.2827', '-123.1207', CURRENT_TIMESTAMP, 'Y'),
(2, '/uploads/vancouver_peak_2.jpg', '49.2900', '-123.1300', CURRENT_TIMESTAMP, 'N'),
(3, '/uploads/jeju_beach_1.jpg', '33.4996', '126.5312', CURRENT_TIMESTAMP, 'Y'),
(3, '/uploads/jeju_beach_2.jpg', '33.4899', '126.5211', CURRENT_TIMESTAMP, 'N'),
(4, '/uploads/nyc_1.jpg', '40.7128', '-74.0060', CURRENT_TIMESTAMP, 'Y'),
(4, '/uploads/nyc_2.jpg', '40.7138', '-74.0050', CURRENT_TIMESTAMP, 'N')
ON CONFLICT DO NOTHING;
