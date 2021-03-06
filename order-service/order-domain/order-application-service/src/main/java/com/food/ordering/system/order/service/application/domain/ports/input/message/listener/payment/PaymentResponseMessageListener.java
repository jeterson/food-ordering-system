package com.food.ordering.system.order.service.application.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.application.domain.dto.message.PaymentResponse;

public interface PaymentResponseMessageListener {

    void paymentCompleted(PaymentResponse paymentResponse);
    void paymentCancelled(PaymentResponse paymentResponse);
}
