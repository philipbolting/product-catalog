package de.philipbolting.product_catalog.category;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByName(String slug);
}
