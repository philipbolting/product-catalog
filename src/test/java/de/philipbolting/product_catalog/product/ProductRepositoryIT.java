package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.JpaConfig;
import de.philipbolting.product_catalog.brand.Brand;
import de.philipbolting.product_catalog.brand.BrandRepository;
import de.philipbolting.product_catalog.category.Category;
import de.philipbolting.product_catalog.category.CategoryRepository;
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
class ProductRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private DateTimeProvider dateTimeProvider;

    @MockitoSpyBean
    private AuditingHandler handler;

    @PersistenceContext
    private EntityManager entityManager;

    private Brand testBrand;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        handler.setDateTimeProvider(dateTimeProvider);
        final var uuid = UUID.randomUUID();
        testBrand = brandRepository.save(new Brand("brand-" + uuid, "Brand " + uuid, "Brand description " + uuid));
        testCategory = categoryRepository.save(new Category("category-" + uuid, "Category " + uuid, "Category description " + uuid));
    }

    @Test
    void shouldSetCreatedAndUpdateLastModified() {
        final var uuid = UUID.randomUUID();
        final var createdAt = Instant.parse("2026-01-02T03:04:05Z");
        final var modifiedAt = Instant.parse("2027-02-03T04:05:06Z");
       final var product = new Product(testBrand, testCategory, "product-" + uuid, "Product " + uuid, "Product description " + uuid);
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(createdAt));
        final var savedProduct = productRepository.save(product);
        assertEquals(createdAt, savedProduct.getCreated());
        assertEquals(createdAt, savedProduct.getLastModified());

        savedProduct.setName("Modified " + modifiedAt);
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(modifiedAt));
        final var modifiedProduct = productRepository.save(savedProduct);
        entityManager.flush(); // required to update lastModified
        assertEquals(createdAt, modifiedProduct.getCreated());
        assertEquals(modifiedAt, modifiedProduct.getLastModified());
    }

    @Test
    void save_withDuplicateSlug_throwsConstraintViolationException() {
        final var duplicateSlug = UUID.randomUUID().toString();
        final var product1 =  new Product(testBrand, testCategory, duplicateSlug, "Product 1", "");
        final var product2 =  new Product(testBrand, testCategory, duplicateSlug, "Product 2", "");
        productRepository.save(product1);
        final var ex = assertThrowsExactly(ConstraintViolationException.class, () -> {
            productRepository.save(product2);
            entityManager.flush();
        });
        assertEquals(ConstraintViolationException.ConstraintKind.UNIQUE, ex.getKind());
        assertEquals("product_slug_key", ex.getConstraintName());
        assertThat(ex.getMessage()).contains(duplicateSlug);
    }

    @Test
    void save_withUniqueSlug_doesNotThrowException() {
        final var product1 =  new Product(testBrand, testCategory,"slug-1", "Product 1", "");
        final var product2 =  new Product(testBrand, testCategory,"slug-2", "Product 2", "");
        productRepository.save(product1);
        assertDoesNotThrow(() -> {
            productRepository.save(product2);
            entityManager.flush();
        });
    }

    @Test
    void save_withDuplicateName_throwsConstraintViolationException() {
        final var duplicateName = UUID.randomUUID().toString();
        final var product1 =  new Product(testBrand, testCategory,"slug-1", duplicateName, "");
        final var product2 =  new Product(testBrand, testCategory,"slug-2", duplicateName, "");
        productRepository.save(product1);
        final var ex = assertThrowsExactly(ConstraintViolationException.class, () -> {
            productRepository.save(product2);
            entityManager.flush();
        });
        assertEquals(ConstraintViolationException.ConstraintKind.UNIQUE, ex.getKind());
        assertEquals("product_name_key", ex.getConstraintName());
        assertThat(ex.getMessage()).contains(duplicateName);
    }

    @Test
    void save_withUniqueName_doesNotThrowException() {
        final var product1 =  new Product(testBrand, testCategory,"slug-1", "Product 1", "");
        final var product2 =  new Product(testBrand, testCategory,"slug-2", "Product 2", "");
        productRepository.save(product1);
        assertDoesNotThrow(() -> {
            productRepository.save(product2);
            entityManager.flush();
        });
    }
}