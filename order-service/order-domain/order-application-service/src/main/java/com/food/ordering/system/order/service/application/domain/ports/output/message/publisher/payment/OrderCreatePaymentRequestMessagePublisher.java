package com.food.ordering.system.order.service.application.domain.ports.output.message.publisher.payment;

import com.food.ordering.system.domain.event.publisher.DomainEventPublish;
import com.food.ordering.system.order.service.application.domain.event.OrderCreatedEvent;

public interface OrderCreatePaymentRequestMessagePublisher extends DomainEventPublish<OrderCreatedEvent> {
}
