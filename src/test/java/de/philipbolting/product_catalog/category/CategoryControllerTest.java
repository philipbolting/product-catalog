package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.SecurityConfig;
import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

    @ParameterizedTest
    @MethodSource("validCategoryDTOs")
    void createCategory_withValidDTO_shouldReturnLocationOfCreatedCategory(CategoryDTO dto) {
        when(categoryService.createCategory(dto)).thenReturn(dto.toCategory());
        restTestClient.post().uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/categories/" + dto.slug());
    }

    static Stream<Arguments> validCategoryDTOs() {
        return Stream.of(
                Arguments.of(new CategoryDTO("some-slug", "Some Name" , "Some Description")),
                Arguments.of(new CategoryDTO("s", "n" , "d")),
                Arguments.of(new CategoryDTO("s".repeat( 50), "n".repeat( 50) , "d".repeat( 2000))),
                Arguments.of(new CategoryDTO("s", "n" , "")),
                Arguments.of(new CategoryDTO("s", "n" , null))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCategoryDTOs")
    void createCategory_withInvalidDTO_shouldReturnBadRequest(CategoryDTO dto, String pointer) {
        when(categoryService.createCategory(dto)).thenReturn(dto.toCategory());
        restTestClient.post().uri("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isNotEmpty()
                .jsonPath("$.errors[0].pointer").isEqualTo(pointer);
    }

    static Stream<Arguments> invalidCategoryDTOs() {
        return Stream.of(
                // invalid slug
                Arguments.of(new CategoryDTO(null, "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("s".repeat(51), "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("-", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("--", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("some--slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO(" some-slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("some slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("some-slug ", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("-some-slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("some-slug-", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("some_slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("sOmE-sLuG", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("søme-slûg", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new CategoryDTO("슬러그-약간의", "Some Name" , "Some Description"), "#/slug"),
                // invalid name
                Arguments.of(new CategoryDTO("some-slug", null , "Some Description"), "#/name"),
                Arguments.of(new CategoryDTO("some-slug", "" , "Some Description"), "#/name"),
                Arguments.of(new CategoryDTO("some-slug", "n".repeat(51) , "Some Description"), "#/name"),
                // invalid description
                Arguments.of(new CategoryDTO("some-slug", "Some Name", "d".repeat(2001)), "#/description")
        );
    }

    @Test
    void createCategory_withDuplicateSlug_shouldReturnBadRequest() {
        final var dto = new CategoryDTO("some-slug", "Some Name" , "Some Description");
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
        final var dto = new CategoryDTO("some-slug", "Some Name" , "Some Description");
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
}