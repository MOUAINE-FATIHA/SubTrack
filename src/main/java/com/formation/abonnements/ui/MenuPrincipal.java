package com.formation.abonnements.ui;

import com.formation.abonnements.dao.*;
import com.formation.abonnements.entity.*;
import com.formation.abonnements.exception.*;
import com.formation.abonnements.service.*;
import com.formation.abonnements.util.DateUtil;
import com.formation.abonnements.util.Validateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner sc = new Scanner(System.in);
    private final AbonnementDAO abonnementDAO = new AbonnementDAOImpl();
    private final PaiementDAO paiementDAO = new PaiementDAOImpl();
    private final AbonnementService abonnementService = new AbonnementService(abonnementDAO, paiementDAO);
    private final PaiementService paiementService = new PaiementService(paiementDAO, abonnementDAO);

    public static void main(String[] args) {
        new MenuPrincipal().run();
    }

    private void run() {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            int choix = Validateur.lireEntier(sc, "Votre choix");
            continuer = traiterChoix(choix);
        }
        System.out.println("Au revoir !");
    }

    private void afficherMenu() {
        System.out.println("\n===== GESTION DES ABONNEMENTS =====");
        System.out.println(" 1. Créer un abonnement");
        System.out.println(" 2. Modifier un abonnement");
        System.out.println(" 3. Supprimer un abonnement");
        System.out.println(" 5. Lister les abonnements");
        System.out.println(" 6. Générer les échéances d'un abonnement");
        System.out.println(" 7. Afficher les paiements d'un abonnement");
        System.out.println(" 8. Enregistrer un paiement");
        System.out.println(" 9. Modifier un paiement");
        System.out.println("10. Supprimer un paiement");
        System.out.println("11. Paiements manqués + montant total");
        System.out.println("12. Somme payée d'un abonnement");
        System.out.println("13. Afficher les 5 derniers paiements");
        System.out.println("14. Rapports financiers");
        System.out.println(" 0. Quitter");
    }

    private boolean traiterChoix(int choix) {
        switch (choix) {
            case 1: creerAbonnement(); break;
            case 2: modifierAbonnement(); break;
            case 3: supprimerAbonnement(); break;
            case 4: resilierAbonnement(); break;
            case 5: listerAbonnements(); break;
            case 6: genererEcheances(); break;
            case 7: afficherPaiementsAbonnement(); break;
            case 8: enregistrerPaiement(); break;
            case 9: modifierPaiement(); break;
            case 10: supprimerPaiement(); break;
            case 11: afficherImpayes(); break;
            case 12: afficherSommePayee(); break;
            case 13: afficherDerniersPaiements(); break;
            case 14: genererRapports(); break;
            case 0: return false;
            default: System.out.println("Choix invalide.");
        }
        return true;
    }

    // ---------- Abonnements ----------

    private void creerAbonnement() {
        System.out.println("\n--- Création d'un abonnement ---");
        String nom = Validateur.lireTexteObligatoire(sc, "Nom du service");
        double montant = Validateur.lireDouble(sc, "Montant mensuel (EUR)");

        LocalDate dateDebut = DateUtil.lireDate(sc, "Date de début");
        while (dateDebut == null) {
            System.out.println("La date de début est obligatoire.");
            dateDebut = DateUtil.lireDate(sc, "Date de début");
        }
        LocalDate dateFin = DateUtil.lireDate(sc, "Date de fin");

        System.out.print("Avec engagement ? (o/n) : ");
        boolean avecEngagement = sc.nextLine().trim().equalsIgnoreCase("o");
        Integer duree = null;
        if (avecEngagement) {
            duree = Validateur.lireEntier(sc, "Durée d'engagement (en mois)");
        }

        try {
            Abonnement abonnement = abonnementService.creerAbonnement(nom, montant, dateDebut, dateFin, avecEngagement, duree);
            System.out.println("Abonnement créé avec succès :");
            System.out.println("  id: " + abonnement.getId());
            System.out.println("  " + abonnement);
        } catch (AbonnementInvalideException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void modifierAbonnement() {
        System.out.println("\n--- Modification d'un abonnement ---");
        String id = Validateur.lireTexteObligatoire(sc, "Id de l'abonnement");
        try {
            Abonnement existant = abonnementService.trouverParId(id);
            System.out.println("Abonnement actuel : " + existant);

            System.out.print("Nouveau nom (vide = inchangé) : ");
            String nom = sc.nextLine().trim();

            System.out.print("Nouveau montant (vide = inchangé) : ");
            String montantSaisi = sc.nextLine().trim();
            Double montant = montantSaisi.isEmpty() ? null : Double.parseDouble(montantSaisi);

            LocalDate dateDebut = DateUtil.lireDate(sc, "Nouvelle date de début");
            LocalDate dateFin = DateUtil.lireDate(sc, "Nouvelle date de fin");

            Abonnement modifie = abonnementService.modifierAbonnement(
                    id, nom.isEmpty() ? null : nom, montant, dateDebut, dateFin);
            System.out.println("Abonnement modifié : " + modifie);
        } catch (AbonnementNotFoundException | AbonnementInvalideException | NumberFormatException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void supprimerAbonnement() {
        String id = Validateur.lireTexteObligatoire(sc, "Id de l'abonnement");
        try {
            abonnementService.supprimerAbonnement(id);
            System.out.println("Abonnement supprimé.");
        } catch (AbonnementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void resilierAbonnement() {
        String id = Validateur.lireTexteObligatoire(sc, "Id de l'abonnement");
        try {
            Abonnement resilie = abonnementService.resilierAbonnement(id);
            System.out.println("Abonnement résilié : " + resilie);
        } catch (AbonnementNotFoundException | AbonnementInvalideException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void listerAbonnements() {
        List<Abonnement> abonnements = abonnementService.listerTous();
        if (abonnements.isEmpty()) {
            System.out.println("Aucun abonnement enregistré.");
            return;
        }
        abonnements.forEach(a -> System.out.println(a.getId() + " | " + a));
    }

    private void genererEcheances() {
        String id = Validateur.lireTexteObligatoire(sc, "Id de l'abonnement");
        try {
            TypePaiement type = choisirTypePaiement();
            List<Paiement> echeances = abonnementService.genererEcheances(id, type);
            System.out.println(echeances.size() + " échéance(s) générée(s) :");
            echeances.forEach(System.out::println);
        } catch (AbonnementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // ---------- Paiements ----------

    private void afficherPaiementsAbonnement() {
        String id = Validateur.lireTexteObligatoire(sc, "Id de l'abonnement");
        try {
            abonnementService.trouverParId(id);
            List<Paiement> paiements = paiementService.paiementsDe(id);
            if (paiements.isEmpty()) {
                System.out.println("Aucun paiement pour cet abonnement.");
            } else {
                paiements.forEach(System.out::println);
            }
        } catch (AbonnementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void enregistrerPaiement() {
        String id = Validateur.lireTexteObligatoire(sc, "Id du paiement");
        try {
            LocalDate date = DateUtil.lireDate(sc, "Date de paiement");
            while (date == null) {
                System.out.println("La date de paiement est obligatoire.");
                date = DateUtil.lireDate(sc, "Date de paiement");
            }
            Paiement paiement = paiementService.enregistrerPaiement(id, date);
            System.out.println("Paiement enregistré : " + paiement);
        } catch (PaiementNotFoundException | PaiementInvalideException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void modifierPaiement() {
        String id = Validateur.lireTexteObligatoire(sc, "Id du paiement");
        try {
            Paiement existant = paiementService.trouverParId(id);
            System.out.println("Paiement actuel : " + existant);
            LocalDate dateEcheance = DateUtil.lireDate(sc, "Nouvelle date d'échéance");

            System.out.print("Modifier le type de paiement ? (o/n) : ");
            TypePaiement type = null;
            if (sc.nextLine().trim().equalsIgnoreCase("o")) {
                type = choisirTypePaiement();
            }
            Paiement modifie = paiementService.modifierPaiement(id, dateEcheance, type);
            System.out.println("Paiement modifié : " + modifie);
        } catch (PaiementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void supprimerPaiement() {
        String id = Validateur.lireTexteObligatoire(sc, "Id du paiement");
        try {
            paiementService.supprimerPaiement(id);
            System.out.println("Paiement supprimé.");
        } catch (PaiementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void afficherImpayes() {
        System.out.println("\n--- Paiements manqués (abonnements avec engagement) ---");
        Map<Abonnement, List<Paiement>> impayes = paiementService.detecterImpayes();
        if (impayes.isEmpty()) {
            System.out.println("Aucun impayé détecté.");
            return;
        }
        impayes.forEach((abonnement, paiements) -> {
            System.out.println(abonnement.getNomService() + " (" + paiements.size() + " impayé(s))");
            paiements.forEach(p -> System.out.println("   " + p));
        });
        System.out.printf("Montant total impayé : %.2f EUR%n", paiementService.montantTotalImpaye());
    }

    private void afficherSommePayee() {
        String id = Validateur.lireTexteObligatoire(sc, "Id de l'abonnement");
        try {
            abonnementService.trouverParId(id);
            double somme = paiementService.sommePayee(id);
            System.out.printf("Somme payée : %.2f EUR%n", somme);
        } catch (AbonnementNotFoundException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void afficherDerniersPaiements() {
        List<Paiement> derniers = paiementService.dernierPaiements(5);
        if (derniers.isEmpty()) {
            System.out.println("Aucun paiement enregistré.");
            return;
        }
        derniers.forEach(System.out::println);
    }



    private void genererRapports() {
        System.out.println("\n--- Rapports financiers ---");
        System.out.println("1) Rapport mensuel");
        System.out.println("2) Rapport annuel");
        System.out.println("3) Rapport des impayés");
        int choix = Validateur.lireEntier(sc, "Choix");
        switch (choix) {
            case 1:
                int mois = Validateur.lireEntier(sc, "Mois (1-12)");
                int anneeMois = Validateur.lireEntier(sc, "Année");
                System.out.printf("Total payé en %02d/%d : %.2f EUR%n", mois, anneeMois,
                        paiementService.rapportMensuel(mois, anneeMois));
                break;
            case 2:
                int anneeAnnuel = Validateur.lireEntier(sc, "Année");
                System.out.printf("Total payé en %d : %.2f EUR%n", anneeAnnuel,
                        paiementService.rapportAnnuel(anneeAnnuel));
                break;
            case 3:
                List<Paiement> impayes = paiementService.rapportImpayes();
                if (impayes.isEmpty()) {
                    System.out.println("Aucun impayé.");
                } else {
                    impayes.forEach(System.out::println);
                }
                break;
            default:
                System.out.println("Choix invalide.");
        }
    }

    private TypePaiement choisirTypePaiement() {
        System.out.println("Type de paiement : 1) CB  2) VIREMENT  3) PRELEVEMENT");
        int choix = Validateur.lireEntier(sc, "Choix");
        switch (choix) {
            case 1: return TypePaiement.CB;
            case 2: return TypePaiement.VIREMENT;
            case 3: return TypePaiement.PRELEVEMENT;
            default:
                System.out.println("Choix invalide, CB appliqué par défaut.");
                return TypePaiement.CB;
        }
    }
}