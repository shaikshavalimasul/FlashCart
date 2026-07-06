package com.flashcart.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private boolean flashSaleActive;
    private Double flashSalePrice;
}
