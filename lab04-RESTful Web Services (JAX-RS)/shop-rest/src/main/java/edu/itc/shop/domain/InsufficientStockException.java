package edu.itc.shop.domain;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

public class InsufficientStockException extends ClientErrorException {
    public InsufficientStockException(long id, int current, int delta) {
        super("Insufficient stock for product " + id + ": current=" + current + ", delta=" + delta,
                Response.Status.CONFLICT);
    }
}
