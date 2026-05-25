package com.admas.management.modules.finance.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardPaymentRequestDTO {
    @NotNull(message = "Payment details are required")
    private PaymentRequestDTO payment;

    @NotNull(message = "Card number is required")
    private String cardNumber;

    @NotNull(message = "Expiry date is required")
    private String expiryDate;

    @NotNull(message = "CVV is required")
    private String cvv;

    @NotNull(message = "Card holder name is required")
    private String cardHolderName;
}
