package de.philipbolting.product_catalog;

import de.philipbolting.product_catalog.brand.BrandNameAlreadyExistsException;
import de.philipbolting.product_catalog.brand.BrandSlugAlreadyExistsException;
import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    private record ErrorDetail(String detail, String pointer) {}

    @ExceptionHandler(BrandSlugAlreadyExistsException.class)
    ProblemDetail handleBrandSlugAlreadyExistsException(BrandSlugAlreadyExistsException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Slug already exists", "#/slug")));
        return pd;
    }

    @ExceptionHandler(BrandNameAlreadyExistsException.class)
    ProblemDetail handleBrandNameAlreadyExistsException(BrandNameAlreadyExistsException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Name already exists", "#/name")));
        return pd;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        super.handleMethodArgumentNotValid(ex, headers, status, request);
        List<ErrorDetail> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError fe) {
                errors.add(new ErrorDetail(fe.getDefaultMessage(), "#/" + fe.getField()));
            } else {
                errors.add(new ErrorDetail(error.getDefaultMessage(), error.getObjectName()));
            }
        });
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }
}
