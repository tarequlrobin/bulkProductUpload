package com.tryneweveryweek.bulkupload.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private String sku;

    @Column(length = 1000)
    private String description;

    private Double price;

    private String imageFileName;
}
