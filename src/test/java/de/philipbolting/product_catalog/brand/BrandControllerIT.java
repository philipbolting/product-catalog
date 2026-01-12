package de.philipbolting.product_catalog.brand;

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
public class BrandControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private BrandRepository brandRepository;

    @Test
    void createBrand() {
        final String uuid = UUID.randomUUID().toString();
        final var dto = new BrandDTO(uuid, "Brand Name " + uuid, "Brand Description " + uuid);
        final var requestSentAt = Instant.now();
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated();

        final var brand = brandRepository.findBySlug(uuid).orElseThrow(BrandNotFoundException::new);
        assertThat(brand.getId()).isGreaterThan(0);
        assertThat(brand.getName()).isEqualTo("Brand Name " + uuid);
        assertThat(brand.getDescription()).isEqualTo("Brand Description " + uuid);
        assertThat(brand.getCreated()).isBetween(requestSentAt, Instant.now());
        assertThat(brand.getLastModified()).isEqualTo(brand.getCreated());
    }
}
