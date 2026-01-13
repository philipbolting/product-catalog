package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureRestTestClient
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void createCategory() {
        final String uuid = UUID.randomUUID().toString();
        final var dto = new CategoryDTO(uuid, "Category Name " + uuid, "Category Description " + uuid);
        final var requestSentAt = Instant.now();
        restTestClient.post().uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated();

        final var category = categoryRepository.findBySlug(uuid).orElseThrow(NotFoundException::new);
        assertThat(category.getId()).isGreaterThan(0);
        assertThat(category.getName()).isEqualTo("Category Name " + uuid);
        assertThat(category.getDescription()).isEqualTo("Category Description " + uuid);
        assertThat(category.getCreated()).isBetween(requestSentAt, Instant.now());
        assertThat(category.getLastModified()).isEqualTo(category.getCreated());
    }
}
