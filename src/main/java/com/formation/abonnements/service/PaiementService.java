package com.formation.abonnements.service;

import com.formation.abonnements.dao.AbonnementDAO;
import com.formation.abonnements.dao.PaiementDAO;
import com.formation.abonnements.entity.*;
import com.formation.abonnements.exception.PaiementInvalideException;
import com.formation.abonnements.exception.PaiementNotFoundException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaiementService {

    private final PaiementDAO paiementDAO;
    private final AbonnementDAO abonnementDAO;

    public PaiementService(PaiementDAO paiementDAO, AbonnementDAO abonnementDAO) {
        this.paiementDAO = paiementDAO;
        this.abonnementDAO = abonnementDAO;
    }

    public Paiement enregistrerPaiement(String idPaiement, LocalDate datePaiement) {
        Paiement paiement = trouverParId(idPaiement);
        if (datePaiement == null) {
            throw new PaiementInvalideException("La date de paiement est obligatoire.");
        }
        paiement.setDatePaiement(datePaiement);
        paiement.setStatut(StatutPaiement.PAYE);
        return paiementDAO.update(paiement);
    }

    public Paiement modifierPaiement(String idPaiement, LocalDate dateEcheance, TypePaiement typePaiement) {
        Paiement paiement = trouverParId(idPaiement);
        if (dateEcheance != null) paiement.setDateEcheance(dateEcheance);
        if (typePaiement != null) paiement.setTypePaiement(typePaiement);
        return paiementDAO.update(paiement);
    }

    public void supprimerPaiement(String idPaiement) {
        trouverParId(idPaiement);
        paiementDAO.delete(idPaiement);
    }

    public Paiement trouverParId(String idPaiement) {
        return paiementDAO.findById(idPaiement)
                .orElseThrow(() -> new PaiementNotFoundException("Aucun paiement trouvé avec l'id : " + idPaiement));
    }

    public List<Paiement> paiementsDe(String idAbonnement) {
        return paiementDAO.findByAbonnement(idAbonnement);
    }

    public List<Paiement> dernierPaiements(int n) {
        return paiementDAO.findLastPayments(n);
    }

    public double sommePayee(String idAbonnement) {
        double montantMensuel = abonnementDAO.findById(idAbonnement)
                .map(Abonnement::getMontantMensuel)
                .orElse(0.0);

        return paiementDAO.findByAbonnement(idAbonnement).stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .collect(Collectors.summingDouble(p -> montantMensuel));
    }

    /**
     * Impayés détectés uniquement pour les abonnements AVEC engagement (règle métier du brief).
     */
    public Map<Abonnement, List<Paiement>> detecterImpayes() {
        List<Abonnement> abonnementsAvecEngagement = abonnementDAO.findByType(AbonnementAvecEngagement.class);

        Map<Abonnement, List<Paiement>> resultat = new LinkedHashMap<>();
        for (Abonnement abonnement : abonnementsAvecEngagement) {
            List<Paiement> impayes = paiementDAO.findUnpaidByAbonnement(abonnement.getId());
            if (!impayes.isEmpty()) {
                resultat.put(abonnement, impayes);
            }
        }
        return resultat;
    }

    public double montantTotalImpaye() {
        return detecterImpayes().entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(p -> e.getKey().getMontantMensuel()))
                .collect(Collectors.summingDouble(Double::doubleValue));
    }

    public double rapportMensuel(int mois, int annee) {
        return paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .filter(p -> p.getDatePaiement().getYear() == annee && p.getDatePaiement().getMonthValue() == mois)
                .collect(Collectors.summingDouble(this::montantAbonnement));
    }

    public double rapportAnnuel(int annee) {
        return paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .filter(p -> p.getDatePaiement().getYear() == annee)
                .collect(Collectors.summingDouble(this::montantAbonnement));
    }

    public List<Paiement> rapportImpayes() {
        return paiementDAO.findAll().stream()
                .filter(p -> p.getStatut() != StatutPaiement.PAYE)
                .collect(Collectors.toList());
    }

    private double montantAbonnement(Paiement p) {
        return abonnementDAO.findById(p.getIdAbonnement())
                .map(Abonnement::getMontantMensuel)
                .orElse(0.0);
    }
}