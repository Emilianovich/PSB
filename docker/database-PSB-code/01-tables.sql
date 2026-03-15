SET NAMES utf8mb4;
CREATE TABLE students (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    fullname VARCHAR(255) NOT NULL,
    social_id VARCHAR(15) NOT NULL
        CHECK (social_id REGEXP '^(?:[1-9]|1[0-3]|E|N|PE)-[0-9]{1,4}-[0-9]{1,6}$'),
    email VARCHAR(255) NOT NULL UNIQUE
        CHECK (
        email REGEXP '^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$'
        AND email NOT REGEXP '@ptystudybuddy\\.dev$'
    ),
    password VARCHAR(255) NOT NULL,
    picture TEXT NOT NULL,
    role CHAR(7) NOT NULL DEFAULT 'STUDENT'
        CHECK (role = 'STUDENT')
);


CREATE TABLE tutors (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    social_id VARCHAR(15) NOT NULL UNIQUE
        CHECK (social_id REGEXP '^(?:[1-9]|1[0-3]|E|N|PE)-[0-9]{1,4}-[0-9]{1,6}$'),
    fullname VARCHAR(255) NOT NULL,
    cv TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
        CHECK (
        email REGEXP '^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$'
        AND email NOT REGEXP '@ptystudybuddy\\.dev$'
    ),
    password VARCHAR(255) NOT NULL,
    picture TEXT,
    score DECIMAL(3,2) DEFAULT 0.00 CHECK (score >= 0 AND score <= 5),
    role CHAR(5) NOT NULL DEFAULT 'TUTOR'
        CHECK (role = 'TUTOR')
);


CREATE TABLE pending_tutors (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    social_id VARCHAR(15) UNIQUE
        CHECK (social_id REGEXP '^(?:[1-9]|1[0-3]|E|N|PE)-[0-9]{1,4}-[0-9]{1,6}$'),
    fullname VARCHAR(255) NOT NULL,
    picture TEXT NOT NULL,
    cv TEXT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
        CHECK (
        email REGEXP '^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$'
        AND email NOT REGEXP '@ptystudybuddy\\.dev$'
    ),
    password VARCHAR(255) NOT NULL,
    approved BOOLEAN DEFAULT NULL,
    blacklisted_at TIMESTAMP DEFAULT NULL
);


CREATE TABLE classrooms (
    id VARCHAR(5) PRIMARY KEY
        CHECK (id REGEXP '^[1-4]-[0-9]{3}$'),
    location VARCHAR(100) NOT NULL
);


CREATE TABLE schedules (
    id CHAR(4) PRIMARY KEY,
    start_time TIME NOT NULL UNIQUE,
    end_time TIME NOT NULL UNIQUE,
    CHECK (end_time > start_time),
    CHECK (
        TIMESTAMPDIFF(HOUR, start_time, end_time) BETWEEN 1 AND 3
        )
);


CREATE TABLE availability (
    id VARCHAR(25) PRIMARY KEY,
    class_id VARCHAR(5) NOT NULL,
    schedule_id CHAR(4) NOT NULL,
    date DATE NOT NULL,
    selected BOOLEAN DEFAULT FALSE,
    end_datetime TIMESTAMP NOT NULL,
    UNIQUE (class_id, schedule_id, date),
    FOREIGN KEY (class_id) REFERENCES classrooms(id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);


CREATE TABLE sessions (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    expected_students INT NOT NULL CHECK (expected_students > 0),
    available_slots INT,
    attendance_marked BOOLEAN DEFAULT FALSE,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE','CANCELLED','NOT_ACTIVE')),
    availability_id VARCHAR(25) NOT NULL,
    end_datetime TIMESTAMP NOT NULL,

    FOREIGN KEY (availability_id) REFERENCES availability(id),

    CHECK (available_slots >= 0 AND available_slots <= expected_students)
);


CREATE TABLE subjects (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(75) NOT NULL,
    description VARCHAR(125) NOT NULL
);


CREATE TABLE session_assignment (
    subject_id CHAR(36) NOT NULL,
    tutor_id CHAR(36) NOT NULL,
    session_id CHAR(36) NOT NULL,
    PRIMARY KEY (subject_id, tutor_id, session_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(id),
    FOREIGN KEY (tutor_id) REFERENCES tutors(id),
    FOREIGN KEY (session_id) REFERENCES sessions(id)
);


CREATE TABLE reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comment TEXT,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    date_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    student_id CHAR(36) NOT NULL,
    tutor_id CHAR(36) NOT NULL,
    session_id CHAR(36) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (tutor_id) REFERENCES tutors(id),
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    UNIQUE (student_id, session_id)
);


CREATE TABLE refresh_tokens (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry TIMESTAMP NOT NULL,
    user_id CHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(8) NOT NULL
        CHECK (role IN ('ADMIN','STUDENT','TUTOR'))
);


CREATE TABLE admins (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    social_id VARCHAR(15) NOT NULL
        CHECK (social_id REGEXP '^(?:[1-9]|1[0-3]|E|N|PE)-[0-9]{1,4}-[0-9]{1,6}$'),
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
    CHECK(email REGEXP '^[a-zA-Z0-9._%+\\-]+@ptystudybuddy\\.dev$'),
    password VARCHAR(255) NOT NULL,
    role CHAR(5) NOT NULL DEFAULT 'ADMIN'
        CHECK (role = 'ADMIN')
);


CREATE TABLE inscriptions (
    student_id CHAR(36) NOT NULL,
    session_id CHAR(36) NOT NULL,
    assisted BOOLEAN DEFAULT NULL,
    evaluation_status BOOLEAN DEFAULT FALSE,
    date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id, session_id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (session_id) REFERENCES sessions(id)
);

INSERT INTO schedules (id, start_time, end_time) VALUES
                                                     ('S001', '07:00:00', '09:00:00'),
                                                     ('S002', '09:05:00', '11:05:00'),
                                                     ('S003', '11:10:00', '13:10:00'),
                                                     ('S004', '13:15:00', '15:15:00'),
                                                     ('S005', '15:20:00', '17:20:00'),
                                                     ('S006', '17:25:00', '19:25:00'),
                                                     ('S007', '19:30:00', '21:30:00');

INSERT INTO admins (social_id, fullname, email, password) VALUES
                                                              ('8-1234-56789', 'Steven Ampie', 'steven.ampie@ptystudybuddy.dev', '$2a$12$rAruzY1SvgKlAUrAovZcPu0cRQnfcvfh1wWkbZa4PBP84FQgj2UZ.'),
                                                              ('8-1022-970', 'Emiola Fagbemi', 'emiola.fagbemi@ptystudybuddy.dev', '$2a$12$HYv7j3MSq2C76PVD0IjgE.gFjBp9Q7bji.4KybKDLp8ns1vVwYsZO');

INSERT INTO subjects (name, description) VALUES
                                             ('Cálculo I', 'Introducción a límites, derivadas e integrales de funciones de una variable con aplicaciones en ciencias e ingeniería.'),
                                             ('Cálculo II', 'Estudio de técnicas de integración, series numéricas, sucesiones y coordenadas polares para funciones de una variable.'),
                                             ('Cálculo III', 'Extensión del cálculo a funciones de varias variables, integrales múltiples, vectores y teoremas de Green y Stokes.'),
                                             ('Física I', 'Principios de mecánica clásica, cinemática, dinámica, trabajo, energía y leyes de Newton aplicadas a sistemas físicos.'),
                                             ('Física II', 'Estudio del electromagnetismo, ondas, óptica y termodinámica con aplicaciones en ingeniería y tecnología moderna.'),
                                             ('Inglés', 'Desarrollo de habilidades de comprensión y expresión en inglés técnico orientado a ciencias e ingeniería profesional.');


