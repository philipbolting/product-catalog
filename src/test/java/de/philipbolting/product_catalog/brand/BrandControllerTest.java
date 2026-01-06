package de.philipbolting.product_catalog.brand;

import de.philipbolting.product_catalog.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = BrandController.class)
@AutoConfigureRestTestClient
@Import(SecurityConfig.class)
class BrandControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private BrandService brandService;

    @Test
    void createBrand_withValidParams_shouldReturnLocationOfCreatedBrand() {
        final var dto = new BrandDTO("one", "One", "Brand One");
        when(brandService.createBrand(dto)).thenReturn(new Brand(dto.slug(), dto.name(), dto.description()));
        restTestClient.post().uri("/api/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("http://localhost/api/brands/one");
    }
}