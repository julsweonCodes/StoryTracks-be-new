-- Create 'post' table
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,              -- PK
    title VARCHAR(100) NOT NULL,             -- 제목
    og_text VARCHAR(100),                    -- 원문 텍스트
    ai_gen_text TEXT,                        -- AI 생성 텍스트
    password VARCHAR(100),                   -- 비밀번호 (옵션)
    rgst_dtm TIMESTAMP DEFAULT NOW(),        -- 등록일
    chng_dtm TIMESTAMP                       -- 수정일
);

-- Create 'image' table
CREATE TABLE images (
    img_id SERIAL PRIMARY KEY,                    -- Auto-incrementing PK
    post_id INT NOT NULL,                         -- FK to post
    img_path VARCHAR(512) NOT NULL,               -- Path/URL to image
    geo_lat VARCHAR(20),                          -- Latitude stored as string
    geo_long VARCHAR(20),                         -- Longitude stored as string
    img_dtm TIMESTAMP,                            -- Datetime from image metadata
    rgst_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Record created time
    thumb_yn VARCHAR(1) DEFAULT 'N',               -- Thumbnail flag
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);

CREATE INDEX idx_images_post_id ON images(post_id);