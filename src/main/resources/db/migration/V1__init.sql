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
    og_text TEXT,
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
    img_file_name VARCHAR(512) NOT NULL,
    geo_lat VARCHAR(20),
    geo_long VARCHAR(20),
    img_dtm TIMESTAMPTZ,
    rgst_dtm TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    thumb_yn VARCHAR(1) DEFAULT 'N',
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);

-- LIKES
CREATE TABLE likes (
    id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    like_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- COMMENTS
CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    parent_id INT,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE image_clusters (
    cluster_id SERIAL PRIMARY KEY,
    cluster_level INT NOT NULL,                     -- 1 (city), 2 (province), 3 (country)
    cluster_lat DOUBLE PRECISION NOT NULL,
    cluster_long DOUBLE PRECISION NOT NULL,
    image_count INT NOT NULL,
    thumb_img_path VARCHAR(512),
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_image_clusters_level
ON image_clusters(cluster_level);
