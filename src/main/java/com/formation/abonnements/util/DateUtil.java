package com.formation.abonnements.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class DateUtil {

    public static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtil() {
    }

    public static LocalDate parser(String texte) {
        return LocalDate.parse(texte.trim(), FORMAT);
    }

    public static String formater(LocalDate date) {
        return date == null ? "-" : date.format(FORMAT);
    }

    public static LocalDate lireDate(Scanner sc, String message) {
        while (true) {
            System.out.print(message + " (jj/mm/aaaa, vide = aucune) : ");
            String saisie = sc.nextLine().trim();
            if (saisie.isEmpty()) {
                return null;
            }
            try {
                return parser(saisie);
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide. Exemple attendu : 25/12/2026");
            }
        }
    }
}