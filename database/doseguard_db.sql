-- ══════════════════════════════
--  DOSEGUARD DATABASE — SCHEMA
-- ══════════════════════════════

DROP SCHEMA IF EXISTS doseguarddb;
CREATE SCHEMA doseguarddb;
USE doseguarddb;

-- ══════════════════════════════
--  TABLES
-- ══════════════════════════════

CREATE TABLE doseguarddb.user (
    id INT AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    fiscal_code VARCHAR(16) UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('PATIENT', 'DOCTOR', 'PHARMACIST') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.doctor_detail (
    user_id INT NOT NULL,
    specialization VARCHAR(150) NOT NULL,
    medical_license_number VARCHAR(50) NOT NULL UNIQUE,
    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.prescription (
    id INT AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    drug VARCHAR(150) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    issue_date DATE NOT NULL,
    status ENUM('PENDING', 'DISPENSED', 'CANCELLED') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (doctor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (patient_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.time_slot (
    id INT AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id),
    FOREIGN KEY (doctor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.appointment (
    id INT AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    slot_id INT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (doctor_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (slot_id)
        REFERENCES time_slot(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.therapy_schedule (
    id INT AUTO_INCREMENT,
    prescription_id INT NOT NULL,
    patient_id INT NOT NULL,
    scheduled_time DATETIME NOT NULL,
    taken BOOLEAN DEFAULT FALSE,
    notes VARCHAR(500),
    PRIMARY KEY (id),
    FOREIGN KEY (prescription_id)
        REFERENCES prescription(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (patient_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════
--  INDEXES
-- ══════════════════════════════

CREATE INDEX idx_user_email ON doseguarddb.user (email);
CREATE INDEX idx_user_fiscal_code ON doseguarddb.user (fiscal_code);
CREATE INDEX idx_prescription_patient ON doseguarddb.prescription (patient_id);
CREATE INDEX idx_prescription_doctor ON doseguarddb.prescription (doctor_id);
CREATE INDEX idx_timeslot_doctor ON doseguarddb.time_slot (doctor_id, date);
CREATE INDEX idx_appointment_patient ON doseguarddb.appointment (patient_id);

-- ══════════════════════════════
--  STORED PROCEDURE
-- ══════════════════════════════

DELIMITER $$

DROP PROCEDURE IF EXISTS doseguarddb.login$$
CREATE PROCEDURE doseguarddb.login(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(100),
    OUT p_id INT,
    OUT p_name VARCHAR(100),
    OUT p_surname VARCHAR(100),
    OUT p_role VARCHAR(20)
)
BEGIN
    SELECT id, name, surname, role
    INTO p_id, p_name, p_surname, p_role
    FROM user
    WHERE email = p_email
      AND password = p_password;

    IF p_role IS NULL THEN
        SET p_role = 'NOT_FOUND';
    END IF;
END$$

DELIMITER ;

-- ══════════════════════════════
--  USERS MYSQL & PERMISSIONS
-- ══════════════════════════════

DROP USER IF EXISTS 'dg_login'@'localhost';
CREATE USER 'dg_login'@'localhost' IDENTIFIED BY 'dg_login';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_login'@'localhost';
GRANT SELECT ON doseguarddb.user TO 'dg_login'@'localhost';

DROP USER IF EXISTS 'dg_patient'@'localhost';
CREATE USER 'dg_patient'@'localhost' IDENTIFIED BY 'dg_patient';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_patient'@'localhost';
GRANT SELECT ON doseguarddb.user TO 'dg_patient'@'localhost';
GRANT SELECT ON doseguarddb.doctor_detail TO 'dg_patient'@'localhost';
GRANT SELECT ON doseguarddb.prescription TO 'dg_patient'@'localhost';
GRANT SELECT ON doseguarddb.time_slot TO 'dg_patient'@'localhost';
GRANT SELECT, INSERT, UPDATE ON doseguarddb.appointment TO 'dg_patient'@'localhost';
GRANT SELECT, UPDATE ON doseguarddb.therapy_schedule TO 'dg_patient'@'localhost';

DROP USER IF EXISTS 'dg_doctor'@'localhost';
CREATE USER 'dg_doctor'@'localhost' IDENTIFIED BY 'dg_doctor';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_doctor'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON doseguarddb.prescription TO 'dg_doctor'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON doseguarddb.time_slot TO 'dg_doctor'@'localhost';
GRANT SELECT, UPDATE ON doseguarddb.appointment TO 'dg_doctor'@'localhost';
GRANT SELECT, INSERT, UPDATE ON doseguarddb.therapy_schedule TO 'dg_doctor'@'localhost';
GRANT SELECT ON doseguarddb.user TO 'dg_doctor'@'localhost';
GRANT SELECT, INSERT, UPDATE ON doseguarddb.doctor_detail TO 'dg_doctor'@'localhost';

DROP USER IF EXISTS 'dg_pharmacist'@'localhost';
CREATE USER 'dg_pharmacist'@'localhost' IDENTIFIED BY 'dg_pharmacist';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_pharmacist'@'localhost';
GRANT SELECT, UPDATE ON doseguarddb.prescription TO 'dg_pharmacist'@'localhost';
GRANT SELECT ON doseguarddb.user TO 'dg_pharmacist'@'localhost';

FLUSH PRIVILEGES;

-- ══════════════════════════════
--  DOSEGUARD — TEST DATA
--  password: 'password123'
-- ══════════════════════════════
USE doseguarddb;

-- ── Users ────────────────────────────────────────────────────

INSERT INTO user (id, name, surname, fiscal_code, email, password, role) VALUES
-- Patients
(1, 'Mario',     'Rossi',     'RSSMRA80A01H501U', 'mario.rossi@test.com',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT'),
(2, 'Laura',     'Bianchi',   'BNCLRA85M42F205Z', 'laura.bianchi@test.com',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT'),
(3, 'Giuseppe',  'Verdi',     'VVRGSP90B10L219X', 'giuseppe.verdi@test.com',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT'),

-- Doctors
(4, 'Andrea',    'Neri',      'NRNDR75C15H501Y', 'dr.neri@test.com',          'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'DOCTOR'),
(5, 'Francesca', 'Ferrari',   'FRRFRN78D50F205W', 'dr.ferrari@test.com',       'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'DOCTOR'),

-- Pharmacists
(6, 'Stefano',   'Russo',     'RSSSFN82E20L219K', 'farmacia.russo@test.com',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PHARMACIST');

-- ── Doctor Details ──────────────────────────────────────────

INSERT INTO doctor_detail (user_id, specialization, medical_license_number) VALUES
(4, 'Medicina Generale', 'MI-123456'),
(5, 'Cardiologia',        'RM-654321');

-- ── Prescriptions ───────────────────────────────────────────

INSERT INTO prescription (id, doctor_id, patient_id, drug, dosage, frequency, issue_date, status) VALUES
(1, 4, 1, 'Tachipirina', '1000 mg', '1 compressa ogni 8 ore', '2026-06-01', 'PENDING'),
(2, 4, 1, 'Augmentin',   '1 gr',    '1 compressa ogni 12 ore', '2026-05-20', 'DISPENSED'),
(3, 5, 2, 'Brufen',       '600 mg',  '1 bustina al bisogno',    '2026-06-05', 'PENDING');

-- ── Time Slots (Inclusi slot specifici per Dr. Andrea Neri - doctor_id: 4) ──

INSERT INTO time_slot (id, doctor_id, date, start_time, available) VALUES
-- Slot Dr. Andrea Neri (ID 4)
(1,  4, '2026-09-01', '09:00:00', FALSE),
(2,  4, '2026-09-01', '09:30:00', FALSE),
(3,  4, '2026-09-01', '10:00:00', TRUE),
(4,  4, '2026-09-01', '10:30:00', TRUE),
(5,  4, '2026-09-02', '15:00:00', FALSE),
(6,  4, '2026-09-02', '15:30:00', TRUE),
(7,  4, '2026-09-02', '16:00:00', FALSE),
(8,  4, '2026-09-03', '11:00:00', TRUE),
(9,  4, '2026-09-03', '11:30:00', TRUE),

-- Slot Dr.ssa Francesca Ferrari (ID 5)
(10, 5, '2026-09-01', '14:00:00', TRUE),
(11, 5, '2026-09-01', '14:30:00', FALSE);

-- ── Appointments ────────────────────────────────────────────

INSERT INTO appointment (id, patient_id, doctor_id, slot_id, status) VALUES
-- Appuntamenti con il Dr. Andrea Neri (doctor_id: 4)
(1, 1, 4, 1, 'CONFIRMED'),  -- Mario Rossi
(2, 2, 4, 2, 'CONFIRMED'),  -- Laura Bianchi
(3, 3, 4, 5, 'PENDING'),    -- Giuseppe Verdi
(4, 1, 4, 7, 'CANCELLED'),  -- Mario Rossi (cancellato)

-- Appuntamento con la Dr.ssa Francesca Ferrari (doctor_id: 5)
(5, 2, 5, 11, 'CONFIRMED'); -- Laura Bianchi

-- ── Therapy Schedules ───────────────────────────────────────

INSERT INTO therapy_schedule (prescription_id, patient_id, scheduled_time, taken, notes) VALUES
(1, 1, '2026-06-01 08:00:00', TRUE,  'Assunta regolarmente a colazione'),
(1, 1, '2026-06-01 16:00:00', TRUE,  'Assunta nel pomeriggio'),
(1, 1, '2026-06-01 23:00:00', FALSE, 'Dimenticata');