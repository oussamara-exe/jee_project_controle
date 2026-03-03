-- Données de démonstration (mot de passe des comptes : password123)
USE gestion_rh;

INSERT INTO departements (id, nom, description) VALUES
(1, 'Direction Générale', 'Direction et management stratégique'),
(2, 'Ressources Humaines', 'Gestion du personnel et recrutement'),
(3, 'Informatique', 'Développement et infrastructure IT'),
(4, 'Comptabilité', 'Gestion comptable'),
(5, 'Commercial', 'Ventes et relation client');

INSERT INTO postes (id, titre, description, salaire_min, salaire_max) VALUES
(1, 'Directeur Général', 'Direction de l''entreprise', 80000.00, 150000.00),
(2, 'Directeur RH', 'Direction des ressources humaines', 60000.00, 90000.00),
(3, 'Responsable IT', 'Responsable infrastructure et développement', 50000.00, 80000.00),
(4, 'Développeur Senior', 'Développement d''applications', 40000.00, 60000.00),
(5, 'Développeur Junior', 'Développement d''applications', 25000.00, 35000.00),
(6, 'Chef Comptable', 'Gestion comptable', 45000.00, 70000.00),
(7, 'Comptable', 'Comptabilité générale', 30000.00, 45000.00),
(8, 'Responsable Commercial', 'Gestion équipe commerciale', 50000.00, 75000.00),
(9, 'Commercial', 'Vente et relation client', 28000.00, 45000.00),
(10, 'Chargé RH', 'Recrutement et administration RH', 30000.00, 45000.00);

INSERT INTO user_accounts (id, username, password, role, actif, date_creation, tentatives_connexion_echouees) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', TRUE, NOW(), 0),
(2, 'rh.manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'RH', TRUE, NOW(), 0),
(3, 'it.manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER', TRUE, NOW(), 0),
(4, 'dev.senior', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYE', TRUE, NOW(), 0),
(5, 'dev.junior', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYE', TRUE, NOW(), 0);

INSERT INTO employes (id, matricule, nom, prenom, date_naissance, email, telephone, adresse, date_embauche, salaire_base, heures_hebdo, iban, actif, departement_id, poste_id, manager_id, user_account_id, version) VALUES
(1, 'EMP001', 'ADMIN', 'Système', '1980-01-01', 'admin@gestion-rh.ma', '0600000001', '123 Rue Admin, Casablanca', '2020-01-01', 100000.00, 35, 'MA64011519000001205000534921', TRUE, 1, 1, NULL, 1, 0),
(2, 'EMP002', 'ALAMI', 'Fatima', '1985-05-15', 'fatima.alami@gestion-rh.ma', '0600000002', '456 Avenue Mohammed V, Rabat', '2020-02-01', 70000.00, 35, 'MA64011519000001205000534922', TRUE, 2, 2, 1, 2, 0),
(3, 'EMP003', 'BENJELLOUN', 'Karim', '1990-03-20', 'karim.benjelloun@gestion-rh.ma', '0600000003', '789 Boulevard Zerktouni, Casablanca', '2020-03-01', 65000.00, 35, 'MA64011519000001205000534923', TRUE, 3, 3, 1, 3, 0),
(4, 'EMP004', 'TAZI', 'Mohammed', '1992-07-10', 'mohammed.tazi@gestion-rh.ma', '0600000004', '321 Rue de la Liberté, Casablanca', '2020-06-01', 50000.00, 35, 'MA64011519000001205000534924', TRUE, 3, 4, 3, 4, 0),
(5, 'EMP005', 'SERRAJ', 'Amina', '1995-11-25', 'amina.serraj@gestion-rh.ma', '0600000005', '654 Avenue Hassan II, Rabat', '2021-01-15', 30000.00, 35, 'MA64011519000001205000534925', TRUE, 3, 5, 3, 5, 0);

UPDATE departements SET responsable_id = 1 WHERE id = 1;
UPDATE departements SET responsable_id = 2 WHERE id = 2;
UPDATE departements SET responsable_id = 3 WHERE id = 3;

INSERT INTO solde_conges (employe_id, annee, jours_acquis, jours_pris, jours_restants) VALUES
(1, YEAR(CURDATE()), 22, 0, 22),
(2, YEAR(CURDATE()), 22, 0, 22),
(3, YEAR(CURDATE()), 22, 0, 22),
(4, YEAR(CURDATE()), 22, 0, 22),
(5, YEAR(CURDATE()), 22, 0, 22);

INSERT INTO conges (employe_id, type_conge, date_debut, date_fin, nombre_jours, statut, commentaire_employe, date_demande, version) VALUES
(4, 'ANNUEL', DATE_ADD(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 39 DAY), 8, 'EN_ATTENTE', 'Congé annuel prévu', NOW(), 0),
(5, 'ANNUEL', DATE_ADD(CURDATE(), INTERVAL 15 DAY), DATE_ADD(CURDATE(), INTERVAL 19 DAY), 5, 'EN_ATTENTE', 'Vacances d''été', NOW(), 0);

INSERT INTO notifications (destinataire_id, type, message, lu, date_creation, lien) VALUES
(3, 'CONGE_A_VALIDER', 'Vous avez 2 demandes de congés en attente de validation', FALSE, NOW(), '/app/conges/validation'),
(2, 'CONGE_A_VALIDER', 'Des demandes de congés nécessitent votre validation RH', FALSE, NOW(), '/app/rh/conges');

INSERT INTO actions_historique (utilisateur_id, type_action, entite_type, entite_id, description, date_action, adresse_ip) VALUES
(1, 'CONNEXION', 'UserAccount', 1, 'Connexion de l''administrateur système', NOW(), '127.0.0.1'),
(2, 'CONNEXION', 'UserAccount', 2, 'Connexion du responsable RH', NOW(), '127.0.0.1');

COMMIT;
