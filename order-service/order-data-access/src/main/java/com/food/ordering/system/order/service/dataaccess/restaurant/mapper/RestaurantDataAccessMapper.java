package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.application.domain.entity.Product;
import com.food.ordering.system.order.service.application.domain.entity.Restaurant;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.expcetion.RestaurantDataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant){
        return restaurant.getProducts().stream().map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities){
        var restaurantEntity = restaurantEntities.stream()
                .findFirst().orElseThrow(() -> new RestaurantDataAccessException("restaurant could not be found"));

        List<Product> products = restaurantEntities.stream().map(p ->
                new Product(new ProductId(p.getProductId()), p.getProductName(), new Money(p.getProductPrice())))
                .collect(Collectors.toList());

        return Restaurant.builder()
                .id(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(products)
                .active(restaurantEntity.getRestaurantActive())
                .build();
    }
}
