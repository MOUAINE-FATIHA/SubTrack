package com.formation.abonnements.exception;

public class PaiementNotFoundException extends RuntimeException {
    public PaiementNotFoundException(String message) {
        super(message);
    }
}