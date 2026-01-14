package de.philipbolting.product_catalog.brand;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface BrandRepository extends CrudRepository<Brand, Long> {
    Optional<Brand> findBySlug(String slug);
    Optional<Brand> findByName(String name);
}
