package com.formation.abonnements.dao;
import com.formation.abonnements.entity.Abonnement;
import java.util.List;
import java.util.Optional;
public interface AbonnementDAO {
    Abonnement create(Abonnement abonnement);
    Optional<Abonnement> findById(String id);
    List<Abonnement> findAll();
    Abonnement update(Abonnement abonnement);
    void delete(String id);
    List<Abonnement> findActiveSubscriptions();
    List<Abonnement> findByType(Class<? extends Abonnement> type);
}