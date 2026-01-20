package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@Import(JpaConfig.class)
@Testcontainers
class CategoryRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    CategoryRepository categoryRepository;

    @MockitoBean
    private DateTimeProvider dateTimeProvider;

    @MockitoSpyBean
    private AuditingHandler handler;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        handler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    void shouldSetCreatedAndUpdateLastModified() {
        final var uuid = UUID.randomUUID();
        final var createdAt = Instant.parse("2026-01-02T03:04:05Z");
        final var modifiedAt = Instant.parse("2027-02-03T04:05:06Z");

        final var category = new Category("slug-" + uuid, "Category " + uuid, "");
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(createdAt));
        final var savedCategory = categoryRepository.save(category);
        assertEquals(createdAt, savedCategory.getCreated());
        assertEquals(createdAt, savedCategory.getLastModified());

        savedCategory.setName("Modified " + modifiedAt);
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(modifiedAt));
        final var modifiedCategory = categoryRepository.save(savedCategory);
        entityManager.flush(); // required to update lastModified
        assertEquals(createdAt, modifiedCategory.getCreated());
        assertEquals(modifiedAt, modifiedCategory.getLastModified());
    }

    @Test
    void save_withDuplicateSlugInSameCategory_throwsConstraintViolationException() {
        final var duplicateSlug = UUID.randomUUID().toString();
        var parentCategory = new Category(1, "slug-parent", "Parent Category", "");
        var category1 = new Category(parentCategory, 1, duplicateSlug, "Child Category 1.1", "");
        var category2 = new Category(parentCategory,2, duplicateSlug, "Child Category 1.2", "");
        categoryRepository.save(parentCategory);
        categoryRepository.save(category1);
        final var ex = assertThrowsExactly(ConstraintViolationException.class, () -> {
            categoryRepository.save(category2);
            entityManager.flush();
        });
        assertEquals(ConstraintViolationException.ConstraintKind.UNIQUE, ex.getKind());
        assertEquals("category_parent_id_slug_key", ex.getConstraintName());
        assertThat(ex.getMessage()).contains(duplicateSlug);
    }

    @Test
    void save_withDuplicateSlugInDifferentCategories_doesNotThrowException() {
        final var duplicateSlug = UUID.randomUUID().toString();
        var parentCategory1 = new Category(1, "slug-parent-1", "Parent Category 1", "");
        var parentCategory2 = new Category(2, "slug-parent-2", "Parent Category 2", "");
        var category1 = new Category(parentCategory1, 1, duplicateSlug, "Child Category 1.1", "");
        var category2 = new Category(parentCategory2,2, duplicateSlug, "Child Category 2.1", "");
        categoryRepository.save(parentCategory1);
        categoryRepository.save(parentCategory2);
        categoryRepository.save(category1);
        assertDoesNotThrow(() -> {
            categoryRepository.save(category2);
            entityManager.flush();
        });
    }

    @Test
    void save_withDuplicateNameInSameCategory_throwsConstraintViolationException() {
        final var duplicateName = UUID.randomUUID().toString();
        var parentCategory = new Category(1, "slug-parent", "Parent Category", "");
        var category1 = new Category(parentCategory, 1, "slug-child-1", duplicateName, "");
        var category2 = new Category(parentCategory,2, "slug-child-2", duplicateName, "");
        categoryRepository.save(parentCategory);
        categoryRepository.save(category1);
        final var ex = assertThrowsExactly(ConstraintViolationException.class, () -> {
            categoryRepository.save(category2);
            entityManager.flush();
        });
        assertEquals(ConstraintViolationException.ConstraintKind.UNIQUE, ex.getKind());
        assertEquals("category_parent_id_name_key", ex.getConstraintName());
        assertThat(ex.getMessage()).contains(duplicateName);
    }

    @Test
    void save_withDuplicateNameInDifferentCategories_doesNotThrowException() {
        final var duplicateName = UUID.randomUUID().toString();
        var parentCategory1 = new Category(1, "slug-parent-1", "Parent Category 1", "");
        var parentCategory2 = new Category(2, "slug-parent-2", "Parent Category 2", "");
        var category1 = new Category(parentCategory1, 1, "slug-child-1", duplicateName, "");
        var category2 = new Category(parentCategory2,2, "slug-child-2", duplicateName, "");
        categoryRepository.save(parentCategory1);
        categoryRepository.save(parentCategory2);
        categoryRepository.save(category1);
        assertDoesNotThrow(() -> {
            categoryRepository.save(category2);
            entityManager.flush();
        });
    }
}