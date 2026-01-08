package de.philipbolting.product_catalog.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BrandSlugAlreadyExistsException extends RuntimeException {
    public BrandSlugAlreadyExistsException() {
        super("Brand slug already exists");
    }
}
