package de.philipbolting.product_catalog.brand;

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
class BrandRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    BrandRepository brandRepository;

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

        var brand = new Brand("slug-" + uuid, "Brand " + uuid, "");
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(createdAt));
        brandRepository.save(brand);
        entityManager.flush();
        assertEquals(createdAt, brand.getCreated());
        assertEquals(createdAt, brand.getLastModified());

        brand.setName("Modified " + modifiedAt);
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(modifiedAt));
        brandRepository.save(brand);
        entityManager.flush();
        assertEquals(createdAt, brand.getCreated());
        assertEquals(modifiedAt, brand.getLastModified());
    }

    @Test
    void save_withDuplicateSlug_throwsConstraintViolationException() {
        final var duplicateSlug = UUID.randomUUID().toString();
        final var brand1 =  new Brand(duplicateSlug, "Brand 1", "");
        final var brand2 =  new Brand(duplicateSlug, "Brand 2", "");
        brandRepository.save(brand1);
        final var ex = assertThrowsExactly(ConstraintViolationException.class, () -> {
            brandRepository.save(brand2);
            entityManager.flush();
        });
        assertEquals(org.hibernate.exception.ConstraintViolationException.ConstraintKind.UNIQUE, ex.getKind());
        assertEquals("brand_slug_key", ex.getConstraintName());
        assertThat(ex.getMessage()).contains(duplicateSlug);
    }

    @Test
    void save_withUniqueSlug_doesNotThrowException() {
        final var brand1 =  new Brand("slug-1", "Brand 1", "");
        final var brand2 =  new Brand("slug-2", "Brand 2", "");
        brandRepository.save(brand1);
        assertDoesNotThrow(() -> {
            brandRepository.save(brand2);
            entityManager.flush();
        });
    }

    @Test
    void save_withDuplicateName_throwsConstraintViolationException() {
        final var duplicateName = UUID.randomUUID().toString();
        final var brand1 =  new Brand("slug-1", duplicateName, "");
        final var brand2 =  new Brand("slug-2", duplicateName, "");
        brandRepository.save(brand1);
        final var ex = assertThrowsExactly(ConstraintViolationException.class, () -> {
            brandRepository.save(brand2);
            entityManager.flush();
        });
        assertEquals(org.hibernate.exception.ConstraintViolationException.ConstraintKind.UNIQUE, ex.getKind());
        assertEquals("brand_name_key", ex.getConstraintName());
        assertThat(ex.getMessage()).contains(duplicateName);
    }

    @Test
    void save_withUniqueName_doesNotThrowException() {
        final var brand1 =  new Brand("slug-1", "Brand 1", "");
        final var brand2 =  new Brand("slug-2", "Brand 2", "");
        brandRepository.save(brand1);
        assertDoesNotThrow(() -> {
            brandRepository.save(brand2);
            entityManager.flush();
        });
    }
}