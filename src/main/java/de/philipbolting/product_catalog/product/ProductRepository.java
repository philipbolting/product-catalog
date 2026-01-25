package de.philipbolting.product_catalog.product;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Product> findBySlug(String slug);
    Optional<Product> findByName(String name);
}
