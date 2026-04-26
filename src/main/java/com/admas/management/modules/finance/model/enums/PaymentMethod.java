package com.admas.management.modules.finance.model.enums;

public enum PaymentMethod {
    CASH("Cash", "Physical cash payment"),
    BANK_TRANSFER("Bank Transfer", "Direct bank transfer"),
    CREDIT_CARD("Credit Card", "Credit/debit card payment"),
    MOBILE_MONEY("Mobile Money", "Mobile payment"),
    CHECK("Check", "Bank check payment"),
    ONLINE("Online", "Online payment gateway"),
    SCHOLARSHIP("Scholarship", "Scholarship funding");

    private final String method;
    private final String description;

    PaymentMethod(String method, String description) {
        this.method = method;
        this.description = description;
    }

    public String getMethod() { return method; }
    public String getDescription() { return description; }
}
