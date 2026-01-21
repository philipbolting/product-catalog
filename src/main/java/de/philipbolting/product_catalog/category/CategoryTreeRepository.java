package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.ReadOnlyRepository;

import java.util.Optional;

public interface CategoryTreeRepository extends ReadOnlyRepository<CategoryTree, Long> {
    Optional<CategoryTree> findBySlug(String slug);
    Optional<CategoryTree> findByParentIdAndName(Long parentId, String name);
}
