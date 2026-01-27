package de.philipbolting.product_catalog.error;

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

    @ExceptionHandler(SlugAlreadyExistsException.class)
    ProblemDetail handleSlugAlreadyExistsException(SlugAlreadyExistsException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Slug already exists", "#/slug")));
        return pd;
    }

    @ExceptionHandler(NameAlreadyExistsException.class)
    ProblemDetail handleNameAlreadyExistsException(NameAlreadyExistsException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Name already exists", "#/name")));
        return pd;
    }

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail handleNotFoundException(NotFoundException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setDetail("Not found");
        return pd;
    }

    @ExceptionHandler(ParentCategoryNotFoundException.class)
    ProblemDetail handleParentCategoryNotFoundException(ParentCategoryNotFoundException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Parent slug does not exist", "#/slug")));
        return pd;
    }

    @ExceptionHandler(ProductBrandSlugNotFoundException.class)
    ProblemDetail handleProductBrandSlugNotFoundException(ProductBrandSlugNotFoundException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Brand slug does not exist", "#/brandSlug")));
        return pd;
    }

    @ExceptionHandler(ProductCategorySlugNotFoundException.class)
    ProblemDetail handleProductCategorySlugNotFoundException(ProductCategorySlugNotFoundException e) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setProperty("errors", List.of(new ErrorDetail("Category slug does not exist", "#/categorySlug")));
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
