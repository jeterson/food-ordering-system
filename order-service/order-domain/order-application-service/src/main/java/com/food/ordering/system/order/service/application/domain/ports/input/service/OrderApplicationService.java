package com.food.ordering.system.order.service.application.domain.ports.input.service;

import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.application.domain.dto.track.TrackOrderResponse;

import javax.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse crateOrder(@Valid CreateOrderCommand createOrderCommand);
    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
