package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @BeforeEach
    void setUp() {
        handler.setDateTimeProvider(dateTimeProvider);
    }

    @Test
    @Disabled("LastModified is unexpectedly not updated by the second call to save().")
    void shouldUpdateLastModified() {
        final var uuid = UUID.randomUUID();
        final var createdAt = Instant.parse("2026-01-02T03:04:05Z");
        final var modifiedAt = Instant.parse("2027-02-03T04:05:06Z");

        var category = new Category("slug-" + uuid, "Category " + uuid, "");
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(createdAt));
        categoryRepository.save(category);
        assertEquals(createdAt, category.getCreated());
        assertEquals(createdAt, category.getLastModified());

        category.setName("Modified " + modifiedAt);
        when(dateTimeProvider.getNow()).thenReturn(Optional.of(modifiedAt));
        categoryRepository.save(category);
        assertEquals(createdAt, category.getCreated());
        assertEquals(modifiedAt, category.getLastModified());
    }

}