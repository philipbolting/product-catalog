package de.philipbolting.product_catalog.category;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/categories")
class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping()
    public ResponseEntity<?> createCategory(@Valid @RequestBody final CategoryDTO request) {
        final var dto = categoryService.createCategory(request);
        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slug}")
                .buildAndExpand(dto.slug())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{*slugWithLeadingSlash}")
    public CategoryDTO findCategoryBySlug(@PathVariable String slugWithLeadingSlash) {
        final var slug = slugWithLeadingSlash.substring(1);
        return CategoryDTO.fromCategory(categoryService.findCategoryBySlug(slug));
    }
}
