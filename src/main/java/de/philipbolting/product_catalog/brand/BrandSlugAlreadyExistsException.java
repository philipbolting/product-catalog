package de.philipbolting.product_catalog.brand;

public class BrandSlugAlreadyExistsException extends RuntimeException {
    public BrandSlugAlreadyExistsException() {
        super("Brand slug already exists");
    }
}
