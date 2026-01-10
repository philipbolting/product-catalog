package de.philipbolting.product_catalog.brand;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/brands")
class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping()
    public ResponseEntity<?> createBrand(@Valid @RequestBody final BrandDTO request) {
        final var brand = brandService.createBrand(request);
        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slug}")
                .buildAndExpand(brand.getSlug())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{slug}")
    public BrandDTO findBrandBySlug(@PathVariable String slug) {
        return BrandDTO.fromBrand(brandService.findBrandBySlug(slug));
    }
}
