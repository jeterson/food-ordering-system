package com.food.ordering.system.order.service.application.domain.ports.output.message.publisher.restaurantapproval;

import com.food.ordering.system.domain.event.publisher.DomainEventPublish;
import com.food.ordering.system.order.service.application.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublish<OrderPaidEvent> {
}
