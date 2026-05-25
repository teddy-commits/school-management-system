package com.admas.management.modules.finance.validator;

import com.admas.management.modules.finance.dto.request.PaymentRequestDTO;
import com.admas.management.modules.finance.model.entity.Fee;
import com.admas.management.modules.finance.repository.FeeRepository;
import com.admas.management.modules.shared.model.User;
import com.admas.management.modules.shared.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final UserRepository userRepository;
    private final FeeRepository feeRepository;

    public List<String> validatePaymentRequest(PaymentRequestDTO request) {
        List<String> errors = new ArrayList<>();

        User student = userRepository.findById(request.getStudentId()).orElse(null);
        if (student == null) {
            errors.add("Student not found with ID: " + request.getStudentId());
        } else if (!student.isStudent()) {
            errors.add("User is not a student");
        }

        if (request.getFeeId() != null) {
            Fee fee = feeRepository.findById(request.getFeeId()).orElse(null);
            if (fee == null) {
                errors.add("Fee not found with ID: " + request.getFeeId());
            } else if (fee.getDueAmount() < request.getAmount()) {
                errors.add("Payment amount exceeds due amount. Due: " + fee.getDueAmount());
            }
        }

        if (request.getAmount() <= 0) {
            errors.add("Payment amount must be greater than 0");
        }
        if (request.getPaymentMethod() != null) {
            switch (request.getPaymentMethod()) {
                case BANK_TRANSFER:
                    if (request.getReferenceNumber() == null || request.getReferenceNumber().isEmpty()) {
                        errors.add("Reference number is required for bank transfer");
                    }
                    break;

                case CHECK:
                    if (request.getChequeNumber() == null || request.getChequeNumber().isEmpty()) {
                        errors.add("Cheque number is required for check payment");
                    }
                    if (request.getBankName() == null || request.getBankName().isEmpty()) {
                        errors.add("Bank name is required for check payment");
                    }
                    break;

                case MOBILE_MONEY:
                    if (request.getMobileNumber() == null || request.getMobileNumber().isEmpty()) {
                        errors.add("Mobile number is required for mobile money payment");
                    } else if (!request.getMobileNumber().matches("^[0-9]{10}$")) {
                        errors.add("Mobile number must be 10 digits");
                    }

                    if (request.getReferenceNumber() == null || request.getReferenceNumber().isEmpty()) {
                        errors.add("Transaction reference is required for mobile money");
                    }
                    break;

                case CREDIT_CARD:
                    if (request.getReferenceNumber() == null || request.getReferenceNumber().isEmpty()) {
                        errors.add("Transaction reference is required for card payment");
                    }
                    break;

                case CASH:
                    break;
            }
        }

        return errors;
    }



    public List<String> validateFeeWaiver(Long feeId, Double waiverAmount, String reason) {
        List<String> errors = new ArrayList<>();

        Fee fee = feeRepository.findById(feeId).orElse(null);
        if (fee == null) {
            errors.add("Fee not found with ID: " + feeId);
            return errors;
        }

        if (waiverAmount <= 0) {
            errors.add("Waiver amount must be greater than 0");
        }

        if (waiverAmount > fee.getDueAmount()) {
            errors.add("Waiver amount cannot exceed due amount. Due: " + fee.getDueAmount());
        }

        if (reason == null || reason.trim().isEmpty()) {
            errors.add("Reason for fee waiver is required");
        }

        if (reason != null && reason.length() < 5) {
            errors.add("Reason must be at least 5 characters");
        }

        return errors;
    }
}
