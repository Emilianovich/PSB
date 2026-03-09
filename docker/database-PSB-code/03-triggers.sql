DELIMITER $$

CREATE TRIGGER t_activate_session
AFTER INSERT ON session_assignment
FOR EACH ROW
BEGIN
    UPDATE sessions
    SET status = 'ACTIVE'
    WHERE id = NEW.session_id;
END$$

CREATE TRIGGER t_set_default_slots
BEFORE INSERT ON sessions
FOR EACH ROW
BEGIN
    SET NEW.available_slots = NEW.expected_students;
END$$


CREATE TRIGGER t_validate_inscription
BEFORE INSERT ON inscriptions
FOR EACH ROW
BEGIN

    DECLARE v_status VARCHAR(10);
    DECLARE v_slots INT;
    DECLARE v_end TIMESTAMP;

    SELECT status, available_slots, end_datetime
    INTO v_status, v_slots, v_end
    FROM sessions
    WHERE id = NEW.session_id;

    IF EXISTS (
        SELECT 1
        FROM inscriptions
        WHERE student_id = NEW.student_id
        AND session_id = NEW.session_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Student already registered in this session';
    END IF;

    IF v_status <> 'ACTIVE' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Session is not active';
    END IF;

    IF v_slots <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No available slots';
    END IF;

    IF NOW() >= v_end THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Session already finished';
    END IF;

    SET NEW.date_time = NOW();

END$$


CREATE TRIGGER t_update_slots_after_insert
AFTER INSERT ON inscriptions
FOR EACH ROW
BEGIN

    DECLARE v_total INT;
    DECLARE v_max INT;

    SELECT COUNT(*)
    INTO v_total
    FROM inscriptions
    WHERE session_id = NEW.session_id;

    SELECT expected_students
    INTO v_max
    FROM sessions
    WHERE id = NEW.session_id;

    UPDATE sessions
    SET available_slots = v_max - v_total
    WHERE id = NEW.session_id;

END$$


CREATE TRIGGER t_update_slots_after_delete
AFTER DELETE ON inscriptions
FOR EACH ROW
BEGIN

    UPDATE sessions
    SET available_slots = available_slots + 1
    WHERE id = OLD.session_id;

END$$


CREATE TRIGGER t_validate_review
BEFORE INSERT ON reviews
FOR EACH ROW
BEGIN

    DECLARE v_end TIMESTAMP;
    DECLARE v_exists INT;

    SELECT end_datetime
    INTO v_end
    FROM sessions
    WHERE id = NEW.session_id;

    SELECT COUNT(*)
    INTO v_exists
    FROM inscriptions
    WHERE student_id = NEW.student_id
    AND session_id = NEW.session_id;

    IF v_exists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Student cannot review a session they did not attend';
    END IF;

    IF NOW() < v_end THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Session has not finished yet';
    END IF;

    SET NEW.date_time = NOW();

END$$


CREATE TRIGGER t_update_tutor_score
AFTER INSERT ON reviews
FOR EACH ROW
BEGIN

    DECLARE v_avg DECIMAL(3,2);

    SELECT AVG(rating)
    INTO v_avg
    FROM reviews
    WHERE tutor_id = NEW.tutor_id;

    UPDATE tutors
    SET score = v_avg
    WHERE id = NEW.tutor_id;

END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER t_pending_tutor_blacklist
BEFORE UPDATE ON pending_tutors
FOR EACH ROW
BEGIN
    IF NEW.approved = FALSE AND OLD.approved IS NULL THEN
        SET NEW.blacklisted_at = NOW() + INTERVAL 30 DAY;
    END IF;
END$$

DELIMITER ;