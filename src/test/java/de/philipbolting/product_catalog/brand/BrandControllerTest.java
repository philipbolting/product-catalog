package de.philipbolting.product_catalog.brand;

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
    void createBrand_withInvalidDTO_shouldReturnBadRequest(BrandDTO dto) {
        when(brandService.createBrand(dto)).thenReturn(dto.toBrand());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    static Stream<Arguments> invalidBrandDTOs() {
        return Stream.of(
                // invalid slug
                Arguments.of(new BrandDTO(null, "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("s".repeat(51), "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("-", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("--", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("some--slug", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO(" some-slug", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("some slug", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("some-slug ", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("-some-slug", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("some-slug-", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("some_slug", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("sOmE-sLuG", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("søme-slûg", "Some Name" , "Some Description")),
                Arguments.of(new BrandDTO("슬러그-약간의", "Some Name" , "Some Description")),
                // invalid name
                Arguments.of(new BrandDTO("some-slug", null , "Some Description")),
                Arguments.of(new BrandDTO("some-slug", "" , "Some Description")),
                Arguments.of(new BrandDTO("some-slug", "n".repeat(51) , "Some Description")),
                // invalid description
                Arguments.of(new BrandDTO("some-slug", "Some Name", "d".repeat(2001)))
        );
    }

    @Test
    void createBrand_withDuplicateSlug_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", "Some Name" , "Some Description");
        when(brandService.createBrand(dto))
                .thenThrow(new BrandSlugAlreadyExistsException());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Brand slug already exists");
    }

    @Test
    void createBrand_withDuplicateName_shouldReturnBadRequest() {
        final var dto = new BrandDTO("some-slug", "Some Name" , "Some Description");
        when(brandService.createBrand(dto))
                .thenThrow(new BrandNameAlreadyExistsException());
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("Brand name already exists");
    }
}