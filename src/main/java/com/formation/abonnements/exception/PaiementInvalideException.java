package com.formation.abonnements.exception;

public class PaiementInvalideException extends RuntimeException {
    public PaiementInvalideException(String message) {
        super(message);
    }
}