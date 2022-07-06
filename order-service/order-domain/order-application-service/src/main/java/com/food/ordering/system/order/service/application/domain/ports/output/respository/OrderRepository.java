package com.food.ordering.system.order.service.application.domain.ports.output.respository;

import com.food.ordering.system.order.service.application.domain.entity.Order;
import com.food.ordering.system.order.service.application.domain.valueobject.TrackingId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order>  findByTrackingId(TrackingId trackingId);
}
