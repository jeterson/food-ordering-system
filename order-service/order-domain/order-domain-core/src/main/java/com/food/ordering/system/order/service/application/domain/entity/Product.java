package com.food.ordering.system.order.service.application.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;

import java.util.Objects;

public class Product extends BaseEntity<ProductId> {
    private  String name;
    private Money price;

    public Product(ProductId id, String name, Money price) {
        this.setId(id);
        this.name = name;
        this.price = price;

    }

    public Product(ProductId productId) {
        super.setId(productId);
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public void updateConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}
