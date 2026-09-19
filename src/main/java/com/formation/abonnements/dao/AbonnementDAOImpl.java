package com.formation.abonnements.dao;

import com.formation.abonnements.entity.Abonnement;
import com.formation.abonnements.entity.StatutAbonnement;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AbonnementDAOImpl implements AbonnementDAO {

    private final Map<String, Abonnement> storage = new ConcurrentHashMap<>();

    @Override
    public Abonnement create(Abonnement abonnement) {
        storage.put(abonnement.getId(), abonnement);
        return abonnement;
    }

    @Override
    public Optional<Abonnement> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Abonnement> findAll() {
        return new java.util.ArrayList<>(storage.values());
    }

    @Override
    public Abonnement update(Abonnement abonnement) {
        storage.put(abonnement.getId(), abonnement);
        return abonnement;
    }

    @Override
    public void delete(String id) {
        storage.remove(id);
    }

    @Override
    public List<Abonnement> findActiveSubscriptions() {
        return storage.values().stream()
                .filter(a -> a.getStatut() == StatutAbonnement.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public List<Abonnement> findByType(Class<? extends Abonnement> type) {
        return storage.values().stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }
}