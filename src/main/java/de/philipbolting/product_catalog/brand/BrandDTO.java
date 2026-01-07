package de.philipbolting.product_catalog.brand;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BrandDTO(
        @NotEmpty(message = "Slug must not be empty")
        @Size(max = 50, message = "Slug must not be longer than {max} chars")
        @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
        String slug,
        @NotEmpty(message = "Slug must not be empty")
        @Size(max = 50, message = "Slug must not be longer than {max} chars")
        String name,
        @Size(max = 2000, message = "Description must not be longer than {max} chars")
        String description) {

    static BrandDTO fromBrand(Brand brand) {
        return new BrandDTO(brand.getSlug(), brand.getName(), brand.getDescription());
    }

    public Brand toBrand() {
        return new Brand(slug, name, description);
    }
}
