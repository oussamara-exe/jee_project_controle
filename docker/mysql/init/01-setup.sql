-- Création de la base et de l'utilisateur pour Docker (connexion depuis n'importe quel host)
CREATE DATABASE IF NOT EXISTS gestion_rh CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'rh_user'@'%' IDENTIFIED BY 'rh_password_2024';
GRANT ALL PRIVILEGES ON gestion_rh.* TO 'rh_user'@'%';
FLUSH PRIVILEGES;
