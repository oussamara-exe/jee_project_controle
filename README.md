# Application de Gestion RH Avancée 🏢

Application JEE complète de gestion des ressources humaines avec workflows avancés, calculs automatiques de paie et reporting.

## 👥 Équipe de Développement

- **Amara Oussama**
- **Aben Boutaieb Khalil**

## 📋 Description

Système complet de gestion RH incluant :
- ✅ Gestion des employés et organigramme
- ✅ Workflow de validation des congés (Employé → Manager → RH)
- ✅ Feuilles de temps et calcul automatique des heures supplémentaires
- ✅ Calcul et génération automatique des fiches de paie
- ✅ Historique et traçabilité complète des actions
- ✅ Reporting avancé avec exports PDF/Excel
- ✅ Système de notifications en temps réel
- ✅ Sécurité JAAS avec 4 rôles (EMPLOYE, MANAGER, RH, ADMIN)

## 🛠️ Technologies Utilisées

### Backend
- **Java EE 8** (Jakarta EE)
- **JPA/Hibernate** pour la persistance
- **EJB** pour la logique métier
- **Servlets** pour le contrôle
- **JAAS** pour la sécurité

### Frontend
- **JSP** avec **JSTL**
- **Bootstrap 5** pour le design responsive
- **JavaScript/jQuery** pour l'interactivité

### Base de Données
- **MySQL 8** (ou PostgreSQL en alternative)

### Reporting
- **JasperReports** pour les rapports complexes
- **iText** pour la génération de PDF
- **Apache POI** pour les exports Excel

### Build & Serveur
- **Maven** pour la gestion de projet
- **WildFly** ou **GlassFish** comme serveur d'application

## 📁 Structure du Projet

```
jee_project_controle/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ma/projet/rh/
│   │   │       ├── entities/          # Entités JPA
│   │   │       ├── repositories/      # Couche d'accès aux données
│   │   │       ├── services/          # Services métier (EJB)
│   │   │       ├── servlets/          # Contrôleurs
│   │   │       ├── filters/           # Filtres de sécurité
│   │   │       ├── enums/             # Énumérations
│   │   │       └── utils/             # Classes utilitaires
│   │   ├── resources/
│   │   │   ├── META-INF/
│   │   │   │   └── persistence.xml    # Configuration JPA
│   │   │   ├── scripts/               # Scripts SQL
│   │   │   └── logback.xml            # Configuration logging
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml            # Configuration web
│   │       │   ├── fragments/         # Composants JSP réutilisables
│   │       │   └── pages/             # Pages JSP
│   │       ├── css/                   # Feuilles de style
│   │       ├── js/                    # Scripts JavaScript
│   │       └── images/                # Images
│   └── test/                          # Tests unitaires
├── pom.xml                            # Configuration Maven
└── README.md
```

## ⚙️ Installation et Configuration

### Prérequis

- **Java JDK 8** ou supérieur
- **Maven 3.6+**
- **MySQL 8.0** ou **PostgreSQL 12+**
- **WildFly 26** ou **GlassFish 5.1**

### 1. Cloner le Projet

```bash
git clone https://github.com/VOTRE_USERNAME/jee_project_controle.git
cd jee_project_controle
```

### 2. Configuration de la Base de Données

#### Option A: MySQL

```bash
# Se connecter à MySQL
mysql -u root -p

# Créer la base de données
CREATE DATABASE gestion_rh CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Créer un utilisateur
CREATE USER 'gestion_rh_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON gestion_rh.* TO 'gestion_rh_user'@'localhost';
FLUSH PRIVILEGES;

# Importer les données de démonstration
USE gestion_rh;
SOURCE src/main/resources/scripts/mysql-init.sql;
```

#### Option B: PostgreSQL

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE gestion_rh;

# Créer un utilisateur
CREATE USER gestion_rh_user WITH PASSWORD 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON DATABASE gestion_rh TO gestion_rh_user;
```

### 3. Configuration du Serveur d'Application (WildFly)

#### Créer une DataSource

Éditer `standalone/configuration/standalone.xml` :

```xml
<datasource jndi-name="java:jboss/datasources/GestionRHDS" pool-name="GestionRHDS">
    <connection-url>jdbc:mysql://localhost:3306/gestion_rh?useSSL=false&amp;serverTimezone=UTC</connection-url>
    <driver>mysql</driver>
    <security>
        <user-name>gestion_rh_user</user-name>
        <password>votre_mot_de_passe</password>
    </security>
</datasource>
```

#### Ajouter le driver MySQL

```bash
# Télécharger le driver MySQL Connector/J
# Copier dans standalone/deployments/
cp mysql-connector-java-8.0.33.jar $WILDFLY_HOME/standalone/deployments/
```

### 4. Compilation du Projet

```bash
# Compiler et packager l'application
mvn clean install

# Ou sans exécuter les tests
mvn clean install -DskipTests
```

### 5. Déploiement

#### Option A: Déploiement avec Maven

```bash
mvn wildfly:deploy
```

#### Option B: Déploiement manuel

```bash
# Copier le WAR dans le dossier deployments
cp target/gestion-rh.war $WILDFLY_HOME/standalone/deployments/
```

### 6. Accès à l'Application

Ouvrez votre navigateur et accédez à :

```
http://localhost:8080/gestion-rh
```

## 🔐 Comptes de Démonstration

| Username    | Mot de passe | Rôle    | Description                      |
|-------------|--------------|---------|----------------------------------|
| admin       | password123  | ADMIN   | Administrateur système           |
| rh.manager  | password123  | RH      | Responsable Ressources Humaines  |
| it.manager  | password123  | MANAGER | Manager d'équipe                 |
| dev.senior  | password123  | EMPLOYE | Développeur senior               |
| dev.junior  | password123  | EMPLOYE | Développeur junior               |

## 🎯 Fonctionnalités par Rôle

### EMPLOYE
- ✅ Consultation de son profil
- ✅ Création de demandes de congés
- ✅ Saisie des feuilles de temps
- ✅ Consultation de ses fiches de paie
- ✅ Notifications personnelles

### MANAGER
- ✅ Toutes les fonctionnalités EMPLOYE
- ✅ Validation des congés de son équipe
- ✅ Validation des feuilles de temps
- ✅ Vue sur les congés de l'équipe
- ✅ Statistiques de l'équipe

### RH
- ✅ Toutes les fonctionnalités MANAGER
- ✅ Gestion complète des employés (CRUD)
- ✅ Validation finale des congés
- ✅ Génération des fiches de paie
- ✅ Accès à tous les rapports
- ✅ Gestion des départements et postes

### ADMIN
- ✅ Toutes les fonctionnalités RH
- ✅ Gestion des comptes utilisateurs
- ✅ Configuration du système
- ✅ Accès à l'historique complet
- ✅ Logs et audit

## 📊 Modèle de Données

### Entités Principales

1. **Employe** - Informations employé avec optimistic locking
2. **Departement** - Départements de l'entreprise
3. **Poste** - Postes avec fourchettes salariales
4. **UserAccount** - Comptes utilisateurs avec sécurité
5. **Conge** - Demandes de congés avec workflow
6. **SoldeConge** - Soldes de congés annuels
7. **FeuilleTemps** - Feuilles de temps hebdomadaires
8. **FichePaie** - Fiches de paie avec calculs automatiques
9. **ActionHistorique** - Traçabilité complète
10. **Notification** - Notifications temps réel

## 🔄 Workflows Implémentés

### Workflow Congés
1. Employé crée une demande (EN_ATTENTE)
2. Manager valide (VALIDE_MANAGER) ou refuse
3. RH valide (APPROUVE) ou refuse
4. Déduction automatique du solde

### Workflow Paie
1. Récupération automatique des feuilles de temps
2. Calcul du salaire brut (base + heures sup + primes)
3. Calcul des déductions (cotisations + IR)
4. Calcul du net à payer
5. Génération PDF de la fiche de paie
6. Validation RH → Marqué comme PAYE

## 🧪 Tests

```bash
# Exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=NomDuTest
```

## 📝 Logs

Les logs sont stockés dans :
- `logs/gestion-rh.log` - Logs généraux
- `logs/security.log` - Logs de sécurité
- `logs/audit.log` - Audit des actions (conservé 365 jours)

## 🚀 Commandes Maven Utiles

```bash
# Nettoyer le projet
mvn clean

# Compiler
mvn compile

# Packager en WAR
mvn package

# Installer dans le repo local
mvn install

# Déployer sur WildFly
mvn wildfly:deploy

# Redéployer
mvn wildfly:redeploy

# Undeploy
mvn wildfly:undeploy
```

## 📦 Génération de Rapports

L'application génère automatiquement :
- 📄 Fiches de paie PDF mensuelles
- 📊 Rapports statistiques RH
- 📈 Tableaux de bord par département
- 📑 Exports Excel pour analyse

## 🔧 Configuration Avancée

### Optimistic Locking

Les entités critiques utilisent `@Version` pour éviter les conflits :
- Employe
- Conge
- FeuilleTemps
- FichePaie

### Sécurité

- Hashage BCrypt des mots de passe
- Sessions avec timeout (30 min)
- Protection CSRF
- Filtres d'authentification et autorisation
- Audit trail complet

## 📖 Documentation

- Cahier des charges : `CahierDesCharges.pdf`
- Documentation technique : À générer
- Guide utilisateur : À générer

## 🐛 Dépannage

### Problème de connexion à la BD

```bash
# Vérifier que MySQL est lancé
systemctl status mysql

# Tester la connexion
mysql -u gestion_rh_user -p gestion_rh
```

### Erreur de déploiement

```bash
# Vérifier les logs WildFly
tail -f $WILDFLY_HOME/standalone/log/server.log
```

### Port déjà utilisé

```bash
# Changer le port dans standalone.xml
<socket-binding name="http" port="${jboss.http.port:8080}"/>
```

## 📧 Contact

Pour toute question ou problème :
- **Amara Oussama**
- **Aben Boutaieb Khalil**

## 🚀 Déploiement sur GitHub

### Créer un nouveau dépôt sur GitHub

1. Allez sur [GitHub](https://github.com) et créez un nouveau dépôt
2. Nommez-le `jee_project_controle` (ou un autre nom de votre choix)
3. **Ne cochez pas** "Initialize with README" (nous avons déjà un README)
4. Cliquez sur "Create repository"

### Pousser le code sur GitHub

```bash
# Ajouter le remote GitHub (remplacez VOTRE_USERNAME par votre nom d'utilisateur GitHub)
git remote add origin https://github.com/VOTRE_USERNAME/jee_project_controle.git

# Renommer la branche principale en 'main' (si nécessaire)
git branch -M main

# Pousser le code
git push -u origin main
```

### Si vous utilisez SSH

```bash
git remote add origin git@github.com:VOTRE_USERNAME/jee_project_controle.git
git push -u origin main
```

## 📜 Licence

Projet académique - JEE 2025

---

**Note** : Ce projet est développé dans un cadre académique et démontre l'utilisation complète de Java EE avec des fonctionnalités professionnelles de gestion RH.

