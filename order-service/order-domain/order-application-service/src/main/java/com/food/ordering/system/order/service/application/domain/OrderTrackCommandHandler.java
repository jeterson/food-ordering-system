package com.food.ordering.system.order.service.application.domain;

import com.food.ordering.system.order.service.application.domain.exceptions.OrderNotFoundException;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.application.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.OrderRepository;
import com.food.ordering.system.order.service.application.domain.valueobject.TrackingId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    public OrderTrackCommandHandler(OrderDataMapper orderDataMapper, OrderRepository orderRepository) {
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
       var order = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
       if(order.isEmpty()){
           log.warn("Could not find order with trackingId ", trackOrderQuery.getOrderTrackingId());
           throw new OrderNotFoundException("Could not find order with trackingId " + trackOrderQuery.getOrderTrackingId());
       }
       return orderDataMapper.orderToTrackOrderResponse(order.get());
    }
}
