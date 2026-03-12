CREATE VIEW v_sessions_summary AS
SELECT
    ROW_NUMBER() over () AS id,
    av.class_id as classId,
    av.date as sessionDate,
    s.status as sessionStatus,
    tu.fullname as tutorName,
    tu.id as tutorId,
    tu.picture as tutorPicture,
    tu.score as tutorScore,
    sub.name as subjectName,
    sub.id as subjectId,
    sub.description as subjectDesc,
    av.schedule_id as scheduleId,
    (SELECT COUNT(*) FROM inscriptions ins WHERE ins.session_id = s.id) as amountOfStudents,
    s.expected_students as sessionSlots,
    sch.start_time as sessionStartTime,
    sch.end_time as sessionEndTime
FROM availability av
         INNER JOIN sessions s ON s.availability_id = av.id
         INNER JOIN schedules sch ON av.schedule_id = sch.id
         INNER JOIN session_assignment sa ON sa.session_id = s.id
         INNER JOIN tutors tu ON tu.id = sa.tutor_id
         INNER JOIN subjects sub ON sub.id = sa.subject_id



CREATE VIEW v_students_sessions AS
SELECT
    ROW_NUMBER() over () AS id,
    tu.fullname as tutorName,
    tu.id as tutorId,
    tu.score as tutorScore,
    tu.picture as tutorPicture,
    av.class_id as classId,
    sub.name as subjectName,
    av.date as sessionDate,
    sch.start_time as sessionStartTime,
    sch.end_time as sessionEndTime,
    ins.evaluation_status as hasEvaluated,
    s.status as sessionStatus,
    ins.student_id as studentId,
    ins.assisted as assisted
FROM inscriptions ins
         INNER JOIN sessions s ON ins.session_id = s.id
         INNER JOIN session_assignment sa ON s.id = sa.session_id
         INNER JOIN tutors tu ON sa.tutor_id = tu.id
         INNER JOIN subjects sub ON sa.subject_id = sub.id
         INNER JOIN availability av ON s.availability_id = av.id
         INNER JOIN schedules sch ON av.schedule_id = sch.id