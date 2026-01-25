package de.philipbolting.product_catalog.product;

import de.philipbolting.product_catalog.brand.BrandRepository;
import de.philipbolting.product_catalog.category.CategoryRepository;
import de.philipbolting.product_catalog.category.CategoryTreeRepository;
import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.NotFoundException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final BrandRepository brandRepository;

    private final CategoryRepository categoryRepository;

    private final CategoryTreeRepository categoryTreeRepository;

    public ProductService(ProductRepository productRepository, BrandRepository brandRepository, CategoryRepository categoryRepository, CategoryTreeRepository categoryTreeRepository) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.categoryTreeRepository = categoryTreeRepository;
    }

    public ProductDTO create(ProductDTO dto) {
        if (productRepository.findBySlug(dto.slug()).isPresent()) {
            throw new SlugAlreadyExistsException();
        }
        if (productRepository.findByName(dto.name()).isPresent()) {
            throw new NameAlreadyExistsException();
        }
        final var brand = brandRepository.findBySlug(dto.brandSlug()).orElseThrow(NotFoundException::new);
        final var categoryTree = categoryTreeRepository.findBySlug(dto.categorySlug()).orElseThrow(NotFoundException::new);
        final var category = categoryRepository.findById(categoryTree.getId()).orElseThrow(NotFoundException::new);
        final var product = new Product(brand, category, dto.slug(), dto.name(), dto.description());
        final var savedProduct = productRepository.save(product);
        return new ProductDTO(brand.getSlug(), categoryTree.getSlug(), savedProduct.getSlug(), savedProduct.getName(), savedProduct.getDescription());
    }

    public ProductDTO findProductBySlug(String slug) {
        final var product = productRepository.findBySlug(slug).orElseThrow(NotFoundException::new);
        final var categoryTree = categoryTreeRepository.findById(product.getCategory().getId()).orElseThrow(NotFoundException::new);
        return new ProductDTO(product.getBrand().getSlug(), categoryTree.getSlug(), product.getSlug(), product.getName(), product.getDescription());
    }

}
