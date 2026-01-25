package de.philipbolting.product_catalog.product;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO request) {
        final var dto = productService.create(request);
        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slug}")
                .buildAndExpand(dto.slug())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{slug}")
    public ProductDTO findProductBySlug(@PathVariable String slug) {
        return productService.findProductBySlug(slug);
    }
}
