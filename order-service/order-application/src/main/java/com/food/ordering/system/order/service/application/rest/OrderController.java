package com.food.ordering.system.order.service.application.rest;

import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.application.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {} at restaurant: {}", createOrderCommand.getCustomerId(), createOrderCommand.getRestaurantId());

       var createOrderResponse =  orderApplicationService.crateOrder(createOrderCommand);
       log.info("Order create with trackingId: {}", createOrderResponse.getOrderTrackingId());
       return ResponseEntity.status(HttpStatus.CREATED).body(createOrderResponse);

    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId){
        var trackOrderResponse = orderApplicationService.trackOrder(TrackOrderQuery.builder().orderTrackingId(trackingId).build());
        log.info("Returning order status with trackingId: {}", trackOrderResponse.getOrderTrackingId());
        return ResponseEntity.ok(trackOrderResponse);

    }

}
