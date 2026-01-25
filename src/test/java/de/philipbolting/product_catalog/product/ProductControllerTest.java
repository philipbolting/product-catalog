package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.SecurityConfig;
import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
public class ProductControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private ProductService productService;

    static Stream<String> validSlugs() {
        return Stream.of(
                "some-slug",
                "s",
                "s".repeat(50));
    }

    static Stream<String> invalidSlugs() {
        return Stream.of(
                "",
                "s".repeat(51),
                "-",
                "--",
                "some--slug",
                " some-slug",
                "some slug",
                "some-slug ",
                "-some-slug",
                "some-slug-",
                "some_slug",
                "sOmE-sLuG",
                "søme-slûg",
                "슬러그-약간의",
                "/",
                "//",
                "//some-slug",
                "/some-slug",
                "/some-slug/",
                "/some-slug//",
                "some-/slug",
                "some/-slug"
        );
    }

    static Stream<String> validNames() {
        return Stream.of(
                "Some Name",
                "n",
                "n".repeat(50)
        );
    }

    static Stream<String> invalidNames() {
        return Stream.of(
                "",
                "n".repeat(51)
        );
    }

    static Stream<String> validDescriptions() {
        return Stream.of(
                "Some Description",
                "",
                "d",
                "d".repeat(50)
        );
    }

    @Test
    void createProduct_withoutBrandSlug_shouldReturnBadRequest() {
        final var dto = new ProductDTO("some-brand", "some-category", null, "Some Name", "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", null, "Some Name", "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/brandSlug");
    }

    @Test
    void createProduct_withoutCategorySlug_shouldReturnBadRequest() {
        final var dto = new ProductDTO("some-brand", "some-category", null, "Some Name", "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", null, "Some Name", "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/categorySlug");
    }

    @Test
    void createProduct_withoutSlug_shouldReturnBadRequest() {
        final var dto = new ProductDTO("some-brand", "some-category", null, "Some Name", "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", null, "Some Name", "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/slug");
    }

    @ParameterizedTest
    @MethodSource("validSlugs")
    void createProduct_withValidSlug_shouldReturnLocationOfCreatedProduct(String slug) {
        final var dto = new ProductDTO("some-brand", "some-category", slug, "Some Name", "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", slug, "Some Name", "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "%s",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """.formatted(slug))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/products/" + slug);
    }

    @ParameterizedTest
    @MethodSource("invalidSlugs")
    void createProduct_withInvalidSlug_shouldReturnBadRequest(String slug) {
        final var dto = new ProductDTO("some-brand", "some-category", slug, "Some Name", "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", slug, "Some Name", "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "%s",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """.formatted(slug))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/slug");
    }

    @Test
    void createProduct_withoutName_shouldReturnBadRequest() {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", null, "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", null, "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "description": "Some Description"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/name");
    }

    @ParameterizedTest
    @MethodSource("validNames")
    void createProduct_withValidName_shouldReturnLocationOfCreatedProduct(String name) {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", name, "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", name, "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "%s",
                            "description": "Some Description"
                        }
                        """.formatted(name))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/products/some-slug");
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void createProduct_withInvalidName_shouldReturnBadRequest(String name) {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", name, "Some Description");
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", name, "Some Description");
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "%s",
                            "description": "Some Description"
                        }
                        """.formatted(name))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/name");
    }

    @Test
    void createProduct_withoutDescription_shouldReturnLocationOfCreatedProduct() {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", null);
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", null);
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "Some Name"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/products/some-slug");
    }

    @ParameterizedTest
    @MethodSource("validDescriptions")
    void createProduct_withValidDescription_shouldReturnLocationOfCreatedProduct(String description) {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", description);
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", description);
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "%s"
                        }
                        """.formatted(description))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/products/some-slug");
    }

    @Test
    void createProduct_withInvalidDescription_shouldReturnBadRequest() {
        final var invalidDescription = "d".repeat(2001);
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", invalidDescription);
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", invalidDescription);
        when(productService.create(dto)).thenReturn(product);
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "%s"
                        }
                        """.formatted(invalidDescription))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo("#/description");
    }

    @Test
    void createProduct_withDuplicateSlug_shouldReturnBadRequest() {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", "Some Description");
        when(productService.create(dto)).thenThrow(new SlugAlreadyExistsException());
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isEqualTo("Slug already exists")
                .jsonPath("$.errors[0].pointer").isEqualTo("#/slug");
    }

    @Test
    void createProduct_withDuplicateName_shouldReturnBadRequest() {
        final var dto = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", "Some Description");
        when(productService.create(dto)).thenThrow(new NameAlreadyExistsException());
        restTestClient.post().uri("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "brandSlug": "some-brand",
                            "categorySlug": "some-category",
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isEqualTo("Name already exists")
                .jsonPath("$.errors[0].pointer").isEqualTo("#/name");
    }

    @Test
    void findProductBySlug_withExistingSlug_shouldReturnProduct() {
        final String expectedSlug = "some-slug";
        final String expectedName = "Some Name";
        final String expectedDescription = "Some Description";
        final var product = new ProductDTO("some-brand", "some-category", "some-slug", "Some Name", "Some Description");
        when(productService.findProductBySlug(expectedSlug)).thenReturn(product);
        restTestClient.get().uri("/api/products/" + expectedSlug)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.slug").isEqualTo(expectedSlug)
                .jsonPath("$.name").isEqualTo(expectedName)
                .jsonPath("$.description").isEqualTo(expectedDescription);
    }
}
