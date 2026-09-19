package com.formation.abonnements.dao;

import com.formation.abonnements.entity.Paiement;
import com.formation.abonnements.entity.StatutPaiement;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PaiementDAOImpl implements PaiementDAO {

    private final Map<String, Paiement> storage = new ConcurrentHashMap<>();

    @Override
    public Paiement create(Paiement paiement) {
        storage.put(paiement.getIdPaiement(), paiement);
        return paiement;
    }

    @Override
    public Optional<Paiement> findById(String idPaiement) {
        return Optional.ofNullable(storage.get(idPaiement));
    }

    @Override
    public List<Paiement> findByAbonnement(String idAbonnement) {
        return storage.values().stream()
                .filter(p -> p.getIdAbonnement().equals(idAbonnement))
                .collect(Collectors.toList());
    }

    @Override
    public List<Paiement> findAll() {
        return new java.util.ArrayList<>(storage.values());
    }

    @Override
    public Paiement update(Paiement paiement) {
        storage.put(paiement.getIdPaiement(), paiement);
        return paiement;
    }

    @Override
    public void delete(String idPaiement) {
        storage.remove(idPaiement);
    }

    @Override
    public List<Paiement> findUnpaidByAbonnement(String idAbonnement) {
        return storage.values().stream()
                .filter(p -> p.getIdAbonnement().equals(idAbonnement))
                .filter(p -> p.getStatut() == StatutPaiement.NON_PAYE || p.getStatut() == StatutPaiement.EN_RETARD)
                .collect(Collectors.toList());
    }

    @Override
    public List<Paiement> findLastPayments(int n) {
        return storage.values().stream()
                .filter(p -> p.getDatePaiement() != null)
                .sorted(Comparator.comparing(Paiement::getDatePaiement).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
}