package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.ContainersConfig;
import de.philipbolting.product_catalog.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureRestTestClient
@Import(ContainersConfig.class)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIT {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Sql("/db/init_brands.sql")
    @Sql("/db/init_categories.sql")
    void createProduct() {
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "slug": "some-product",
                            "brandSlug": "brand-1",
                            "categorySlug": "category-2/category-2-1",
                            "name": "Some Product",
                            "description": "Some product description"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated();

        final var product = productRepository.findBySlug("some-product").orElseThrow(NotFoundException::new);
        assertThat(product.getId()).isGreaterThan(0);
        assertThat(product.getSlug()).isEqualTo("some-product");
        assertThat(product.getBrand().getSlug()).isEqualTo("brand-1");
        assertThat(product.getCategory().getSlug()).isEqualTo("category-2-1");
        assertThat(product.getName()).isEqualTo("Some Product");
        assertThat(product.getDescription()).isEqualTo("Some product description");
    }
}
