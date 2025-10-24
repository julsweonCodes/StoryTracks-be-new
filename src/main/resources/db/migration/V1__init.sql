-- Create 'post' table
CREATE TABLE posts (
    post_id SERIAL PRIMARY KEY,                   -- Auto-incrementing PK
    title VARCHAR(150) NOT NULL,                  -- Slightly longer title field
    og_text TEXT,                                 -- Original text (BLOB → TEXT)
    ai_gen_text TEXT,                             -- AI-generated text
    password VARCHAR(128),                        -- Store hashed passwords (64 is too short for bcrypt)
    rgst_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Registration datetime
    chng_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Last changed datetime
);

-- Create 'image' table
CREATE TABLE images (
    img_id SERIAL PRIMARY KEY,                    -- Auto-incrementing PK
    post_id INT NOT NULL,                         -- FK to post
    img_path VARCHAR(512) NOT NULL,               -- Path/URL to image
    geo_lat DECIMAL(10, 7),                       -- More precise than VARCHAR
    geo_long DECIMAL(10, 7),
    img_dtm TIMESTAMP,                            -- Datetime from image metadata
    rgst_dtm TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- When record was added
    thumb_yn BOOLEAN DEFAULT FALSE,               -- Store as boolean instead of VARCHAR(1)
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE
);
