package de.philipbolting.product_catalog.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@Testcontainers
class CategoryTreeRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    CategoryTreeRepository categoryTreeRepository;

    @Test
    @Sql("/db/init_categories.sql")
    void shouldFindAll() {
        final var tree = categoryTreeRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder"));

        assertEquals(7, tree.size());

        var actual = tree.getFirst();
        assertNull(actual.getParentId());
        assertEquals("category-1", actual.getSlug());
        assertEquals("Category 1", actual.getName());
        assertEquals("001", actual.getSortOrder());
        assertEquals(0, actual.getDepth());

        actual = tree.get(1);
        assertEquals(1, actual.getParentId());
        assertEquals("category-1/category-1-1", actual.getSlug());
        assertEquals("Category 1.1", actual.getName());
        assertEquals("001001", actual.getSortOrder());
        assertEquals(1, actual.getDepth());

        actual = tree.get(2);
        assertEquals(1, actual.getParentId());
        assertEquals("category-1/category-1-2", actual.getSlug());
        assertEquals("Category 1.2", actual.getName());
        assertEquals("001002", actual.getSortOrder());
        assertEquals(1, actual.getDepth());

        actual = tree.get(3);
        assertNull(actual.getParentId());
        assertEquals("category-2", actual.getSlug());
        assertEquals("Category 2", actual.getName());
        assertEquals("002", actual.getSortOrder());
        assertEquals(0, actual.getDepth());

        actual = tree.get(4);
        assertEquals(2, actual.getParentId());
        assertEquals("category-2/category-2-1", actual.getSlug());
        assertEquals("Category 2.1", actual.getName());
        assertEquals("002001", actual.getSortOrder());
        assertEquals(1, actual.getDepth());

        actual = tree.get(5);
        assertEquals(5, actual.getParentId());
        assertEquals("category-2/category-2-1/category-2-1-1", actual.getSlug());
        assertEquals("Category 2.1.1", actual.getName());
        assertEquals("002001001", actual.getSortOrder());
        assertEquals(2, actual.getDepth());

        actual = tree.get(6);
        assertEquals(5, actual.getParentId());
        assertEquals("category-2/category-2-1/category-2-1-2", actual.getSlug());
        assertEquals("Category 2.1.2", actual.getName());
        assertEquals("002001002", actual.getSortOrder());
        assertEquals(2, actual.getDepth());
    }

}