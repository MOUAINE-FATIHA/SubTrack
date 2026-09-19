package com.formation.abonnements.dao;

import com.formation.abonnements.entity.Paiement;

import java.util.List;
import java.util.Optional;
public interface PaiementDAO {

    Paiement create(Paiement paiement);

    Optional<Paiement> findById(String idPaiement);

    List<Paiement> findByAbonnement(String idAbonnement);

    List<Paiement> findAll();

    Paiement update(Paiement paiement);

    void delete(String idPaiement);

    List<Paiement> findUnpaidByAbonnement(String idAbonnement);

    List<Paiement> findLastPayments(int n);
}