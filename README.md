# Application de gestion d'abonnements

Application console Java 8 permettant de centraliser la gestion des abonnements
(personnels et professionnels), suivre les échéances, détecter les paiements
manqués et générer des rapports financiers.

## Lien Jira
<https://fatihamouaine1-1788777177360.atlassian.net/jira/software/projects/SUB/boards/34?sprintStarted=true&filter=&groupBy=none>

## Architecture
- entity : Abonnement, Paiement
- dao : accès aux données (persistance en mémoire)
- service : logique métier
- ui : menu console
- util : utilitaires (dates, validations)
- exception : exceptions métier