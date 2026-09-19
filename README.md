# SubTrack — Application de Gestion d'Abonnements

<h3 align="center">Application console Java 8 pour centraliser le suivi des abonnements</h3>

<p align="center">
  <em>Créer, suivre et gérer ses abonnements (avec ou sans engagement), automatiser la génération des échéances,
  détecter les impayés et produire des rapports financiers — le tout en ligne de commande.</em>
</p>

---

## Présentation

**SubTrack** répond à un besoin concret : suivre facilement des abonnements personnels ou professionnels (streaming, assurances, forfaits mobiles, outils SaaS...) sans perdre le fil des échéances ni des paiements manqués.

Le projet applique une **architecture en couches** (UI → Service → DAO → Entity) et met en pratique les apports de **Java 8** côté programmation fonctionnelle : Stream API, expressions lambda, `Optional`, `Collectors`, ainsi que l'API `java.time` pour la gestion des dates.

---

## Fonctionnalités

### Gestion des abonnements
- Création d'un abonnement **avec engagement** (avec durée en mois) ou **sans engagement**
- Modification (nom, montant, dates)
- Suppression
- Résiliation (passage au statut `RESILIE`)
- Liste de tous les abonnements

### Échéances & paiements
- Génération automatique des échéances mensuelles (statut `NON_PAYE`) à partir de la date de début
- Enregistrement d'un paiement (date + mode : `CB`, `VIREMENT`, `PRELEVEMENT`)
- Modification / suppression d'un paiement
- Consultation des paiements d'un abonnement
- Affichage des 5 derniers paiements

### Suivi des impayés
- Détection des paiements manqués, **limitée aux abonnements avec engagement** (règle métier du cahier des charges)
- Calcul du montant total impayé
- Calcul de la somme déjà payée pour un abonnement donné

### Rapports financiers
- Rapport mensuel (total payé sur un mois donné)
- Rapport annuel (total payé sur une année donnée)
- Rapport des impayés (liste complète, tous abonnements confondus)

---

## Stack technique

| Élément | Détail |
|---|---|
| **Langage** | Java 8 |
| **Build** | Maven |
| **Stream API** | `filter`, `map`, `sorted`, `limit`, `collect` |
| **Programmation fonctionnelle** | Lambdas, références de méthode (`Abonnement::getMontantMensuel`) |
| **Optional\<T\>** | Utilisé dans les recherches par id (`findById`) pour éviter les `null` |
| **Collectors** | `toList`, `summingDouble`, `groupingBy` |
| **java.time** | `LocalDate`, `plusMonths`, calcul d'intervalles entre dates |
| **Persistance** | Collections Java en mémoire (`Map`), aucune base de données pour ce sprint |
| **Versionning** | Git & GitHub |
| **Suivi de projet** | Jira (Epics / Stories / Tâches) |

---

## Architecture

Le projet suit une architecture en couches stricte : chaque couche ne communique qu'avec celle directement en dessous.

```
 UI (console)
     │  saisie / affichage
     ▼
 Service (logique métier, validations, règles)
     │  appelle une interface, pas une implémentation
     ▼
 DAO (accès aux données — interface + implémentation en mémoire)
     │  manipule
     ▼
 Entity (Abonnement, Paiement, enums)

 Util       → utilitaires transverses (dates, validations de saisie)
 Exception  → exceptions métier personnalisées, utilisées par toutes les couches
```

**Pourquoi cette séparation ?** Elle isole la logique métier (Service) du mode de stockage (DAO). Le Service dépend d'une **interface** DAO, jamais de son implémentation directe — ce qui permettrait par exemple de remplacer la persistance en mémoire par une base de données via JDBC sans modifier ni le Service ni l'UI.

---

## Modèle de données

```
Abonnement (classe abstraite)
 ├── AbonnementAvecEngagement   (+ dureeEngagementMois)
 └── AbonnementSansEngagement

Paiement
 → relié à un Abonnement via idAbonnement (relation 1..n)

Enums :
 StatutAbonnement : ACTIVE · SUSPENDU · RESILIE
 StatutPaiement   : PAYE · NON_PAYE · EN_RETARD
 TypePaiement     : CB · VIREMENT · PRELEVEMENT
```

---

## Arborescence du projet

```
gestion-abonnements/
├── .gitignore
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── formation/
                    └── abonnements/
                        │
                        ├── entity/                          # Objets métier
                        │   ├── Abonnement.java               #   Classe abstraite
                        │   ├── AbonnementAvecEngagement.java
                        │   ├── AbonnementSansEngagement.java
                        │   ├── Paiement.java
                        │   ├── StatutAbonnement.java
                        │   ├── StatutPaiement.java
                        │   └── TypePaiement.java
                        │
                        ├── dao/                             # Accès aux données
                        │   ├── AbonnementDAO.java            #   Interface
                        │   ├── AbonnementDAOImpl.java         #   Implémentation en mémoire
                        │   ├── PaiementDAO.java              #   Interface
                        │   └── PaiementDAOImpl.java           #   Implémentation en mémoire
                        │
                        ├── service/                         # Logique métier
                        │   ├── AbonnementService.java
                        │   └── PaiementService.java
                        │
                        ├── ui/                              # Interface console
                        │   └── MenuPrincipal.java
                        │
                        ├── util/                            # Utilitaires transverses
                        │   ├── DateUtil.java
                        │   └── Validateur.java
                        │
                        └── exception/                       # Exceptions métier
                            ├── AbonnementNotFoundException.java
                            ├── AbonnementInvalideException.java
                            ├── PaiementNotFoundException.java
                            └── PaiementInvalideException.java
```

---

## Installation et exécution

### Prérequis

| Outil | Version minimale |
|---|---|
| JDK | 8 |
| Maven | 3.x |
| Git | 2.x |

### Cloner et lancer

```bash
git clone https://github.com/MOUAINE-FATIHA/SubTrack.git
cd SubTrack

mvn compile
mvn exec:java -Dexec.mainClass="com.formation.abonnements.ui.MenuPrincipal"
```

### Alternative — lancer depuis IntelliJ

Ouvrir `MenuPrincipal.java` et cliquer sur le bouton ▶️ à côté de la méthode `main`.

### Compilation manuelle (sans Maven)

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.formation.abonnements.ui.MenuPrincipal
```

---

## Suivi du projet

- **Board Jira** : <colle ici l'URL de ton board Jira>
- **Dépôt GitHub** : https://github.com/MOUAINE-FATIHA/SubTrack

---

## Pistes d'amélioration (bonus)

- Persistance via base de données relationnelle (PostgreSQL / MySQL) avec JDBC
- Sauvegarde des logs applicatifs dans un fichier
- Export des rapports financiers en CSV / JSON

---

## Auteur

**Fatiha Mouaine** — Projet individuel réalisé dans le cadre du Sprint 1 (14/09/2026 → 18/09/2026).