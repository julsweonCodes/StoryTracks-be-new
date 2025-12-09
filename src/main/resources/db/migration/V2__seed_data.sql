DO $$
DECLARE
    img_counter INTEGER := 1;
    uid1 INT;
    uid2 INT;
    pid INT;
BEGIN

-- ================================
-- USER 1
-- ================================
INSERT INTO users (user_id, pwd, email, birth_ymd, nickname, blog_name, bio)
VALUES ('user123', 'password123', 'user123@example.com', '19951012',
        'TravelerTom', 'cutecute blog cute',
        'Loves exploring new cities and sharing photos')
ON CONFLICT (user_id) DO NOTHING;

SELECT id INTO uid1 FROM users WHERE user_id='user123';


-- ================================
-- USER 2
-- ================================
INSERT INTO users (user_id, pwd, email, birth_ymd, nickname, profile_img, blog_name, bio,
                   rgst_dtm, chng_dtm, last_login_dtm)
VALUES ('test12345',
        '$2a$10$6VgbR0pdXJ/Xl/IQH4O8JecZWB2/q2cFPiGkNMzvlVahKNGl/s74K',
        'test1234@gmail.com', '19991111', 'cutiepie',
        'profiles/1763882165078_IMG_5717.JPG',
        'cutest girls''s blog', 'ahaahh ahahhah lol',
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (user_id) DO NOTHING;

SELECT id INTO uid2 FROM users WHERE user_id='test12345';


-- =============================================
-- USER 1 — 20 POSTS + IMAGES
-- =============================================
FOR i IN 1..20 LOOP
    INSERT INTO posts (user_id, title, og_text, ai_gen_text, password, rgst_dtm, chng_dtm)
    VALUES (
        uid1,
        'User1 Post ' || i,
        '<img>' || 'blog-img-' || img_counter || '.png' || '</img>',
        'AI generated content for User1 Post ' || i,
        'password1234',
        NOW(), NOW()
    )
    RETURNING post_id INTO pid;

    INSERT INTO images (
        post_id, img_path, img_file_name,
        geo_lat, geo_long, img_dtm, rgst_dtm, thumb_yn
    )
    VALUES (
        pid,
        'posts/' || 'blog-img-' || img_counter || '.png',
        'blog-img-' || img_counter || '.png',
        (37.50 + i * 0.004)::TEXT,
        (126.90 + i * 0.004)::TEXT,
        NOW(), NOW(), 'Y'
    );

    img_counter := img_counter + 1;
END LOOP;


-- =============================================
-- USER 2 — 20 POSTS + IMAGES
-- =============================================
FOR i IN 21..40 LOOP
    INSERT INTO posts (user_id, title, og_text, ai_gen_text, password, rgst_dtm, chng_dtm)
    VALUES (
        uid2,
        'User2 Post ' || i,
        '<img>' || 'blog-img-' || img_counter || '.png' || '</img>',
        'AI generated content for User2 Post ' || i,
        'password1234',
        NOW(), NOW()
    )
    RETURNING post_id INTO pid;

    INSERT INTO images (
        post_id, img_path, img_file_name,
        geo_lat, geo_long, img_dtm, rgst_dtm, thumb_yn
    )
    VALUES (
        pid,
        'posts/' || 'blog-img-' || img_counter || '.png',
        'blog-img-' || img_counter || '.png',
        (49.25 + (i-21) * 0.004)::TEXT,
        (-123.15 + (i-21) * 0.004)::TEXT,
        NOW(), NOW(), 'Y'
    );

    img_counter := img_counter + 1;
END LOOP;

END $$;
