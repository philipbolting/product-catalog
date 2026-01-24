package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.brand.Brand;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {
    Optional<Brand> findBySlug(String slug);
    Optional<Brand> findByName(String name);
}
