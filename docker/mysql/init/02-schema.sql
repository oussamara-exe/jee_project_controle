-- Schéma des tables (ordre compatible avec les clés étrangères)
USE gestion_rh;

SET time_zone = '+00:00';

-- Table des départements (sans FK responsable au départ)
CREATE TABLE IF NOT EXISTS departements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    responsable_id BIGINT,
    INDEX idx_departement_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS postes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    salaire_min DECIMAL(10,2),
    salaire_max DECIMAL(10,2),
    INDEX idx_poste_titre (titre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    date_creation DATETIME NOT NULL,
    derniere_connexion DATETIME,
    tentatives_connexion_echouees INT DEFAULT 0,
    date_verrouillage DATETIME,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_actif (actif)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS employes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(20) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20),
    adresse TEXT,
    date_embauche DATE NOT NULL,
    salaire_base DECIMAL(10,2),
    heures_hebdo INT DEFAULT 35,
    iban VARCHAR(34),
    photo VARCHAR(255),
    actif BOOLEAN DEFAULT TRUE,
    version BIGINT DEFAULT 0,
    departement_id BIGINT,
    poste_id BIGINT,
    manager_id BIGINT,
    user_account_id BIGINT UNIQUE,
    INDEX idx_matricule (matricule),
    INDEX idx_email (email),
    INDEX idx_actif (actif),
    FOREIGN KEY (departement_id) REFERENCES departements(id),
    FOREIGN KEY (poste_id) REFERENCES postes(id),
    FOREIGN KEY (manager_id) REFERENCES employes(id),
    FOREIGN KEY (user_account_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- FK responsable après création de la table employes
ALTER TABLE departements
ADD CONSTRAINT fk_departement_responsable
FOREIGN KEY (responsable_id) REFERENCES employes(id);

CREATE TABLE IF NOT EXISTS conges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employe_id BIGINT NOT NULL,
    type_conge VARCHAR(20) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    nombre_jours INT NOT NULL,
    statut VARCHAR(20) NOT NULL,
    commentaire_employe TEXT,
    commentaire_manager TEXT,
    commentaire_rh TEXT,
    date_demande DATETIME NOT NULL,
    date_validation_manager DATETIME,
    date_validation_rh DATETIME,
    validateur_manager_id BIGINT,
    validateur_rh_id BIGINT,
    version BIGINT DEFAULT 0,
    INDEX idx_conge_employe (employe_id),
    INDEX idx_conge_statut (statut),
    INDEX idx_conge_dates (date_debut, date_fin),
    FOREIGN KEY (employe_id) REFERENCES employes(id),
    FOREIGN KEY (validateur_manager_id) REFERENCES user_accounts(id),
    FOREIGN KEY (validateur_rh_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS solde_conges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employe_id BIGINT NOT NULL,
    annee INT NOT NULL,
    jours_acquis INT NOT NULL,
    jours_pris INT DEFAULT 0,
    jours_restants INT NOT NULL,
    UNIQUE KEY unique_employe_annee (employe_id, annee),
    FOREIGN KEY (employe_id) REFERENCES employes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS feuilles_temps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employe_id BIGINT NOT NULL,
    date_semaine DATE NOT NULL,
    heures_normales DECIMAL(5,2) DEFAULT 0,
    heures_supplementaires DECIMAL(5,2) DEFAULT 0,
    statut VARCHAR(20) NOT NULL,
    date_saisie DATETIME NOT NULL,
    date_validation DATETIME,
    validateur_id BIGINT,
    version BIGINT DEFAULT 0,
    INDEX idx_temps_employe (employe_id),
    INDEX idx_temps_statut (statut),
    FOREIGN KEY (employe_id) REFERENCES employes(id),
    FOREIGN KEY (validateur_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS fiches_paie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employe_id BIGINT NOT NULL,
    mois INT NOT NULL,
    annee INT NOT NULL,
    salaire_base DECIMAL(10,2) NOT NULL,
    heures_supplementaires DECIMAL(5,2) DEFAULT 0,
    montant_heures_sup DECIMAL(10,2) DEFAULT 0,
    primes DECIMAL(10,2) DEFAULT 0,
    indemnites DECIMAL(10,2) DEFAULT 0,
    total_brut DECIMAL(10,2) NOT NULL,
    cotisations_sociales DECIMAL(10,2) DEFAULT 0,
    retenue_ir DECIMAL(10,2) DEFAULT 0,
    autres_deductions DECIMAL(10,2) DEFAULT 0,
    total_deductions DECIMAL(10,2) DEFAULT 0,
    net_a_payer DECIMAL(10,2) NOT NULL,
    statut VARCHAR(20) NOT NULL,
    date_generation DATETIME NOT NULL,
    date_validation DATETIME,
    date_paiement DATE,
    validateur_id BIGINT,
    version BIGINT DEFAULT 0,
    UNIQUE KEY unique_employe_mois_annee (employe_id, mois, annee),
    FOREIGN KEY (employe_id) REFERENCES employes(id),
    FOREIGN KEY (validateur_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS actions_historique (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utilisateur_id BIGINT NOT NULL,
    type_action VARCHAR(50) NOT NULL,
    entite_type VARCHAR(100),
    entite_id BIGINT,
    description TEXT,
    valeurs_avant TEXT,
    valeurs_apres TEXT,
    date_action DATETIME NOT NULL,
    adresse_ip VARCHAR(45),
    INDEX idx_historique_utilisateur (utilisateur_id),
    INDEX idx_historique_type (type_action),
    INDEX idx_historique_entite (entite_type, entite_id),
    FOREIGN KEY (utilisateur_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destinataire_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    lu BOOLEAN DEFAULT FALSE,
    date_creation DATETIME NOT NULL,
    date_lecture DATETIME,
    lien VARCHAR(255),
    entite_type VARCHAR(100),
    entite_id BIGINT,
    INDEX idx_notif_destinataire (destinataire_id),
    INDEX idx_notif_lu (lu),
    FOREIGN KEY (destinataire_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
