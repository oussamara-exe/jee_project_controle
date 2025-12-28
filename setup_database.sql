-- Script de configuration rapide de la base de données
-- Application de Gestion RH

-- ===============================================
-- 1. Création de la base de données
-- ===============================================
DROP DATABASE IF EXISTS gestion_rh;
CREATE DATABASE gestion_rh 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- ===============================================
-- 2. Création de l'utilisateur
-- ===============================================
DROP USER IF EXISTS 'rh_user'@'localhost';
CREATE USER 'rh_user'@'localhost' IDENTIFIED BY 'rh_password_2024';

-- ===============================================
-- 3. Attribution des privilèges
-- ===============================================
GRANT ALL PRIVILEGES ON gestion_rh.* TO 'rh_user'@'localhost';
FLUSH PRIVILEGES;

-- ===============================================
-- 4. Vérification
-- ===============================================
SELECT 'Base de données créée avec succès!' as Message;
SHOW DATABASES LIKE 'gestion_rh';
SELECT user, host FROM mysql.user WHERE user = 'rh_user';

-- ===============================================
-- PROCHAINES ÉTAPES
-- ===============================================
-- Exécuter le script d'initialisation :
-- mysql -u rh_user -p gestion_rh < src/main/resources/scripts/mysql-init.sql
-- 
-- Mot de passe : rh_password_2024
-- ===============================================

