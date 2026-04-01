# SGCD — Système de Gestion d'un Cabinet Dentaire

Projet Java EE — Licence Génie Informatique — FST Fès 2025-2026

## Stack technique

| Couche | Technologie |
|--------|------------|
| Langage | Java 17 |
| Serveur | Apache Tomcat 10.x (Jakarta EE 10) |
| Vue | JSP + JSTL 3 + Bootstrap 5 |
| Persistance | JDBC + MySQL 8 |
| Sécurité | BCrypt (jbcrypt 0.4) |
| Build | Maven 3 |

## Prérequis

- JDK 17+
- Apache Tomcat 10.1.x
- MySQL 8.0
- Maven 3.8+
- IntelliJ IDEA (recommandé)

## Installation

### 1. Base de données

```sql
-- Exécuter le script SQL fourni dans /sql/sgcd.sql
-- Il crée la BD, les tables et insère les données de test
mysql -u root -p < sql/sgcd.sql
```

### 2. Configuration

Modifier `src/main/webapp/WEB-INF/web.xml` :

```xml
<context-param>
    <param-name>db.url</param-name>
    <param-value>jdbc:mysql://localhost:3306/sgcd?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Africa/Casablanca</param-value>
</context-param>
<context-param>
    <param-name>db.user</param-name>
    <param-value>root</param-value>         <!-- votre user MySQL -->
</context-param>
<context-param>
    <param-name>db.password</param-name>
    <param-value></param-value>              <!-- votre mot de passe MySQL -->
</context-param>
```

### 3. Compilation et déploiement

```bash
# Compiler et packager
mvn clean package

# Le WAR est généré dans target/sgcd-1.0-SNAPSHOT.war
# Copier dans le dossier webapps de Tomcat
cp target/sgcd-1.0-SNAPSHOT.war $TOMCAT_HOME/webapps/sgcd.war

# Démarrer Tomcat
$TOMCAT_HOME/bin/startup.sh
```

### 4. Accès

```
http://localhost:8080/sgcd
```

## Comptes de test

| Login | Mot de passe | Rôle |
|-------|-------------|------|
| h.fassi@sgcd.ma | sgcd1234 | Administrateur |
| r.mansouri@sgcd.ma | sgcd1234 | Dentiste |
| l.tazi@sgcd.ma | sgcd1234 | Dentiste |
| s.alaoui@sgcd.ma | sgcd1234 | Assistante |

## Architecture MVC

```
ma.fst.sgcd/
├── model/
│   ├── enums/          7 enums (Role, StatutRDV, MotifRDV, Sexe…)
│   └── *.java          12 entités métier
├── repository/
│   ├── IRepository.java   interface générique
│   └── *Repository.java   8 implémentations JDBC
├── service/            5 services (Auth, Patient, RDV, Consultation, Facture)
├── controller/         9 Servlets @WebServlet
├── filter/             AuthFilter (protection globale)
├── listener/           AppContextListener (init DataSource)
└── util/               DBUtil, PasswordUtil
```

## URLs disponibles

| URL | Description | Rôles |
|-----|-------------|-------|
| /auth | Page de connexion | Public |
| /dashboard | Tableau de bord + agenda du jour | Tous |
| /patients | Liste des patients (+ recherche) | Tous |
| /patients?action=add | Ajouter patient | Assistante, Admin |
| /patients?action=detail&id=X | Dossier complet | Tous |
| /rdv | Agenda du jour | Assistante, Dentiste |
| /rdv?action=add | Nouveau rendez-vous | Assistante |
| /consultation?action=ouvrir&idRdv=X | Nouvelle consultation | Dentiste |
| /consultation?action=detail&id=X | Détail consultation | Dentiste |
| /facture | Liste des factures | Assistante |
| /facture?action=detail&id=X | Détail + paiement | Assistante |
| /admin/utilisateurs | Gestion utilisateurs | Admin |
| /statistiques | Tableau de bord KPI | Admin |
| /logout | Déconnexion | Tous |

## Diagramme de classes simplifié

```
Utilisateur ◄── Administrateur
            ◄── Dentiste (specialite, joursDisponibles)
            ◄── Assistante (poste)

Patient ──1──── DossierMedical ──*──── Consultation ──1──── Facture
        └── 0..1── ResponsableLegal           |──*──── Acte
                                              |──0..1── Prescription ──*──── Medicament
                                              |──0..*── Document

RendezVous ──1──► Consultation
```
