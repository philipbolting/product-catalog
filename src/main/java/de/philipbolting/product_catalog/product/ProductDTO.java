package de.philipbolting.product_catalog.product;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductDTO(
        @NotEmpty(message = "Brand slug must not be empty")
        @Size(max = 50, message = "Brand slug must not be longer than {max} chars")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String brandSlug,
        @NotEmpty(message = "Category slug must not be empty")
        @Size(max = 50, message = "Category slug must not be longer than {max} chars")
        @Pattern(regexp = "^[a-z0-9]+(?:[-/][a-z0-9]+)*$")
        String categorySlug,
        @NotEmpty(message = "Slug must not be empty")
        @Size(max = 50, message = "Slug must not be longer than {max} chars")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String slug,
        @NotEmpty(message = "Name must not be empty")
        @Size(max = 50, message = "Name must not be longer than {max} chars")
        String name,
        @Size(max = 2000, message = "Description must not be longer than {max} chars")
        String description
        ) {
}
