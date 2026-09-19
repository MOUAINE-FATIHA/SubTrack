package com.formation.abonnements.service;

import com.formation.abonnements.dao.AbonnementDAO;
import com.formation.abonnements.dao.PaiementDAO;
import com.formation.abonnements.entity.*;
import com.formation.abonnements.exception.AbonnementInvalideException;
import com.formation.abonnements.exception.AbonnementNotFoundException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class AbonnementService {

    private final AbonnementDAO abonnementDAO;
    private final PaiementDAO paiementDAO;

    public AbonnementService(AbonnementDAO abonnementDAO, PaiementDAO paiementDAO) {
        this.abonnementDAO = abonnementDAO;
        this.paiementDAO = paiementDAO;
    }

    public Abonnement creerAbonnement(String nomService, double montantMensuel,
                                      LocalDate dateDebut, LocalDate dateFin,
                                      boolean avecEngagement, Integer dureeEngagementMois) {
        validerDates(dateDebut, dateFin);
        validerMontant(montantMensuel);

        Abonnement abonnement;
        if (avecEngagement) {
            if (dureeEngagementMois == null || dureeEngagementMois <= 0) {
                throw new AbonnementInvalideException("La durée d'engagement doit être positive.");
            }
            abonnement = new AbonnementAvecEngagement(nomService, montantMensuel, dateDebut, dateFin, dureeEngagementMois);
        } else {
            abonnement = new AbonnementSansEngagement(nomService, montantMensuel, dateDebut, dateFin);
        }
        return abonnementDAO.create(abonnement);
    }

    public Abonnement modifierAbonnement(String id, String nomService, Double montantMensuel,
                                         LocalDate dateDebut, LocalDate dateFin) {
        Abonnement abonnement = trouverParId(id);
        if (nomService != null) abonnement.setNomService(nomService);
        if (montantMensuel != null) {
            validerMontant(montantMensuel);
            abonnement.setMontantMensuel(montantMensuel);
        }
        if (dateDebut != null) abonnement.setDateDebut(dateDebut);
        if (dateFin != null) abonnement.setDateFin(dateFin);
        validerDates(abonnement.getDateDebut(), abonnement.getDateFin());
        return abonnementDAO.update(abonnement);
    }

    public void supprimerAbonnement(String id) {
        trouverParId(id);
        abonnementDAO.delete(id);
    }

    public Abonnement resilierAbonnement(String id) {
        Abonnement abonnement = trouverParId(id);
        if (abonnement.getStatut() == StatutAbonnement.RESILIE) {
            throw new AbonnementInvalideException("Cet abonnement est déjà résilié.");
        }
        abonnement.setStatut(StatutAbonnement.RESILIE);
        return abonnementDAO.update(abonnement);
    }

    public Abonnement trouverParId(String id) {
        return abonnementDAO.findById(id)
                .orElseThrow(() -> new AbonnementNotFoundException("Aucun abonnement trouvé avec l'id : " + id));
    }

    public List<Abonnement> listerTous() {
        return abonnementDAO.findAll();
    }

    public List<Abonnement> listerActifs() {
        return abonnementDAO.findActiveSubscriptions();
    }

    /**
     * Génère les échéances mensuelles (statut NON_PAYE) entre dateDebut et aujourd'hui
     * (ou dateFin si elle est déjà passée).
     */
    public List<Paiement> genererEcheances(String idAbonnement, TypePaiement typePaiementParDefaut) {
        Abonnement abonnement = trouverParId(idAbonnement);
        LocalDate debut = abonnement.getDateDebut();
        LocalDate limite = (abonnement.getDateFin() != null && abonnement.getDateFin().isBefore(LocalDate.now()))
                ? abonnement.getDateFin()
                : LocalDate.now();

        long nbMois = ChronoUnit.MONTHS.between(debut.withDayOfMonth(1), limite.withDayOfMonth(1));

        return LongStream.rangeClosed(0, Math.max(nbMois, 0))
                .mapToObj(debut::plusMonths)
                .map(dateEcheance -> new Paiement(abonnement.getId(), dateEcheance, typePaiementParDefaut))
                .map(paiementDAO::create)
                .collect(Collectors.toList());
    }

    private void validerDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null) {
            throw new AbonnementInvalideException("La date de début est obligatoire.");
        }
        if (dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new AbonnementInvalideException("La date de fin doit être postérieure à la date de début.");
        }
    }

    private void validerMontant(double montant) {
        if (montant <= 0) {
            throw new AbonnementInvalideException("Le montant mensuel doit être strictement positif.");
        }
    }
}