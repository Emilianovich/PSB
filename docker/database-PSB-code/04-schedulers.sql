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


DELIMITER $$

CREATE EVENT ev_update_inactive_sessions
ON SCHEDULE EVERY 2 HOUR
STARTS CURRENT_DATE + INTERVAL 1 DAY
DO
BEGIN
    UPDATE sessions
    SET status = 'NOT_ACTIVE'
    WHERE status = 'ACTIVE'
      AND end_datetime < NOW();
END $$

DELIMITER ;