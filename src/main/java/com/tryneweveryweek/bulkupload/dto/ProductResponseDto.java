package com.tryneweveryweek.bulkupload.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private String id;
    private String name;
    private String sku;
    private String description;
    private Double price;
}
