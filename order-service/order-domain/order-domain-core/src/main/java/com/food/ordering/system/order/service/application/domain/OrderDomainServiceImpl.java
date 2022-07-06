package com.food.ordering.system.order.service.application.domain;

import com.food.ordering.system.order.service.application.domain.entity.Order;
import com.food.ordering.system.order.service.application.domain.entity.Product;
import com.food.ordering.system.order.service.application.domain.entity.Restaurant;
import com.food.ordering.system.order.service.application.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.application.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.application.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.application.domain.exceptions.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService{

    private static  final String UTC = "UTC";

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id {} is approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for order id {}", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id {} is cancelled", order.getId().getValue());
    }

    private void validateRestaurant(Restaurant  restaurant){
        if(!restaurant.isActive()){
            throw new OrderDomainException("Restaurant with id " + restaurant.getId() + " is currently not active");
        }

    }
    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        order.getItems().forEach(item -> {
            restaurant.getProducts().forEach(restaurantProduct -> {
                Product currentProduct = item.getProduct();
                if(currentProduct.equals(restaurantProduct)){
                    currentProduct.updateConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
                }
            });
        });
    }
}
