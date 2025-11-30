package com.tryneweveryweek.bulkupload.dto;

import com.tryneweveryweek.bulkupload.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDto {
    private String id;
    private String name;
    private String sku;
    private String description;
    private Double price;
    private String imageUrl;

    public ProductResponseDto(Product product, String imageUrl) {
        this.id = product.getId();
        this.name = product.getName();
        this.sku = product.getSku();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imageUrl = imageUrl;
    }
}
