package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.application.domain.entity.Order;
import com.food.ordering.system.order.service.application.domain.entity.OrderItem;
import com.food.ordering.system.order.service.application.domain.entity.Product;
import com.food.ordering.system.order.service.application.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.application.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.application.domain.valueobject.TrackingId;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .items(orderItemsToOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessage(order.getFailureMessages() != null ?
                        String.join(Order.FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()) : "")
                .build();

        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity order) {
        return Order.builder()
                .customerId(new CustomerId(order.getCustomerId()))
                .orderStatus(order.getOrderStatus())
                .orderId(new OrderId(order.getId()))
                .price(new Money(order.getPrice()))
                .restaurantId(new RestaurantId(order.getRestaurantId()))
                .trackingId(new TrackingId(order.getTrackingId()))
                .deliveryAddress(orderEntityAddressToDeliveryAddress(order.getAddress()))
                .items(orderItemEntitiesToOrderItems(order.getItems()))
                .failureMessages(order.getFailureMessage().isEmpty() ? new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(order.getFailureMessage().split(Order.FAILURE_MESSAGE_DELIMITER))))
                .build();
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        return items.stream().map(item -> OrderItem.builder()
                        .id(new OrderItemId(item.getId()))
                        .price(new Money(item.getPrice()))
                        .quantity(item.getQuantity())
                        .subTotal(new Money(item.getSubTotal()))
                        .product(new Product(new ProductId(item.getProductId())))
                        .build())
                .collect(Collectors.toList());
    }

    private StreetAddress orderEntityAddressToDeliveryAddress(OrderAddressEntity address) {
        return new StreetAddress(address.getId(),address.getStreet(), address.getPostalCode(), address.getCity());
    }


    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream().map(orderItem -> OrderItemEntity.builder()
                .id(orderItem.getId().getValue())
                .productId(orderItem.getProduct().getId().getValue())
                .price(orderItem.getPrice().getAmount())
                .quantity(orderItem.getQuantity())
                .subTotal(orderItem.getSubTotal().getAmount())
                .build()).collect(Collectors.toList());
    }

    private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .city(deliveryAddress.getCity())
                .postalCode(deliveryAddress.getPostalCode())
                .street(deliveryAddress.getStreet())
                .build();
    }
}
