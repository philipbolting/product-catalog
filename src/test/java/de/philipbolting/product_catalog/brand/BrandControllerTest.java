package de.philipbolting.product_catalog.brand;

import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import de.philipbolting.product_catalog.SecurityConfig;
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

@WebMvcTest(controllers = BrandController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
class BrandControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private BrandService brandService;

    @ParameterizedTest
    @MethodSource("validBrandDTOs")
    void createBrand_withValidDTO_shouldReturnLocationOfCreatedBrand(BrandDTO dto) {
        when(brandService.createBrand(dto)).thenReturn(dto.toBrand());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/brands/" + dto.slug());
    }

    static Stream<Arguments> validBrandDTOs() {
        return Stream.of(
                Arguments.of(new BrandDTO("some-slug", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("s", "n" , "d")),
                Arguments.of(new BrandDTO("s".repeat( 50), "n".repeat( 50) , "d".repeat( 2000))),
                Arguments.of(new BrandDTO("s", "n" , "")),
                Arguments.of(new BrandDTO("s", "n" , null))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidBrandDTOs")
    void createBrand_withInvalidDTO_shouldReturnBadRequest(BrandDTO dto, String pointer) {
        when(brandService.createBrand(dto)).thenReturn(dto.toBrand());
        restTestClient.post().uri("/api/brands")
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

    static Stream<Arguments> invalidBrandDTOs() {
        return Stream.of(
                // invalid slug
                Arguments.of(new BrandDTO(null, "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("s".repeat(51), "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("-", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("--", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("some--slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO(" some-slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("some slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("some-slug ", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("-some-slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("some-slug-", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("some_slug", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("sOmE-sLuG", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("søme-slûg", "Some Name" , "Some Description"), "#/slug"),
                Arguments.of(new BrandDTO("슬러그-약간의", "Some Name" , "Some Description"), "#/slug"),
                // invalid name
                Arguments.of(new BrandDTO("some-slug", null , "Some Description"), "#/name"),
                Arguments.of(new BrandDTO("some-slug", "" , "Some Description"), "#/name"),
                Arguments.of(new BrandDTO("some-slug", "n".repeat(51) , "Some Description"), "#/name"),
                // invalid description
                Arguments.of(new BrandDTO("some-slug", "Some Name", "d".repeat(2001)), "#/description")
        );
    }

    @Test
    void createBrand_withDuplicateSlug_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", "Some Name" , "Some Description");
        when(brandService.createBrand(dto))
                .thenThrow(new SlugAlreadyExistsException());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isEqualTo("Slug already exists")
                .jsonPath("$.errors[0].pointer").isEqualTo("#/slug");
    }

    @Test
    void createBrand_withDuplicateName_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", "Some Name" , "Some Description");
        when(brandService.createBrand(dto))
                .thenThrow(new NameAlreadyExistsException());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors").isNotEmpty()
                .jsonPath("$.errors[0].detail").isEqualTo("Name already exists")
                .jsonPath("$.errors[0].pointer").isEqualTo("#/name");
    }

    @Test
    void findBrandBySlug_withExistingSlug_shouldReturnBrand() {
        final var dto = new BrandDTO("some-slug", "Some Name" , "Some Description");
        when(brandService.findBrandBySlug(dto.slug())).thenReturn(dto.toBrand());
        restTestClient.get().uri("/api/brands/" + dto.slug())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.slug").isEqualTo(dto.slug())
                .jsonPath("$.name").isEqualTo(dto.name())
                .jsonPath("$.description").isEqualTo(dto.description());
    }
}