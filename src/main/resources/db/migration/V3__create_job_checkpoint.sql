-- V3__create_job_checkpoint.sql
-- Purpose: Store scheduler checkpoint timestamps for incremental recomputation jobs.

CREATE TABLE IF NOT EXISTS job_checkpoint (
    job_name        VARCHAR(100) PRIMARY KEY,
    last_success_at TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Automatically update updated_at on row updates
CREATE OR REPLACE FUNCTION set_updated_at_job_checkpoint()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_set_updated_at_job_checkpoint ON job_checkpoint;

CREATE TRIGGER trg_set_updated_at_job_checkpoint
BEFORE UPDATE ON job_checkpoint
FOR EACH ROW
EXECUTE FUNCTION set_updated_at_job_checkpoint();

-- Seed initial checkpoint for image cluster recompute job
INSERT INTO job_checkpoint (job_name, last_success_at)
VALUES ('image_cluster_recompute', NOW() - INTERVAL '1 day')
ON CONFLICT (job_name) DO NOTHING;
