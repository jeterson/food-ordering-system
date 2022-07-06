package com.food.ordering.system.order.service.application.domain;

import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.application.domain.entity.Order;
import com.food.ordering.system.order.service.application.domain.entity.Restaurant;
import com.food.ordering.system.order.service.application.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.application.domain.exceptions.OrderDomainException;
import com.food.ordering.system.order.service.application.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.CustomerRepository;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.OrderRepository;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
public class OrderCreateHelper {
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    public OrderCreateHelper(OrderDomainService orderDomainService,
                                OrderRepository orderRepository,
                             CustomerRepository customerRepository,
                             RestaurantRepository restaurantRepository,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        var restaurant = checkRestaurant(createOrderCommand);
        var order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        var orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        saveOrder(order);
        log.info("Order is create wih id {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;

    }

    private Order saveOrder(Order order) {
        var orderResult = orderRepository.save(order);
        if(orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order saved with id {}", orderResult.getId().getValue());
        return orderResult;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        var restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        var restaurantOptional = restaurantRepository.findRestaurantInformation(restaurant);
        if(restaurantOptional.isEmpty()) {
            log.warn("Could not find restaurant with id: {}", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with id: " + createOrderCommand.getRestaurantId());
        }
        return restaurantOptional.get();
    }


    private void checkCustomer(UUID customerId) {
        var customer = customerRepository.findCustomer(customerId);
        if(customer.isEmpty()){
            log.warn("Could not find customer with id: {}", customerId);
            throw  new OrderDomainException("Could not find customer with id: " + customerId);
        }
    }
}
