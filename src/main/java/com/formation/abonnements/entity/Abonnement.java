package com.formation.abonnements.entity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
public abstract class Abonnement {

    private final String id;
    private String nomService;
    private double montantMensuel;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutAbonnement statut;

    protected Abonnement(String nomService, double montantMensuel, LocalDate dateDebut, LocalDate dateFin) {
        this.id = UUID.randomUUID().toString();
        this.nomService = nomService;
        this.montantMensuel = montantMensuel;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = StatutAbonnement.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getNomService() {
        return nomService;
    }

    public void setNomService(String nomService) {
        this.nomService = nomService;
    }

    public double getMontantMensuel() {
        return montantMensuel;
    }

    public void setMontantMensuel(double montantMensuel) {
        this.montantMensuel = montantMensuel;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public StatutAbonnement getStatut() {
        return statut;
    }

    public void setStatut(StatutAbonnement statut) {
        this.statut = statut;
    }
    public abstract String getTypeAbonnement();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Abonnement)) return false;
        Abonnement that = (Abonnement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "[%s] %s - %.2f EUR/mois - du %s au %s - statut: %s",
                getTypeAbonnement(), nomService, montantMensuel, dateDebut, dateFin, statut
        );
    }
}