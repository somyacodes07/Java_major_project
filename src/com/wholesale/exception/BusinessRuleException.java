package com.wholesale.exception;

/**
 * Custom Exception thrown when a business rule or validation condition is violated
 * (e.g. credit limit exceeded, below minimum order quantity, empty fields).
 */
public class BusinessRuleException extends Exception {
    public BusinessRuleException(String message) {
        super(message);
    }
}
