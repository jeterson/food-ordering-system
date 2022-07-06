package com.food.ordering.system.order.service.application.domain.ports.output.respository;

import com.food.ordering.system.order.service.application.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Optional<Customer> findCustomer(UUID customerId);
}
