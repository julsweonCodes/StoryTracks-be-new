-- V3__likes_update.sql
-- Purpose: Add constraints & indexes for like/unlike functionality

BEGIN;

-- 1) Ensure no duplicate likes exist before adding UNIQUE constraint
DELETE FROM likes a
USING likes b
WHERE a.id < b.id
  AND a.post_id = b.post_id
  AND a.user_id = b.user_id;

-- 2) Add UNIQUE constraint on (post_id, user_id)
ALTER TABLE likes
    ADD CONSTRAINT uq_likes_post_user UNIQUE (post_id, user_id);

-- 3) Add index for faster lookups (existsByPostIdAndUserId)
CREATE INDEX idx_likes_post_user
    ON likes(post_id, user_id);

-- 4) Optional: rename like_at → liked_at for clarity
ALTER TABLE likes
    RENAME COLUMN like_at TO liked_at;

COMMIT;
