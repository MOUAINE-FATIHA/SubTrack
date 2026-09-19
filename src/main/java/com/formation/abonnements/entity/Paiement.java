package com.formation.abonnements.entity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
public class Paiement {

    private final String idPaiement;
    private final String idAbonnement;
    private LocalDate dateEcheance;
    private LocalDate datePaiement;
    private TypePaiement typePaiement;
    private StatutPaiement statut;

    public Paiement(String idAbonnement, LocalDate dateEcheance, TypePaiement typePaiement) {
        this.idPaiement = UUID.randomUUID().toString();
        this.idAbonnement = idAbonnement;
        this.dateEcheance = dateEcheance;
        this.typePaiement = typePaiement;
        this.statut = StatutPaiement.NON_PAYE;
    }

    public String getIdPaiement() {
        return idPaiement;
    }

    public String getIdAbonnement() {
        return idAbonnement;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public TypePaiement getTypePaiement() {
        return typePaiement;
    }

    public void setTypePaiement(TypePaiement typePaiement) {
        this.typePaiement = typePaiement;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paiement)) return false;
        Paiement paiement = (Paiement) o;
        return Objects.equals(idPaiement, paiement.idPaiement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaiement);
    }

    @Override
    public String toString() {
        return String.format(
                "id: %s | abonnement: %s | due le %s | payée le %s | type: %s | statut: %s",
                idPaiement, idAbonnement, dateEcheance,
                datePaiement != null ? datePaiement : "-", typePaiement, statut
        );
    }
}