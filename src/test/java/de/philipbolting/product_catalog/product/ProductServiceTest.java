package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.brand.Brand;
import de.philipbolting.product_catalog.brand.BrandRepository;
import de.philipbolting.product_catalog.category.Category;
import de.philipbolting.product_catalog.category.CategoryRepository;
import de.philipbolting.product_catalog.category.CategoryTree;
import de.philipbolting.product_catalog.category.CategoryTreeRepository;
import de.philipbolting.product_catalog.error.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryTreeRepository categoryTreeRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createProduct_withDuplicteSlug_shouldThrowException() {
        final var duplicateSlug = "some-product";
        final var someBrand = new Brand("some-brand", "Some Brand", "Some brand description");
        final var someCategory = new Category("some-category", "Some Category", "Some category description");
        final var duplicateProduct = new Product(someBrand, someCategory, duplicateSlug, "Some Product", "Some product description");
        final var dto = new ProductDTO("some-brand", "some-category", duplicateSlug, "Some Product", "Some product description");
        when(productRepository.findBySlug(duplicateSlug)).thenReturn(Optional.of(duplicateProduct));
        assertThrows(SlugAlreadyExistsException.class, () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_withDuplicteName_shouldThrowException() {
        final var duplicateName = "Some Product";
        final var someBrand = new Brand("some-brand", "Some Brand", "Some brand description");
        final var someCategory = new Category("some-category", "Some Category", "Some category description");
        final var duplicateProduct = new Product(someBrand, someCategory, "some-slug", duplicateName, "Some product description");
        final var dto = new ProductDTO("some-brand", "some-category", "some-product", duplicateName, "Some product description");
        when(productRepository.findByName(duplicateName)).thenReturn(Optional.of(duplicateProduct));
        assertThrows(NameAlreadyExistsException.class, () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_withInvalidBrandSlug_shouldThrowException() {
        final var brandSlug = "some-brand";
        final var dto = new ProductDTO(brandSlug, "some-category", "some-product", "Some Product", "Some product description");
        when(productRepository.findBySlug("some-product")).thenReturn(Optional.empty());
        when(productRepository.findByName("Some Product")).thenReturn(Optional.empty());
        when(brandRepository.findBySlug(brandSlug)).thenReturn(Optional.empty());
        assertThrows(ProductBrandSlugNotFoundException.class, () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_withInvalidCategorySlug_shouldThrowException() {
        final var categorySlug = "some-category";
        final var someBrand = new Brand("some-brand", "Some Brand", "Some brand description");
        final var dto = new ProductDTO("some-brand", "some-category", "some-product", "Some Product", "Some product description");
        when(productRepository.findBySlug("some-product")).thenReturn(Optional.empty());
        when(productRepository.findByName("Some Product")).thenReturn(Optional.empty());
        when(brandRepository.findBySlug("some-brand")).thenReturn(Optional.of(someBrand));
        when(categoryTreeRepository.findBySlug(categorySlug)).thenReturn(Optional.empty());
        assertThrows(ProductCategorySlugNotFoundException.class, () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_withValidParams_shouldReturnProduct() {
        final var someBrand = new Brand("some-brand", "Some Brand", "Some brand description");
        final var someCategory = new Category("some-category", "Some Category", "Some category description");
        final var someCategoryTree = new CategoryTree(456L, 123L, "Some Category", "some-category", "001001", 1);
        final var someProduct = new Product(someBrand, someCategory, "some-product", "Some Product", "Some product description");
        final var productDto = new ProductDTO("some-brand", "some-parent-category/some-category", "some-product", "Some Product", "Some product description");
        when(productRepository.findBySlug("some-product")).thenReturn(Optional.empty());
        when(productRepository.findByName("Some Product")).thenReturn(Optional.empty());
        when(brandRepository.findBySlug("some-brand")).thenReturn(Optional.of(someBrand));
        when(categoryTreeRepository.findBySlug("some-parent-category/some-category")).thenReturn(Optional.of(someCategoryTree));
        when(categoryRepository.findById(456L)).thenReturn(Optional.of(someCategory));
        when(productRepository.save(someProduct)).thenReturn(someProduct);
        final var savedProduct = productService.createProduct(productDto);
        assertNotNull(savedProduct);
        assertEquals("some-brand", savedProduct.brandSlug());
        assertEquals("some-category", savedProduct.categorySlug());
    }

    @Test
    void findProductBySlug_withExistingSlug_shouldReturnProduct() {
        final var someBrand = new Brand("some-brand", "Some Brand", "Some brand description");
        final var someCategory = new Category("some-category", "Some Category", "Some category description");
        someCategory.setId(456L);
        final var someCategoryTree = new CategoryTree(456L, 123L, "Some Category", "some-category", "000", 0);
        final var someProduct = new Product(someBrand, someCategory, "some-product", "Some Product", "Some product description");
        when(productRepository.findBySlug("some-product")).thenReturn(Optional.of(someProduct));
        when(categoryTreeRepository.findById(456L)).thenReturn(Optional.of(someCategoryTree));
        final var product = productService.findProductBySlug("some-product");
        assertNotNull(product);
        assertEquals("some-brand", product.brandSlug());
        assertEquals("some-category", product.categorySlug());
        assertEquals("some-product", product.slug());
        assertEquals("Some Product", product.name());
        assertEquals("Some product description", product.description());
    }

}
