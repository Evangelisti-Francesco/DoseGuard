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
    dob DATE NOT NULL,
    gender ENUM('Uomo', 'Donna', 'Altro') NOT NULL,
    country VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('PATIENT', 'CAREGIVER') NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.medication (
    id INT AUTO_INCREMENT,
    caregiver_id INT NOT NULL,
    barcode VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    dosage VARCHAR(50) NOT NULL,
    total_pills INT NOT NULL,
    remaining_pills INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('AVAILABLE', 'DISCONTINUED') DEFAULT 'AVAILABLE',
    PRIMARY KEY (id),
    FOREIGN KEY (caregiver_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE doseguarddb.intake_schedule (
    id INT AUTO_INCREMENT,
    patient_id INT NOT NULL,
    medication_id INT NOT NULL,
    intake_time DATETIME NOT NULL,
    dosage_prescribed VARCHAR(50) NOT NULL,
    notes VARCHAR(255) DEFAULT NULL,
    status ENUM('TO_TAKE', 'TAKEN', 'SKIPPED') DEFAULT 'TO_TAKE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (patient_id)
        REFERENCES user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (medication_id)
        REFERENCES medication(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ══════════════════════════════
--  INDEXES
-- ══════════════════════════════

CREATE INDEX idx_user_email ON doseguarddb.user (email);
CREATE INDEX idx_medication_barcode ON doseguarddb.medication (barcode);
CREATE INDEX idx_intake_patient ON doseguarddb.intake_schedule (patient_id);
CREATE INDEX idx_intake_medication ON doseguarddb.intake_schedule (medication_id);

-- ══════════════════════════════
--  STORED PROCEDURES
-- ══════════════════════════════

DELIMITER $$

DROP PROCEDURE IF EXISTS doseguarddb.login$$
CREATE PROCEDURE doseguarddb.login(
    IN p_email VARCHAR(100),
    IN p_password VARCHAR(255),
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

DROP PROCEDURE IF EXISTS doseguarddb.confirm_intake$$
CREATE PROCEDURE doseguarddb.confirm_intake(
    IN  p_intake_id INT,
    IN  p_medication_id INT,
    OUT p_success BOOLEAN
)
BEGIN
    -- Decrementa la giacenza delle pillole se disponibili
    UPDATE medication
    SET remaining_pills = remaining_pills - 1
    WHERE id = p_medication_id
      AND remaining_pills > 0;

    IF (ROW_COUNT() > 0) THEN
        UPDATE intake_schedule
        SET status = 'TAKEN'
        WHERE id = p_intake_id;

        SET p_success = TRUE;
    ELSE
        SET p_success = FALSE;
    END IF;
END$$

DELIMITER ;

-- ══════════════════════════════
--  USERS MYSQL
-- ══════════════════════════════

DROP USER IF EXISTS 'dg_login'@'localhost';
CREATE USER 'dg_login'@'localhost' IDENTIFIED BY 'dg_login';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_login'@'localhost';
GRANT SELECT, INSERT ON doseguarddb.user TO 'dg_login'@'localhost';

DROP USER IF EXISTS 'dg_paziente'@'localhost';
CREATE USER 'dg_paziente'@'localhost' IDENTIFIED BY 'dg_paziente';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_paziente'@'localhost';
GRANT EXECUTE ON PROCEDURE doseguarddb.confirm_intake TO 'dg_paziente'@'localhost';
GRANT SELECT ON doseguarddb.medication TO 'dg_paziente'@'localhost';
GRANT SELECT ON doseguarddb.user TO 'dg_paziente'@'localhost';
GRANT SELECT, INSERT, UPDATE ON doseguarddb.intake_schedule TO 'dg_paziente'@'localhost';
GRANT UPDATE (email, password) ON doseguarddb.user TO 'dg_paziente'@'localhost';
GRANT UPDATE ON doseguarddb.medication TO 'dg_paziente'@'localhost';

DROP USER IF EXISTS 'dg_caregiver'@'localhost';
CREATE USER 'dg_caregiver'@'localhost' IDENTIFIED BY 'dg_caregiver';
GRANT EXECUTE ON PROCEDURE doseguarddb.login TO 'dg_caregiver'@'localhost';
GRANT SELECT ON doseguarddb.user TO 'dg_caregiver'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON doseguarddb.medication TO 'dg_caregiver'@'localhost';
GRANT SELECT ON doseguarddb.intake_schedule TO 'dg_caregiver'@'localhost';
GRANT SELECT, UPDATE ON doseguarddb.intake_schedule TO 'dg_caregiver'@'localhost';
GRANT UPDATE (email, password) ON doseguarddb.user TO 'dg_caregiver'@'localhost';
FLUSH PRIVILEGES;

-- ══════════════════════════════
--  DOSEGUARD — TEST DATA
-- ══════════════════════════════
USE doseguarddb;

-- ══════════════════════════════
--  USERS
--  password: 'password123' (hash SHA-256 fittizio)
-- ══════════════════════════════

INSERT INTO user (name, surname, dob, gender, country, city, email, password, role) VALUES
-- Pazienti
('Anna',    'Bianchi', '2003-01-13', 'Donna', 'Italia', 'Roma',   'annabianchi@gmail.com',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT'),
('Martina', 'Maurizi', '2000-07-10', 'Donna', 'Italia', 'Roma',   'martinamaurizi30@gmail.com', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT'),
('Marco',   'Rossi',   '1999-05-22', 'Uomo',  'Italia', 'Milano', 'marco.rossi@email.com',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'PATIENT'),
-- Caregiver / Medici
('Luca',    'Messina', '1985-03-14', 'Uomo',  'Italia', 'Roma',   'dr.messina@doseguard.it',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CAREGIVER'),
('Sara',    'Cassino', '1990-11-05', 'Donna', 'Italia', 'Roma',   'dr.cassino@doseguard.it',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CAREGIVER');

-- ══════════════════════════════
--  MEDICATIONS
-- ══════════════════════════════

INSERT INTO medication (caregiver_id, barcode, name, description, dosage, total_pills, remaining_pills, price) VALUES
-- Registrati da Dr. Messina (Caregiver ID 4)
(4, 'A800123456', 'Tachipirina', 'Paracetamolo per stati febbrili', '1000mg', 30, 28, 7.50),
(4, 'A800987654', 'Otopax', 'Gocce auricolari per otite', '5ml', 10, 10, 12.00),
(4, 'A800112233', 'Augmentin', 'Antibiotico ad ampio spettro', '875mg + 125mg', 12, 4, 15.30),
(4, 'A800445566', 'Brufen', 'Antinfiammatorio e analgesico', '600mg', 30, 25, 9.80),
-- Registrati da Dr.ssa Cassino (Caregiver ID 5)
(5, 'A800778899', 'Eutirox', 'Trattamento per tiroide', '50mcg', 50, 48, 4.20),
(5, 'A800990011', 'Xanax', 'Ansiolitico a breve durata', '0.50mg', 20, 18, 8.90);

-- ══════════════════════════════
--  INTAKE SCHEDULES
-- ══════════════════════════════

INSERT INTO intake_schedule (patient_id, medication_id, intake_time, dosage_prescribed, notes, status, created_at) VALUES
-- Anna ha una compressa di Tachipirina da assumere
(1, 1, '2026-08-16 12:00:00', '1 compressa', 'Dopo i pasti', 'TO_TAKE', NOW() - INTERVAL 2 DAY),

-- Martina: Tachipirina assunta con successo
(2, 1, '2026-08-15 08:00:00', '1 compressa', 'A stomaco pieno', 'TAKEN', NOW() - INTERVAL 1 DAY),

-- Martina: Augmentin da prendere stasera
(2, 3, '2026-08-16 20:00:00', '1 compressa', 'Prendere con un bicchiere d acqua', 'TO_TAKE', NOW() - INTERVAL 5 HOUR),

-- Martina: Brufen saltato
(2, 4, '2026-08-14 14:00:00', '1 compressa', 'In caso di dolore forte', 'SKIPPED', NOW() - INTERVAL 2 DAY),

-- Marco: Eutirox da assumere al mattino
(3, 5, '2026-08-17 07:30:00', '1 compressa', 'A digiuno', 'TO_TAKE', NOW() - INTERVAL 12 HOUR);