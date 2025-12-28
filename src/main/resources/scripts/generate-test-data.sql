-- Script de génération de données de test complètes
-- Application de Gestion RH Avancée
-- Ce script ajoute des données réalistes pour démontrer toutes les fonctionnalités

USE gestion_rh;

-- Suppression des données de test existantes (pour permettre la réexécution)
-- Note: Utiliser TRUNCATE pour réinitialiser les auto-increment, ou DELETE pour conserver l'historique
DELETE FROM actions_historique;
DELETE FROM notifications;
DELETE FROM fiches_paie;
DELETE FROM feuilles_temps;
DELETE FROM conges;
DELETE FROM soldes_conge;

-- ============================================
-- SOLDES DE CONGÉS POUR L'ANNÉE 2024
-- ============================================
INSERT INTO soldes_conge (id, employe_id, annee, jours_acquis, jours_pris, jours_restants) VALUES
(1, 1, 2024, 26, 5, 21),
(2, 2, 2024, 26, 10, 16),
(3, 3, 2024, 26, 8, 18),
(4, 4, 2024, 26, 3, 23),
(5, 5, 2024, 26, 0, 26);

-- ============================================
-- DEMANDES DE CONGÉS
-- ============================================
INSERT INTO conges (id, employe_id, type_conge, date_debut, date_fin, nombre_jours, statut, commentaire_employe, date_demande, version) VALUES
-- Congés approuvés
(1, 2, 'ANNUEL', '2024-06-15', '2024-06-20', 6, 'APPROUVE', 'Vacances d''été', '2024-05-20 10:00:00', 0),
(2, 3, 'ANNUEL', '2024-07-01', '2024-07-05', 5, 'APPROUVE', 'Vacances estivales', '2024-06-15 14:30:00', 0),
(3, 4, 'ANNUEL', '2024-08-10', '2024-08-12', 3, 'APPROUVE', 'Week-end prolongé', '2024-07-25 09:15:00', 0),

-- Congés validés par manager, en attente RH
(4, 4, 'ANNUEL', '2024-12-23', '2024-12-27', 5, 'VALIDE_MANAGER', 'Congés de fin d''année', '2024-11-15 11:00:00', 0),
(5, 5, 'ANNUEL', '2024-12-20', '2024-12-31', 12, 'VALIDE_MANAGER', 'Vacances de Noël', '2024-11-10 16:20:00', 0),

-- Congés en attente de validation manager
(6, 4, 'MALADIE', '2024-12-18', '2024-12-18', 1, 'EN_ATTENTE', 'Arrêt maladie', '2024-12-17 08:30:00', 0),
(7, 5, 'ANNUEL', '2025-01-05', '2025-01-10', 6, 'EN_ATTENTE', 'Reprise après les fêtes', '2024-12-15 10:00:00', 0),

-- Congés refusés
(8, 3, 'ANNUEL', '2024-11-20', '2024-11-30', 11, 'REFUSE', 'Demande refusée pour charge de travail', '2024-10-25 15:00:00', 0);

-- Mise à jour des validateurs pour les congés validés
UPDATE conges SET validateur_manager_id = 3, date_validation_manager = '2024-05-25 10:00:00' WHERE id = 1;
UPDATE conges SET validateur_manager_id = 1, date_validation_manager = '2024-06-20 14:00:00' WHERE id = 2;
UPDATE conges SET validateur_manager_id = 3, date_validation_manager = '2024-08-01 09:00:00' WHERE id = 3;
UPDATE conges SET validateur_manager_id = 3, date_validation_manager = '2024-11-20 11:30:00' WHERE id = 4;
UPDATE conges SET validateur_manager_id = 3, date_validation_manager = '2024-11-15 16:45:00' WHERE id = 5;
UPDATE conges SET validateur_manager_id = 1, date_validation_manager = '2024-10-30 16:00:00', commentaire_manager = 'Refusé: période de charge importante' WHERE id = 8;

UPDATE conges SET validateur_rh_id = 2, date_validation_rh = '2024-05-26 11:00:00' WHERE id = 1;
UPDATE conges SET validateur_rh_id = 2, date_validation_rh = '2024-06-21 15:00:00' WHERE id = 2;
UPDATE conges SET validateur_rh_id = 2, date_validation_rh = '2024-08-02 10:00:00' WHERE id = 3;

-- ============================================
-- FEUILLES DE TEMPS (Dernières semaines)
-- ============================================
INSERT INTO feuilles_temps (id, employe_id, date_semaine, heures_normales, heures_supplementaires, statut, date_saisie, version) VALUES
-- Feuilles validées
(1, 4, '2024-12-02', 35, 5, 'VALIDE', '2024-12-03 09:00:00', 0),
(2, 5, '2024-12-02', 35, 0, 'VALIDE', '2024-12-03 09:30:00', 0),
(3, 4, '2024-12-09', 35, 8, 'VALIDE', '2024-12-10 09:00:00', 0),
(4, 5, '2024-12-09', 35, 3, 'VALIDE', '2024-12-10 09:30:00', 0),

-- Feuilles soumises, en attente de validation
(5, 4, '2024-12-16', 35, 6, 'SOUMIS', '2024-12-17 08:45:00', 0),
(6, 5, '2024-12-16', 35, 2, 'SOUMIS', '2024-12-17 09:00:00', 0),

-- Feuille en brouillon
(7, 4, '2024-12-23', 30, 0, 'BROUILLON', '2024-12-23 17:00:00', 0);

-- Mise à jour des validateurs pour les feuilles validées
UPDATE feuilles_temps SET validateur_id = 3, date_validation = '2024-12-04 10:00:00', commentaire = 'Validé' WHERE id = 1;
UPDATE feuilles_temps SET validateur_id = 3, date_validation = '2024-12-04 10:15:00', commentaire = 'Validé' WHERE id = 2;
UPDATE feuilles_temps SET validateur_id = 3, date_validation = '2024-12-11 10:00:00', commentaire = 'Validé - Bon travail sur le projet' WHERE id = 3;
UPDATE feuilles_temps SET validateur_id = 3, date_validation = '2024-12-11 10:15:00', commentaire = 'Validé' WHERE id = 4;

-- ============================================
-- FICHES DE PAIE (Derniers mois)
-- ============================================
INSERT INTO fiches_paie (id, employe_id, mois, annee, salaire_base, heures_supplementaires, montant_heures_sup, primes, indemnites, prime_anciennete, total_brut, cotisations_sociales, retenue_ir, autres_deductions, total_deductions, net_a_payer, statut, date_generation, version) VALUES
-- Fiches payées
(1, 2, 11, 2024, 70000.00, 0, 0.00, 2000.00, 500.00, 1500.00, 74000.00, 14800.00, 7400.00, 0.00, 22200.00, 51800.00, 'PAYE', '2024-11-25 10:00:00', 0),
(2, 3, 11, 2024, 65000.00, 10, 8125.00, 1500.00, 500.00, 1300.00, 76425.00, 15285.00, 7642.50, 0.00, 22927.50, 53497.50, 'PAYE', '2024-11-25 10:00:00', 0),
(3, 4, 11, 2024, 50000.00, 15, 9375.00, 1000.00, 500.00, 1000.00, 61875.00, 12375.00, 6187.50, 0.00, 18562.50, 43312.50, 'PAYE', '2024-11-25 10:00:00', 0),
(4, 5, 11, 2024, 30000.00, 5, 1875.00, 500.00, 300.00, 600.00, 33275.00, 6655.00, 3327.50, 0.00, 9982.50, 23292.50, 'PAYE', '2024-11-25 10:00:00', 0),

-- Fiches validées, non encore payées
(5, 2, 12, 2024, 70000.00, 0, 0.00, 2000.00, 500.00, 1500.00, 74000.00, 14800.00, 7400.00, 0.00, 22200.00, 51800.00, 'VALIDE', '2024-12-25 10:00:00', 0),
(6, 3, 12, 2024, 65000.00, 12, 9750.00, 1500.00, 500.00, 1300.00, 78050.00, 15610.00, 7805.00, 0.00, 23415.00, 54635.00, 'VALIDE', '2024-12-25 10:00:00', 0),
(7, 4, 12, 2024, 50000.00, 18, 11250.00, 1000.00, 500.00, 1000.00, 63750.00, 12750.00, 6375.00, 0.00, 19125.00, 44625.00, 'VALIDE', '2024-12-25 10:00:00', 0),
(8, 5, 12, 2024, 30000.00, 5, 1875.00, 500.00, 300.00, 600.00, 33275.00, 6655.00, 3327.50, 0.00, 9982.50, 23292.50, 'VALIDE', '2024-12-25 10:00:00', 0),

-- Fiche calculée, en attente de validation
(9, 1, 12, 2024, 100000.00, 0, 0.00, 5000.00, 1000.00, 2000.00, 108000.00, 21600.00, 10800.00, 0.00, 32400.00, 75600.00, 'CALCULE', '2024-12-25 10:00:00', 0);

-- Mise à jour des validateurs et dates de paiement
UPDATE fiches_paie SET validateur_id = 2, date_validation = '2024-11-26 14:00:00', date_paiement = '2024-11-30 00:00:00' WHERE id IN (1, 2, 3, 4);
UPDATE fiches_paie SET validateur_id = 2, date_validation = '2024-12-26 14:00:00' WHERE id IN (5, 6, 7, 8);

-- ============================================
-- NOTIFICATIONS
-- ============================================
INSERT INTO notifications (id, destinataire_id, type, message, lu, date_creation, entite_type, entite_id) VALUES
-- Notifications non lues
(1, 3, 'CONGE_A_VALIDER', 'Nouvelle demande de congé de Mohammed TAZI (Maladie - 1 jour)', FALSE, '2024-12-17 08:30:00', 'Conge', 6),
(2, 3, 'CONGE_A_VALIDER', 'Nouvelle demande de congé de Amina SERRAJ (Annuel - 6 jours)', FALSE, '2024-12-15 10:00:00', 'Conge', 7),
(3, 2, 'CONGE_A_VALIDER', 'Demande de congé validée par manager nécessitant validation RH', FALSE, '2024-11-20 11:30:00', 'Conge', 4),
(4, 2, 'CONGE_A_VALIDER', 'Demande de congé validée par manager nécessitant validation RH', FALSE, '2024-11-15 16:45:00', 'Conge', 5),
(5, 3, 'TEMPS_A_VALIDER', 'Nouvelle feuille de temps soumise par Mohammed TAZI', FALSE, '2024-12-17 08:45:00', 'FeuilleTemps', 5),
(6, 3, 'TEMPS_A_VALIDER', 'Nouvelle feuille de temps soumise par Amina SERRAJ', FALSE, '2024-12-17 09:00:00', 'FeuilleTemps', 6),

-- Notifications lues
(7, 4, 'CONGE_APPROUVE', 'Votre demande de congé a été approuvée', TRUE, '2024-08-02 10:00:00', 'Conge', 3),
(8, 4, 'CONGE_APPROUVE', 'Votre demande de congé a été approuvée', TRUE, '2024-05-26 11:00:00', 'Conge', 1),
(9, 3, 'CONGE_APPROUVE', 'Votre demande de congé a été approuvée', TRUE, '2024-06-21 15:00:00', 'Conge', 2),
(10, 4, 'PAIE_GENEREE', 'Votre fiche de paie de Novembre 2024 est disponible', TRUE, '2024-11-25 10:00:00', 'FichePaie', 3);

UPDATE notifications SET date_lecture = '2024-08-02 15:00:00' WHERE id = 7;
UPDATE notifications SET date_lecture = '2024-05-26 16:00:00' WHERE id = 8;
UPDATE notifications SET date_lecture = '2024-06-21 17:00:00' WHERE id = 9;
UPDATE notifications SET date_lecture = '2024-11-26 09:00:00' WHERE id = 10;

-- ============================================
-- ACTIONS HISTORIQUE (Exemples)
-- ============================================
INSERT INTO actions_historique (id, utilisateur_id, type_action, entite_type, entite_id, description, date_action, adresse_ip) VALUES
(1, 4, 'CREATE', 'Conge', 6, 'Création d''une demande de congé (Maladie)', '2024-12-17 08:30:00', '192.168.1.100'),
(2, 3, 'UPDATE', 'Conge', 4, 'Validation de la demande de congé par le manager', '2024-11-20 11:30:00', '192.168.1.50'),
(3, 2, 'UPDATE', 'Conge', 1, 'Validation finale de la demande de congé par RH', '2024-05-26 11:00:00', '192.168.1.20'),
(4, 4, 'CREATE', 'FeuilleTemps', 5, 'Soumission d''une feuille de temps', '2024-12-17 08:45:00', '192.168.1.100'),
(5, 3, 'UPDATE', 'FeuilleTemps', 1, 'Validation d''une feuille de temps', '2024-12-04 10:00:00', '192.168.1.50'),
(6, 2, 'CREATE', 'FichePaie', 5, 'Génération d''une fiche de paie', '2024-12-25 10:00:00', '192.168.1.20'),
(7, 2, 'UPDATE', 'FichePaie', 5, 'Validation d''une fiche de paie', '2024-12-26 14:00:00', '192.168.1.20');

