package de.philipbolting.product_catalog.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BrandNameAlreadyExistsException extends RuntimeException {
    public BrandNameAlreadyExistsException() {
        super("Brand name already exists");
    }
}
