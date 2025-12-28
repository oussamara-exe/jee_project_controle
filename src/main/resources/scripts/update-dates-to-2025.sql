-- Script pour mettre à jour toutes les dates vers 2025
-- Mise à jour des dates de congés (ajouter 1 an)
UPDATE conges 
SET date_debut = DATE_ADD(date_debut, INTERVAL 1 YEAR),
    date_fin = DATE_ADD(date_fin, INTERVAL 1 YEAR),
    date_demande = DATE_ADD(date_demande, INTERVAL 1 YEAR),
    date_validation_manager = CASE 
        WHEN date_validation_manager IS NOT NULL THEN DATE_ADD(date_validation_manager, INTERVAL 1 YEAR)
        ELSE NULL
    END,
    date_validation_rh = CASE 
        WHEN date_validation_rh IS NOT NULL THEN DATE_ADD(date_validation_rh, INTERVAL 1 YEAR)
        ELSE NULL
    END
WHERE YEAR(date_debut) = 2024;

-- Mise à jour des dates de feuilles de temps (ajouter 1 an)
UPDATE feuilles_temps 
SET date_semaine = DATE_ADD(date_semaine, INTERVAL 1 YEAR),
    date_saisie = DATE_ADD(date_saisie, INTERVAL 1 YEAR),
    date_validation = CASE 
        WHEN date_validation IS NOT NULL THEN DATE_ADD(date_validation, INTERVAL 1 YEAR)
        ELSE NULL
    END
WHERE YEAR(date_semaine) = 2024;

-- Mise à jour des fiches de paie (année 2025)
UPDATE fiches_paie 
SET annee = 2025,
    date_generation = DATE_ADD(date_generation, INTERVAL 1 YEAR),
    date_validation = CASE 
        WHEN date_validation IS NOT NULL THEN DATE_ADD(date_validation, INTERVAL 1 YEAR)
        ELSE NULL
    END,
    date_paiement = CASE 
        WHEN date_paiement IS NOT NULL THEN DATE_ADD(date_paiement, INTERVAL 1 YEAR)
        ELSE NULL
    END
WHERE annee = 2024;

-- Mise à jour des soldes de congés pour 2025
INSERT INTO solde_conges (employe_id, annee, jours_acquis, jours_pris, jours_restants)
SELECT employe_id, 2025, jours_acquis, jours_pris, jours_restants
FROM solde_conges
WHERE annee = 2024
ON DUPLICATE KEY UPDATE 
    jours_acquis = VALUES(jours_acquis),
    jours_pris = VALUES(jours_pris),
    jours_restants = VALUES(jours_restants);

-- Mise à jour des dates de création des notifications (ajouter 1 an)
UPDATE notifications 
SET date_creation = DATE_ADD(date_creation, INTERVAL 1 YEAR),
    date_lecture = CASE 
        WHEN date_lecture IS NOT NULL THEN DATE_ADD(date_lecture, INTERVAL 1 YEAR)
        ELSE NULL
    END
WHERE YEAR(date_creation) = 2024;

-- Mise à jour des dates d'actions historiques (ajouter 1 an)
UPDATE actions_historique 
SET date_action = DATE_ADD(date_action, INTERVAL 1 YEAR)
WHERE YEAR(date_action) = 2024;

-- Vérification
SELECT 'Congés mis à jour' as status, COUNT(*) as total FROM conges WHERE YEAR(date_debut) = 2025;
SELECT 'Feuilles de temps mises à jour' as status, COUNT(*) as total FROM feuilles_temps WHERE YEAR(date_semaine) = 2025;
SELECT 'Fiches de paie mises à jour' as status, COUNT(*) as total FROM fiches_paie WHERE annee = 2025;

