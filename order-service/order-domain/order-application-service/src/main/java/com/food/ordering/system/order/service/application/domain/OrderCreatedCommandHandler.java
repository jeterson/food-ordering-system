package com.food.ordering.system.order.service.application.domain;

import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.application.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.application.domain.ports.output.message.publisher.payment.OrderCreatePaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatePaymentRequestMessagePublisher orderCreatePaymentRequestMessagePublisher;

    public OrderCreatedCommandHandler(OrderCreateHelper orderCreateHelper, OrderDataMapper orderDataMapper,
                                      OrderCreatePaymentRequestMessagePublisher orderCreatePaymentRequestMessagePublisher) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatePaymentRequestMessagePublisher = orderCreatePaymentRequestMessagePublisher;
    }

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand){
        var orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order is create with id {}", orderCreatedEvent.getOrder().getId().getValue());
        orderCreatePaymentRequestMessagePublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order created successfully");
    }


}
