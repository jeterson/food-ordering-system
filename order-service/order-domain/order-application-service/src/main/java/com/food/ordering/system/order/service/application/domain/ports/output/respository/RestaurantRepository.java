package com.food.ordering.system.order.service.application.domain.ports.output.respository;

import com.food.ordering.system.order.service.application.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

   Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
