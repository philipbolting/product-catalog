package de.philipbolting.product_catalog.brand;

public record BrandDTO(
        String slug,
        String name,
        String description) {

    static BrandDTO fromBrand(Brand brand) {
        return new BrandDTO(brand.getSlug(), brand.getName(), brand.getDescription());
    }

    public Brand toBrand() {
        return new Brand(slug, name, description);
    }
}
