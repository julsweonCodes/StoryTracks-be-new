-- USERS
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) UNIQUE NOT NULL,
    pwd TEXT NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    birth_ymd CHAR(8),
    nickname VARCHAR(100) NOT NULL,
    profile_img TEXT,
    blog_name VARCHAR(255) NOT NULL,
    bio TEXT,
    rgst_dtm TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    chng_dtm TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    last_login_dtm TIMESTAMPTZ
);

-- FOLLOWERS
CREATE TABLE followers (
    follow_id INT NOT NULL,
    followed_by_id INT NOT NULL,
    followed_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follow_id, followed_by_id),
    FOREIGN KEY (follow_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_by_id) REFERENCES users(id) ON DELETE CASCADE
);

-- POSTS
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    og_text VARCHAR(100),
    ai_gen_text TEXT,
    password VARCHAR(100),
    rgst_dtm TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    chng_dtm TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- IMAGES
CREATE TABLE images (
    img_id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    img_path VARCHAR(512) NOT NULL,
    geo_lat VARCHAR(20),
    geo_long VARCHAR(20),
    img_dtm TIMESTAMPTZ,
    rgst_dtm TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    thumb_yn VARCHAR(1) DEFAULT 'N',
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);
