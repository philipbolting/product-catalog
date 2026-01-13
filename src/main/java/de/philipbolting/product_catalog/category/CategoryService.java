package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.NotFoundException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(CategoryDTO dto) {
        if (categoryRepository.findBySlug(dto.slug()).isPresent()) {
            throw new SlugAlreadyExistsException();
        }
        if (categoryRepository.findByName(dto.name()).isPresent()) {
            throw new NameAlreadyExistsException();
        }
        return categoryRepository.save(dto.toCategory());
    }

    public Category findCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug).orElseThrow(NotFoundException::new);
    }
}
