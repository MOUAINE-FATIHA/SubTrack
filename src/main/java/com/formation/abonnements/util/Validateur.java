package com.formation.abonnements.util;

import java.util.Scanner;

public final class Validateur {

    private Validateur() {
    }

    public static double lireDouble(Scanner sc, String message) {
        while (true) {
            System.out.print(message + " : ");
            String saisie = sc.nextLine().trim();
            try {
                double valeur = Double.parseDouble(saisie);
                if (valeur <= 0) {
                    System.out.println("La valeur doit être strictement positive.");
                    continue;
                }
                return valeur;
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre valide (ex: 12.99).");
            }
        }
    }

    public static int lireEntier(Scanner sc, String message) {
        while (true) {
            System.out.print(message + " : ");
            String saisie = sc.nextLine().trim();
            try {
                return Integer.parseInt(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez saisir un nombre entier valide.");
            }
        }
    }

    public static String lireTexteObligatoire(Scanner sc, String message) {
        while (true) {
            System.out.print(message + " : ");
            String saisie = sc.nextLine().trim();
            if (!saisie.isEmpty()) {
                return saisie;
            }
            System.out.println("Cette valeur ne peut pas être vide.");
        }
    }
}