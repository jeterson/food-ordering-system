package com.food.ordering.system.order.service.application.domain;

import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.application.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderCreatedCommandHandler orderCreatedCommandHandler;
    private final OrderTrackCommandHandler orderTrackCommandHandler;

    public OrderApplicationServiceImpl(OrderCreatedCommandHandler orderCreatedCommandHandler, OrderTrackCommandHandler orderTrackCommandHandler) {
        this.orderCreatedCommandHandler = orderCreatedCommandHandler;
        this.orderTrackCommandHandler = orderTrackCommandHandler;
    }

    @Override
    public CreateOrderResponse crateOrder(CreateOrderCommand createOrderCommand) {
        return orderCreatedCommandHandler.createOrder(createOrderCommand);
    }

    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return orderTrackCommandHandler.trackOrder(trackOrderQuery);
    }
}
