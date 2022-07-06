package com.food.ordering.system.order.service.application.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.application.domain.entity.Customer;
import com.food.ordering.system.order.service.application.domain.entity.Order;
import com.food.ordering.system.order.service.application.domain.entity.Product;
import com.food.ordering.system.order.service.application.domain.entity.Restaurant;
import com.food.ordering.system.order.service.application.domain.exceptions.OrderDomainException;
import com.food.ordering.system.order.service.application.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.application.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.application.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.application.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.application.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.CustomerRepository;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.OrderRepository;
import com.food.ordering.system.order.service.application.domain.ports.output.respository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;

    private final UUID CUSTOMER_ID = UUID.fromString("299884e8-fc77-11ec-b939-0242ac120002");
    private final UUID RESTAURANT_ID = UUID.fromString("2ec15710-fc77-11ec-b939-0242ac120002");
    private final UUID PRODUCT_ID = UUID.fromString("31b5afd4-fc77-11ec-b939-0242ac120002");
    private final UUID ORDER_ID = UUID.fromString("34eea3ae-fc77-11ec-b939-0242ac120002");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init() {
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("155")
                        .city("Recife")
                        .build())
                .price(PRICE)
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                            ))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("155")
                        .city("Recife")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("155")
                        .city("Recife")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()
                ))
                .build();

        Customer customer = new Customer();
        customer.setId(new CustomerId(CUSTOMER_ID));

        Restaurant restaurant = Restaurant.builder()
                .id(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1",  new Money(new BigDecimal("50.00"))),
                                  new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .active(true)
                .build();

        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        order.setId(new OrderId(ORDER_ID));

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurant));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

    }

    @Test
    public void testCreateOrder(){
        var createOrderResponse = orderApplicationService.crateOrder(createOrderCommand);
        assertEquals(createOrderResponse.getOrderStatus(), OrderStatus.PENDING);
        assertEquals(createOrderResponse.getMessage(), "Order created successfully");
        assertNotNull(createOrderResponse.getOrderTrackingId());
    }
    @Test
    public void testCreateWrongTotalPrice() {
        var orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.crateOrder(createOrderCommandWrongPrice));
        assertEquals(orderDomainException.getMessage(), "Total price: 250.00 is not equal to Order items total: 200.00!");
    }

    @Test
    public void testCreateOrderWithWrongProductPrice() {
        var orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.crateOrder(createOrderCommandWrongProductPrice));
        assertEquals(orderDomainException.getMessage(), "Order item price: 60.00 is not valid");
    }

    @Test
    public void testCreateOrderWithPassiveRestaurant() {
        var restaurant = Restaurant.builder()
                .id(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1",  new Money(new BigDecimal("50.00"))),
                        new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50.00")))
                ))
                .active(false)
                .build();

        when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(createOrderCommand)))
                .thenReturn(Optional.of(restaurant));

       var orderDomainException = assertThrows(OrderDomainException.class, () -> orderApplicationService.crateOrder(createOrderCommand));
       assertEquals(orderDomainException.getMessage(), "Restaurant with id " + restaurant.getId() + " is currently not active");
    }
}
