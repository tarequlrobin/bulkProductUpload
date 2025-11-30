package com.tryneweveryweek.bulkupload.repository;

import com.tryneweveryweek.bulkupload.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
