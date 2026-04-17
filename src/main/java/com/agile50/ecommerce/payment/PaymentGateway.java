package com.agile50.ecommerce.payment;

import java.math.BigDecimal;

@FunctionalInterface
public interface PaymentGateway {
    boolean charge(BigDecimal amount);
}
