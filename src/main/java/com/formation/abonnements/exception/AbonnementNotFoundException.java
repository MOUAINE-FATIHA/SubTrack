package com.formation.abonnements.exception;

public class AbonnementNotFoundException extends RuntimeException {
    public AbonnementNotFoundException(String message) {
        super(message);
    }
}