-- ============================================================
--  SGCD — Script SQL 
--  Hashes BCrypt valides pour le mot de passe : sgcd1234
--  FST Fès — Licence GI — 2025-2026
-- ============================================================

DROP DATABASE IF EXISTS sgcd;
CREATE DATABASE sgcd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sgcd;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- TABLES
-- ============================================================

CREATE TABLE utilisateur (
    idUtilisateur BIGINT       NOT NULL AUTO_INCREMENT,
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    login         VARCHAR(150) NOT NULL,
    motDePasse    VARCHAR(255) NOT NULL,
    role          ENUM('ADMINISTRATEUR','DENTISTE','ASSISTANTE') NOT NULL,
    statut        ENUM('ACTIF','INACTIF') NOT NULL DEFAULT 'ACTIF',
    CONSTRAINT pk_utilisateur       PRIMARY KEY (idUtilisateur),
    CONSTRAINT uq_utilisateur_email UNIQUE (email),
    CONSTRAINT uq_utilisateur_login UNIQUE (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE administrateur (
    idUtilisateur BIGINT NOT NULL,
    niveauAcces   INT    NOT NULL DEFAULT 1,
    CONSTRAINT pk_administrateur    PRIMARY KEY (idUtilisateur),
    CONSTRAINT fk_admin_utilisateur FOREIGN KEY (idUtilisateur)
        REFERENCES utilisateur(idUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE assistante (
    idUtilisateur BIGINT      NOT NULL,
    poste         VARCHAR(100),
    CONSTRAINT pk_assistante             PRIMARY KEY (idUtilisateur),
    CONSTRAINT fk_assistante_utilisateur FOREIGN KEY (idUtilisateur)
        REFERENCES utilisateur(idUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dentiste (
    idUtilisateur BIGINT      NOT NULL,
    specialite    VARCHAR(150),
    CONSTRAINT pk_dentiste             PRIMARY KEY (idUtilisateur),
    CONSTRAINT fk_dentiste_utilisateur FOREIGN KEY (idUtilisateur)
        REFERENCES utilisateur(idUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dentiste_jours_disponibles (
    idUtilisateur BIGINT      NOT NULL,
    jour          VARCHAR(20) NOT NULL,
    CONSTRAINT pk_dent_jours          PRIMARY KEY (idUtilisateur, jour),
    CONSTRAINT fk_dent_jours_dentiste FOREIGN KEY (idUtilisateur)
        REFERENCES dentiste(idUtilisateur) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE patient (
    idPatient     BIGINT        NOT NULL AUTO_INCREMENT,
    nom           VARCHAR(100)  NOT NULL,
    prenom        VARCHAR(100)  NOT NULL,
    dateNaissance DATE          NOT NULL,
    sexe          ENUM('H','F') NOT NULL,
    adresse       VARCHAR(255)  NOT NULL,
    telephone     VARCHAR(20)   NOT NULL,
    numeroCNSS    VARCHAR(50),
    antecedents   TEXT,
    allergie      VARCHAR(255),
    CONSTRAINT pk_patient PRIMARY KEY (idPatient)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE responsable_legal (
    idPatient   BIGINT       NOT NULL,
    nom         VARCHAR(100) NOT NULL,
    telephone   VARCHAR(20)  NOT NULL,
    lienParente VARCHAR(100) NOT NULL,
    CONSTRAINT pk_responsable_legal         PRIMARY KEY (idPatient),
    CONSTRAINT fk_responsable_legal_patient FOREIGN KEY (idPatient)
        REFERENCES patient(idPatient) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE dossier_medical (
    idDossier    BIGINT      NOT NULL AUTO_INCREMENT,
    numeroRef    VARCHAR(50) NOT NULL,
    dateCreation DATE        NOT NULL,
    idPatient    BIGINT      NOT NULL,
    CONSTRAINT pk_dossier_medical   PRIMARY KEY (idDossier),
    CONSTRAINT uq_dossier_numeroRef UNIQUE (numeroRef),
    CONSTRAINT uq_dossier_patient   UNIQUE (idPatient),
    CONSTRAINT fk_dossier_patient   FOREIGN KEY (idPatient)
        REFERENCES patient(idPatient) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE rendez_vous (
    idRDV        BIGINT   NOT NULL AUTO_INCREMENT,
    dateHeure    DATETIME NOT NULL,
    motif        ENUM('CONTROLE','URGENCE','DETARTRAGE','SOIN','CHIRURGIE','PROTHESE') NOT NULL,
    statut       ENUM('PLANIFIE','EN_SALLE_ATTENTE','EN_COURS','TERMINE','ANNULE','NON_HONORE')
                          NOT NULL DEFAULT 'PLANIFIE',
    notes        TEXT,
    duree        INT      NOT NULL DEFAULT 30,
    idPatient    BIGINT   NOT NULL,
    idDentiste   BIGINT   NOT NULL,
    idAssistante BIGINT,
    CONSTRAINT pk_rendez_vous    PRIMARY KEY (idRDV),
    CONSTRAINT fk_rdv_patient    FOREIGN KEY (idPatient)   REFERENCES patient(idPatient)        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_rdv_dentiste   FOREIGN KEY (idDentiste)  REFERENCES dentiste(idUtilisateur)   ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_rdv_assistante FOREIGN KEY (idAssistante)REFERENCES assistante(idUtilisateur) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE consultation (
    idConsultation BIGINT NOT NULL AUTO_INCREMENT,
    date           DATE   NOT NULL,
    diagnostic     TEXT   NOT NULL,
    observations   TEXT,
    idRDV          BIGINT,
    idDossier      BIGINT NOT NULL,
    idDentiste     BIGINT NOT NULL,
    CONSTRAINT pk_consultation          PRIMARY KEY (idConsultation),
    CONSTRAINT uq_consultation_rdv      UNIQUE (idRDV),
    CONSTRAINT fk_consultation_rdv      FOREIGN KEY (idRDV)      REFERENCES rendez_vous(idRDV)          ON DELETE SET NULL  ON UPDATE CASCADE,
    CONSTRAINT fk_consultation_dossier  FOREIGN KEY (idDossier)  REFERENCES dossier_medical(idDossier)  ON DELETE RESTRICT  ON UPDATE CASCADE,
    CONSTRAINT fk_consultation_dentiste FOREIGN KEY (idDentiste) REFERENCES dentiste(idUtilisateur)     ON DELETE RESTRICT  ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE acte (
    code      VARCHAR(10)   NOT NULL,
    nom       VARCHAR(150)  NOT NULL,
    tarifBase DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_acte PRIMARY KEY (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE consultation_acte (
    idConsultation BIGINT      NOT NULL,
    codeActe       VARCHAR(10) NOT NULL,
    CONSTRAINT pk_consultation_acte     PRIMARY KEY (idConsultation, codeActe),
    CONSTRAINT fk_consacte_consultation FOREIGN KEY (idConsultation) REFERENCES consultation(idConsultation) ON DELETE CASCADE  ON UPDATE CASCADE,
    CONSTRAINT fk_consacte_acte         FOREIGN KEY (codeActe)       REFERENCES acte(code)                  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE document (
    idDocument      BIGINT       NOT NULL AUTO_INCREMENT,
    type            ENUM('RADIOGRAPHIE','PHOTOGRAPHIE','COMPTE_RENDU') NOT NULL,
    dateImportation DATE         NOT NULL,
    cheminAcces     VARCHAR(500) NOT NULL,
    idConsultation  BIGINT       NOT NULL,
    CONSTRAINT pk_document              PRIMARY KEY (idDocument),
    CONSTRAINT fk_document_consultation FOREIGN KEY (idConsultation) REFERENCES consultation(idConsultation) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE prescription (
    idPrescription BIGINT NOT NULL AUTO_INCREMENT,
    date           DATE   NOT NULL,
    instructions   TEXT,
    idConsultation BIGINT NOT NULL,
    CONSTRAINT pk_prescription              PRIMARY KEY (idPrescription),
    CONSTRAINT uq_prescription_consultation UNIQUE (idConsultation),
    CONSTRAINT fk_prescription_consultation FOREIGN KEY (idConsultation) REFERENCES consultation(idConsultation) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE medicament (
    idMedicament    BIGINT       NOT NULL AUTO_INCREMENT,
    nom             VARCHAR(150) NOT NULL,
    dosage          VARCHAR(100) NOT NULL,
    dureeTraitement INT          NOT NULL,
    idPrescription  BIGINT       NOT NULL,
    CONSTRAINT pk_medicament           PRIMARY KEY (idMedicament),
    CONSTRAINT fk_medicament_prescript FOREIGN KEY (idPrescription) REFERENCES prescription(idPrescription) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE facture (
    idFacture      BIGINT        NOT NULL AUTO_INCREMENT,
    date           DATE          NOT NULL,
    montantTotal   DECIMAL(10,2) NOT NULL,
    statut         ENUM('EN_ATTENTE','PAYEE','ANNULEE') NOT NULL DEFAULT 'EN_ATTENTE',
    emailEnvoye    BOOLEAN       NOT NULL DEFAULT FALSE,
    idConsultation BIGINT        NOT NULL,
    CONSTRAINT pk_facture              PRIMARY KEY (idFacture),
    CONSTRAINT uq_facture_consultation UNIQUE (idConsultation),
    CONSTRAINT fk_facture_consultation FOREIGN KEY (idConsultation) REFERENCES consultation(idConsultation) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE paiement (
    idPaiement   BIGINT        NOT NULL AUTO_INCREMENT,
    montant      DECIMAL(10,2) NOT NULL,
    modePaiement ENUM('ESPECES','CARTE_BANCAIRE','CHEQUE') NOT NULL,
    datePaiement DATE          NOT NULL,
    idFacture    BIGINT        NOT NULL,
    CONSTRAINT pk_paiement     PRIMARY KEY (idPaiement),
    CONSTRAINT uq_paiement_fac UNIQUE (idFacture),
    CONSTRAINT fk_paiement_fac FOREIGN KEY (idFacture) REFERENCES facture(idFacture) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- DONNÉES — UTILISATEURS
-- ============================================================
-- ⚠️  Hashes BCrypt RÉELS et VÉRIFIÉS pour le mot de passe : sgcd1234
-- ============================================================

INSERT INTO utilisateur (idUtilisateur, nom, prenom, email, login, motDePasse, role, statut) VALUES
(1, 'FASSI',    'Hassan',  'h.fassi@sgcd.ma',    'h.fassi@sgcd.ma',    '$2a$10$0NVcOywcYmrICVsSNmvFM.YA1apM0MrnPRpYbSitm/9XbExN4dUDW', 'ADMINISTRATEUR', 'ACTIF'),
(2, 'MANSOURI', 'Rachid',  'r.mansouri@sgcd.ma', 'r.mansouri@sgcd.ma', '$2a$10$J0wApRdG6ecyyGSNJU3gHeULn16hj4HwosJm1cdGcBn/x0pJYR9jy', 'DENTISTE',       'ACTIF'),
(3, 'TAZI',     'Leila',   'l.tazi@sgcd.ma',     'l.tazi@sgcd.ma',     '$2a$10$M62hT1Oo.UXNZiYXBs4OVeOfh7z8t08OU50OtQcb7M6OCd81FA0Ke', 'DENTISTE',       'ACTIF'),
(4, 'BENNANI',  'Ahmed',   'a.bennani@sgcd.ma',  'a.bennani@sgcd.ma',  '$2a$10$X6jrCUB29cGdFD7ubKrI1eIMxkMIOz4t8UWY8VvJ8ujQ7Wy7FgFiq', 'DENTISTE',       'ACTIF'),
(5, 'ALAOUI',   'Samira',  's.alaoui@sgcd.ma',   's.alaoui@sgcd.ma',   '$2a$10$1QINgRssgQQaaboXnoPSFOooKA0Cg4giI.1ND0qGVn03XJFD34oQ6', 'ASSISTANTE',     'ACTIF'),
(6, 'KADIRI',   'Nora',    'n.kadiri@sgcd.ma',   'n.kadiri@sgcd.ma',   '$2a$10$VwN2shntjt4Gl0xT8C7ImuXjL9df4Ve52vJkxVSRRwHaAmRE3TYam', 'ASSISTANTE',     'ACTIF'),
(7, 'MOUHIB',   'Khalid',  'k.mouhib@sgcd.ma',   'k.mouhib@sgcd.ma',   '$2a$10$xijZtHUqe4e3AkOHpgSay.8pDRYHuOR8pcKxAzr.JKM/Uclp1tqAK', 'DENTISTE',       'INACTIF');

INSERT INTO administrateur (idUtilisateur, niveauAcces) VALUES (1, 1);

INSERT INTO dentiste (idUtilisateur, specialite) VALUES
(2, 'Chirurgien Dentiste'),
(3, 'Chirurgien Dentiste'),
(4, 'Chirurgien Dentiste'),
(7, 'Chirurgien Dentiste');

INSERT INTO assistante (idUtilisateur, poste) VALUES
(5, 'Assistante Dentaire'),
(6, 'Assistante Dentaire');

INSERT INTO dentiste_jours_disponibles (idUtilisateur, jour) VALUES
(2,'LUNDI'),(2,'MARDI'),(2,'MERCREDI'),(2,'JEUDI'),(2,'VENDREDI'),(2,'SAMEDI'),
(3,'LUNDI'),(3,'MARDI'),(3,'MERCREDI'),(3,'JEUDI'),(3,'VENDREDI'),(3,'SAMEDI'),
(4,'LUNDI'),(4,'MARDI'),(4,'MERCREDI'),(4,'JEUDI'),(4,'VENDREDI'),(4,'SAMEDI');

-- ============================================================
-- PATIENTS
-- ============================================================
INSERT INTO patient (idPatient, nom, prenom, dateNaissance, sexe, adresse, telephone, numeroCNSS, antecedents, allergie) VALUES
(1, 'BENALI',   'Amina',   '1990-03-15', 'F', '12 Rue Hassan II, Casablanca',     '0661234567', '1234567890', 'Diabète type 2', 'Pénicilline'),
(2, 'EL FASSI', 'Youssef', '1985-07-22', 'H', '34 Boulevard Zerktouni, Rabat',    '0672345678', NULL, NULL, NULL),
(3, 'TAZI',     'Fatima',  '1978-11-30', 'F', '8 Avenue des FAR, Casablanca',     '0653456789', NULL, NULL, NULL),
(4, 'ALAMI',    'Karim',   '2000-01-10', 'H', '56 Rue Ibn Batouta, Fès',          '0644567890', NULL, NULL, NULL),
(5, 'IDRISSI',  'Sara',    '1995-06-18', 'F', '22 Rue Moulay Youssef, Marrakech', '0635678901', NULL, NULL, NULL),
(6, 'BERRADA',  'Omar',    '2018-09-05', 'H', '3 Impasse Al Amal, Casablanca',    '0626789012', NULL, NULL, NULL),
(7, 'CHAOUI',   'Laila',   '1982-04-25', 'F', '17 Rue Tarik Ibn Ziad, Tanger',    '0617890123', NULL, NULL, NULL),
(8, 'MANSOURI', 'Rachid',  '1970-12-01', 'H', '45 Avenue Mohammed V, Casablanca', '0668901234', NULL, 'Hypertension', NULL);

INSERT INTO responsable_legal (idPatient, nom, telephone, lienParente) VALUES
(6, 'BERRADA Karim', '0626789013', 'Père');

-- ============================================================
-- DOSSIERS MÉDICAUX
-- ============================================================
INSERT INTO dossier_medical (idDossier, numeroRef, dateCreation, idPatient) VALUES
(1, 'DOS-2024-001', '2024-01-15', 1),
(2, 'DOS-2024-002', '2024-02-20', 2),
(3, 'DOS-2024-003', '2024-03-10', 3),
(4, 'DOS-2024-004', '2024-04-05', 4),
(5, 'DOS-2024-005', '2024-05-12', 5),
(6, 'DOS-2024-006', '2024-06-18', 6),
(7, 'DOS-2024-007', '2024-07-22', 7),
(8, 'DOS-2024-008', '2024-08-30', 8);

-- ============================================================
-- CATALOGUE DES ACTES
-- ============================================================
INSERT INTO acte (code, nom, tarifBase) VALUES
('C001', 'Consultation',                             200.00),
('D001', 'Détartrage',                               400.00),
('S001', 'Obturation composite (1 face)',             500.00),
('S002', 'Obturation composite (2 faces)',            700.00),
('E001', 'Extraction simple',                        600.00),
('E002', 'Extraction chirurgicale',                 1200.00),
('R001', 'Traitement radiculaire (monoradiculée)',  1000.00),
('R002', 'Traitement radiculaire (pluriradiculée)', 1500.00),
('P001', 'Couronne céramique',                      2500.00),
('P002', 'Prothèse amovible partielle',             3000.00),
('X001', 'Radio panoramique',                        300.00),
('X002', 'Radio rétroalvéolaire',                    150.00);

-- ============================================================
-- RENDEZ-VOUS (datés à aujourd'hui pour l'agenda)
-- ============================================================
INSERT INTO rendez_vous (idRDV, dateHeure, motif, statut, notes, duree, idPatient, idDentiste, idAssistante) VALUES
(1,  DATE_FORMAT(NOW(), '%Y-%m-%d 09:00:00'), 'CONTROLE',   'TERMINE',          NULL, 30, 1, 2, 5),
(2,  DATE_FORMAT(NOW(), '%Y-%m-%d 09:30:00'), 'DETARTRAGE', 'EN_COURS',         NULL, 45, 2, 3, 5),
(3,  DATE_FORMAT(NOW(), '%Y-%m-%d 10:00:00'), 'SOIN',       'EN_SALLE_ATTENTE', NULL, 45, 3, 2, 5),
(4,  DATE_FORMAT(NOW(), '%Y-%m-%d 10:30:00'), 'URGENCE',    'PLANIFIE',         NULL, 30, 5, 4, 5),
(5,  DATE_FORMAT(NOW(), '%Y-%m-%d 11:00:00'), 'CHIRURGIE',  'PLANIFIE',         NULL, 60, 4, 2, 5),
(6,  DATE_FORMAT(NOW(), '%Y-%m-%d 11:30:00'), 'PROTHESE',   'PLANIFIE',         NULL, 60, 7, 3, 5),
(7,  DATE_FORMAT(NOW(), '%Y-%m-%d 14:00:00'), 'CONTROLE',   'PLANIFIE',         NULL, 30, 8, 4, 5),
(8,  DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 15 DAY), '%Y-%m-%d 10:00:00'), 'SOIN',      'TERMINE', NULL, 45, 1, 2, 5),
(9,  DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 39 DAY), '%Y-%m-%d 09:00:00'), 'DETARTRAGE','TERMINE', NULL, 45, 1, 3, 5),
(10, DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 79 DAY), '%Y-%m-%d 11:00:00'), 'CONTROLE',  'TERMINE', NULL, 30, 1, 2, 5);

-- ============================================================
-- CONSULTATIONS (historique patient 1 — BENALI Amina)
-- ============================================================
INSERT INTO consultation (idConsultation, date, diagnostic, observations, idRDV, idDossier, idDentiste) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 79 DAY), 'Contrôle annuel - RAS',               'Hygiène buccale satisfaisante.',     10, 1, 2),
(2, DATE_SUB(CURDATE(), INTERVAL 39 DAY), 'Gingivite chronique légère',           'Prescription détartrage semestriel.',9,  1, 3),
(3, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 'Carie profonde sur 36 — obturation',  'Patient sous anesthésie locale.',    8,  1, 2),
(4, CURDATE(),                             'Contrôle post-traitement',             'Cicatrisation satisfaisante.',       1,  1, 2);

-- ============================================================
-- ACTES PAR CONSULTATION
-- ============================================================
INSERT INTO consultation_acte (idConsultation, codeActe) VALUES
(1,'C001'),(1,'X001'),
(2,'C001'),(2,'D001'),
(3,'C001'),(3,'X002'),(3,'S001'),
(4,'C001');

-- ============================================================
-- PRESCRIPTION
-- ============================================================
INSERT INTO prescription (idPrescription, date, instructions, idConsultation) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 15 DAY),
   'Prendre les médicaments après les repas. Éviter les aliments durs pendant 48h.', 3);

INSERT INTO medicament (idMedicament, nom, dosage, dureeTraitement, idPrescription) VALUES
(1, 'Amoxicilline',   '1g — 3x/jour',           7, 1),
(2, 'Ibuprofène',     '400mg — 3x/jour',         5, 1),
(3, 'Chlorhexidine',  'Bain de bouche 2x/jour',  7, 1);

-- ============================================================
-- FACTURES ET PAIEMENTS
-- ============================================================
INSERT INTO facture (idFacture, date, montantTotal, statut, emailEnvoye, idConsultation) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 79 DAY), 500.00, 'PAYEE',      TRUE,  1),
(2, DATE_SUB(CURDATE(), INTERVAL 39 DAY), 600.00, 'PAYEE',      FALSE, 2),
(3, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 850.00, 'PAYEE',      TRUE,  3),
(4, CURDATE(),                             200.00, 'EN_ATTENTE', FALSE, 4);

INSERT INTO paiement (idPaiement, montant, modePaiement, datePaiement, idFacture) VALUES
(1, 500.00, 'ESPECES',        DATE_SUB(CURDATE(), INTERVAL 79 DAY), 1),
(2, 600.00, 'CARTE_BANCAIRE', DATE_SUB(CURDATE(), INTERVAL 39 DAY), 2),
(3, 850.00, 'CHEQUE',         DATE_SUB(CURDATE(), INTERVAL 15 DAY), 3);

-- ============================================================
-- VÉRIFICATION FINALE
-- ============================================================
SELECT 'utilisateur'       AS table_name, COUNT(*) nb FROM utilisateur    UNION ALL
SELECT 'patient',                         COUNT(*)    FROM patient         UNION ALL
SELECT 'dossier_medical',                 COUNT(*)    FROM dossier_medical UNION ALL
SELECT 'acte',                            COUNT(*)    FROM acte            UNION ALL
SELECT 'rendez_vous',                     COUNT(*)    FROM rendez_vous     UNION ALL
SELECT 'consultation',                    COUNT(*)    FROM consultation    UNION ALL
SELECT 'facture',                         COUNT(*)    FROM facture;

-- Afficher les comptes avec leur prefix hash pour confirmer
SELECT CONCAT(prenom,' ',nom) AS utilisateur, login, role,
       LEFT(motDePasse,10) AS hash_debut,
       '→ Mot de passe: sgcd1234' AS info
FROM utilisateur
WHERE statut = 'ACTIF'
ORDER BY idUtilisateur;

SELECT '✅ SGCD prêt — Connectez-vous avec sgcd1234' AS message;
