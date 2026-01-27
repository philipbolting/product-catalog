package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.NotFoundException;
import de.philipbolting.product_catalog.error.ParentCategoryNotFoundException;
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
    public CategoryDTO createCategory(CategoryDTO dto) {
        if (categoryTreeRepository.findBySlug(dto.slug()).isPresent()) {
            throw new SlugAlreadyExistsException();
        }
        final var category = dto.toCategory();
        final var parentSlug = extractParentSlug(dto.slug());
        if (parentSlug != null) {
            final var parentCategoryTree = categoryTreeRepository.findBySlug(parentSlug).orElseThrow(ParentCategoryNotFoundException::new);
            if (categoryTreeRepository.findByParentIdAndName(parentCategoryTree.getId(), dto.name()).isPresent()) {
                throw new NameAlreadyExistsException();
            }
            final var parentCategory = categoryRepository.findById(parentCategoryTree.getId()).orElseThrow(NotFoundException::new);
            category.setParent(parentCategory);
            final var slug = dto.slug().substring(parentSlug.length() + 1);
            category.setSlug(slug);
        }
        final var savedCategory = categoryRepository.save(category);
        final var fullSlug = parentSlug != null ? parentSlug + "/" + savedCategory.getSlug() : savedCategory.getSlug();
        return CategoryDTO.fromCategory(savedCategory).withSlug(fullSlug);
    }

    public CategoryDTO findCategoryBySlug(String slug) {
        final var categoryTreeInfo = categoryTreeRepository.findBySlug(slug).orElseThrow(NotFoundException::new);
        final var category = categoryRepository.findById(categoryTreeInfo.getId()).orElseThrow(NotFoundException::new);
        category.setSlug(categoryTreeInfo.getSlug());
        return CategoryDTO.fromCategory(category);
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
