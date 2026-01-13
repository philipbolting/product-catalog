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
        final var category = categoryService.createCategory(request);
        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{slug}")
                .buildAndExpand(category.getSlug())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{slug}")
    public CategoryDTO findCategoryBySlug(@PathVariable String slug) {
        return CategoryDTO.fromCategory(categoryService.findCategoryBySlug(slug));
    }
}
