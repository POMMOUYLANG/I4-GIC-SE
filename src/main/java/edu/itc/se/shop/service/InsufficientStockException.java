package edu.itc.se.shop.service;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(long id, int currentStock, int attemptedDelta) {
        super("Product " + id + " has " + currentStock + " units, cannot remove " + Math.abs(attemptedDelta));
    }
}
