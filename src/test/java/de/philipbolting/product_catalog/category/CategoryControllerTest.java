package de.philipbolting.product_catalog.category;

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

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private CategoryService categoryService;

    static Stream<String> validSlugs() {
        return Stream.of(
                "some-slug",
                "s",
                "s".repeat(100));
    }

    static Stream<String> invalidSlugs() {
        return Stream.of(
                "",
                "s".repeat(101),
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
    void createCategory_withoutSlug_shouldReturnBadRequest() {
        final var dto = new CategoryDTO(null, "Some Name", "Some Description");
        final var category = new CategoryDTO(null, "Some Name", "Some Description");
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
    void createCategory_withValidSlug_shouldReturnLocationOfCreatedCategory(String slug) {
        final var dto = new CategoryDTO(slug, "Some Name", "Some Description");
        final var category = new CategoryDTO(slug, "Some Name", "Some Description");
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
                .expectHeader().location("http://localhost/api/categories/" + slug);
    }

    @ParameterizedTest
    @MethodSource("invalidSlugs")
    void createCategory_withInvalidSlug_shouldReturnBadRequest(String slug) {
        final var dto = new CategoryDTO(slug, "Some Name", "Some Description");
        final var category = new CategoryDTO(slug, "Some Name", "Some Description");
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
    void createCategory_withoutName_shouldReturnBadRequest() {
        final var dto = new CategoryDTO("some-slug", null, "Some Description");
        final var category = new CategoryDTO("some-slug", null, "Some Description");
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
    void createCategory_withValidName_shouldReturnLocationOfCreatedCategory(String name) {
        final var dto = new CategoryDTO("some-slug", name, "Some Description");
        final var category = new CategoryDTO("some-slug", name, "Some Description");
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
                .expectHeader().location("http://localhost/api/categories/some-slug");
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void createCategory_withInvalidName_shouldReturnBadRequest(String name) {
        final var dto = new CategoryDTO("some-slug", name, "Some Description");
        final var category = new CategoryDTO("some-slug", name, "Some Description");
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
    void createCategory_withoutDescription_shouldReturnLocationOfCreatedCategory() {
        final var dto = new CategoryDTO("some-slug", "Some Name", null);
        final var category = new CategoryDTO("some-slug", "Some Name", null);
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
                .expectHeader().location("http://localhost/api/categories/some-slug");
    }

    @ParameterizedTest
    @MethodSource("validDescriptions")
    void createCategory_withValidDescription_shouldReturnLocationOfCreatedCategory(String description) {
        final var dto = new CategoryDTO("some-slug", "Some Name", description);
        final var category = new CategoryDTO("some-slug", "Some Name", description);
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
                .expectHeader().location("http://localhost/api/categories/some-slug");
    }

    @Test
    void createCategory_withInvalidDescription_shouldReturnBadRequest() {
        final var invalidDescription = "d".repeat(2001);
        final var dto = new CategoryDTO("some-slug", "Some Name", invalidDescription);
        final var category = new CategoryDTO("some-slug", "Some Name", invalidDescription);
        when(categoryService.createCategory(dto)).thenReturn(category);
        restTestClient.post().uri("/api/categories")
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
    void createCategory_withDuplicateSlug_shouldReturnBadRequest() {
        final var dto = new CategoryDTO("some-slug", "Some Name", "Some Description");
        when(categoryService.createCategory(dto)).thenThrow(new SlugAlreadyExistsException());
        restTestClient.post().uri("/api/categories")
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
    void createCategory_withDuplicateName_shouldReturnBadRequest() {
        final var dto = new CategoryDTO("some-slug", "Some Name", "Some Description");
        when(categoryService.createCategory(dto)).thenThrow(new NameAlreadyExistsException());
        restTestClient.post().uri("/api/categories")
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
    void findCategoryBySlug_withExistingSlug_shouldReturnCategory() {
        final String expectedSlug = "some-slug";
        final String expectedName = "Some Name";
        final String expectedDescription = "Some Description";
        final var category = new Category(expectedSlug, expectedName, expectedDescription);
        when(categoryService.findCategoryBySlug(expectedSlug)).thenReturn(category);
        restTestClient.get().uri("/api/categories/" + expectedSlug)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.slug").isEqualTo(expectedSlug)
                .jsonPath("$.name").isEqualTo(expectedName)
                .jsonPath("$.description").isEqualTo(expectedDescription);
    }

    @Test
    void findCategoryBySlug_withExistingNestedSlug_shouldReturnCategory() {
        final String expectedSlug = "some-parent-slug/some-child-slug";
        final String expectedName = "Some Name";
        final String expectedDescription = "Some Description";
        final var category = new Category(expectedSlug, expectedName, expectedDescription);
        when(categoryService.findCategoryBySlug(expectedSlug)).thenReturn(category);
        restTestClient.get().uri("/api/categories/" + expectedSlug)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.slug").isEqualTo(expectedSlug)
                .jsonPath("$.name").isEqualTo(expectedName)
                .jsonPath("$.description").isEqualTo(expectedDescription);
    }
}