package de.philipbolting.product_catalog.brand;

public class BrandNameAlreadyExistsException extends RuntimeException {
    public BrandNameAlreadyExistsException() {
        super("Brand name already exists");
    }
}
