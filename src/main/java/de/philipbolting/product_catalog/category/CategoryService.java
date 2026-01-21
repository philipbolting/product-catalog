package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.NotFoundException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryTreeRepository categoryTreeRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryTreeRepository categoryTreeRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryTreeRepository = categoryTreeRepository;
    }

    @Transactional
    public Category createCategory(CategoryDTO dto) {
        if (categoryTreeRepository.findBySlug(dto.slug()).isPresent()) {
            throw new SlugAlreadyExistsException();
        }
        final var parentSlug = extractParentSlug(dto.slug());
        if (parentSlug != null) {
            final var parentCategory = categoryTreeRepository.findBySlug(parentSlug).orElseThrow(NotFoundException::new);
            if (categoryTreeRepository.findByParentIdAndName(parentCategory.getId(), dto.name()).isPresent()) {
                throw new NameAlreadyExistsException();
            }
        }
        return categoryRepository.save(dto.toCategory());
    }

    public Category findCategoryBySlug(String slug) {
        final var categoryTreeInfo = categoryTreeRepository.findBySlug(slug).orElseThrow(NotFoundException::new);
        final var category = categoryRepository.findById(categoryTreeInfo.getId()).orElseThrow(NotFoundException::new);
        category.setSlug(categoryTreeInfo.getSlug());
        return category;
    }

    private String extractParentSlug(String slug) {
        final var indexOfLastSlash = slug.lastIndexOf('/');
        if (indexOfLastSlash == -1) {
            return null;
        }
        final var parentSlug =  slug.substring(0, indexOfLastSlash);
        return parentSlug.isEmpty() ? null : parentSlug;
    }
}
