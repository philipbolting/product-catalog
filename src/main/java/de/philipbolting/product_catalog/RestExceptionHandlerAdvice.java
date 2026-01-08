package de.philipbolting.product_catalog;

import de.philipbolting.product_catalog.brand.BrandNameAlreadyExistsException;
import de.philipbolting.product_catalog.brand.BrandSlugAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BrandSlugAlreadyExistsException.class)
    ProblemDetail handleBrandSlugAlreadyExistsException(BrandSlugAlreadyExistsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BrandNameAlreadyExistsException.class)
    ProblemDetail handleBrandNameAlreadyExistsException(BrandNameAlreadyExistsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
