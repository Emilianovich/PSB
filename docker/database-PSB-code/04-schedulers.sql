CREATE EVENT ev_pending_tutor_blacklist_check
ON SCHEDULE EVERY 1 HOUR
DO
UPDATE pending_tutors
SET
    blacklisted_at = NULL,
    approved = NULL
WHERE approved = FALSE
  AND blacklisted_at IS NOT NULL
  AND blacklisted_at <= NOW();