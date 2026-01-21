package de.philipbolting.product_catalog.brand;

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

@WebMvcTest(controllers = BrandController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
class BrandControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private BrandService brandService;

    static Stream<String> validSlugs() {
        return Stream.of(
                "some-slug",
                "s",
                "s".repeat(50)
        );
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
                "슬러그-약간의"
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
    void createBrand_withoutSlug_shouldReturnBadRequest() {
        final var dto = new BrandDTO(null, "Some Name", "Some Description");
        final var brand = new Brand(null, "Some Name", "Some Description");
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void createBrand_withValidSlug_shouldReturnLocationOfCreatedBrand(String slug) {
        final var dto = new BrandDTO(slug, "Some Name", "Some Description");
        final var brand = new Brand(slug, "Some Name", "Some Description");
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "slug": "%s",
                            "name": "Some Name",
                            "description": "Some Description"
                        }
                        """.formatted(slug))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/brands/" + slug);
    }

    @ParameterizedTest
    @MethodSource("invalidSlugs")
    void createBrand_withInvalidSlug_shouldReturnBadRequest(String slug) {
        final var dto = new BrandDTO(slug, "Some Name", "Some Description");
        final var brand = new Brand(slug, "Some Name", "Some Description");
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void createBrand_withoutName_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", null, "Some Description");
        final var brand = new Brand("some-slug", null, "Some Description");
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void createBrand_withValidName_shouldReturnLocationOfCreatedBrand(String name) {
        final var dto = new BrandDTO("some-slug", name, "Some Description");
        final var brand = new Brand("some-slug", name, "Some Description");
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "slug": "some-slug",
                            "name": "%s",
                            "description": "Some Description"
                        }
                        """.formatted(name))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/brands/some-slug");
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void createBrand_withInvalidName_shouldReturnBadRequest(String name) {
        final var dto = new BrandDTO("some-slug", name, "Some Description");
        final var brand = new Brand("some-slug", name, "Some Description");
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void createBrand_withoutDescription_shouldReturnLocationOfCreatedBrand() {
        final var dto = new BrandDTO("some-slug", "Some Name", null);
        final var brand = new Brand("some-slug", "Some Name", null);
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "slug": "some-slug",
                            "name": "Some Name"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/brands/some-slug");
    }

    @ParameterizedTest
    @MethodSource("validDescriptions")
    void createBrand_withValidDescription_shouldReturnLocationOfCreatedBrand(String description) {
        final var dto = new BrandDTO("some-slug", "Some Name", description);
        final var brand = new Brand("some-slug", "Some Name", description);
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "slug": "some-slug",
                            "name": "Some Name",
                            "description": "%s"
                        }
                        """.formatted(description))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/brands/some-slug");
    }

    @Test
    void createBrand_withInvalidDescription_shouldReturnBadRequest() {
        final var invalidDescription = "d".repeat(2001);
        final var dto = new BrandDTO("some-slug", "Some Name", invalidDescription);
        final var brand = new Brand("some-slug", "Some Name", invalidDescription);
        when(brandService.createBrand(dto)).thenReturn(brand);
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void createBrand_withDuplicateSlug_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", "Some Name", "Some Description");
        when(brandService.createBrand(dto)).thenThrow(new SlugAlreadyExistsException());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void createBrand_withDuplicateName_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", "Some Name", "Some Description");
        when(brandService.createBrand(dto)).thenThrow(new NameAlreadyExistsException());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body("""
                        {
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
    void findBrandBySlug_withExistingSlug_shouldReturnBrand() {
        final var dto = new BrandDTO("some-slug", "Some Name", "Some Description");
        final var brand = new Brand("some-slug", "Some Name", "Some Description");
        when(brandService.findBrandBySlug("some-slug")).thenReturn(brand);
        restTestClient.get().uri("/api/brands/some-slug")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.slug").isEqualTo(dto.slug())
                .jsonPath("$.name").isEqualTo(dto.name())
                .jsonPath("$.description").isEqualTo(dto.description());
    }
}