package de.philipbolting.product_catalog.category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void createCategory_withDuplicateSlug_shouldThrowException() {
        final var dto = new CategoryDTO("some-slug", "Some Category", "Some description");
        when(categoryRepository.findBySlug(dto.slug())).thenReturn(Optional.of(dto.toCategory()));
        assertThrows(CategorySlugAlreadyExistsException.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void createCategory_withDuplicateName_shouldThrowException() {
        final var dto = new CategoryDTO("some-slug", "Some Category", "Some description");
        when(categoryRepository.findByName(dto.name())).thenReturn(Optional.of(dto.toCategory()));
        assertThrows(CategoryNameAlreadyExistsException.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void createCategory_withUniqueSlugAndName_shouldReturnCategory() {
        final var dto = new CategoryDTO("some-slug", "Some Category", "Some description");
        when(categoryRepository.findBySlug(dto.slug())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(dto.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(dto.toCategory())).thenReturn(dto.toCategory());
        var category = categoryService.createCategory(dto);
        assertNotNull(category);
        assertEquals(dto.slug(), category.getSlug());
        assertEquals(dto.name(), category.getName());
        assertEquals(dto.description(), category.getDescription());
    }

    @Test
    void findCategoryBySlug_withExistingSlug_shouldReturnCategory() {
        final var dto = new CategoryDTO("some-slug", "Some Category", "Some description");
        when(categoryRepository.findBySlug(dto.slug())).thenReturn(Optional.of(dto.toCategory()));
        var category = categoryService.findCategoryBySlug(dto.slug());
        assertNotNull(category);
        assertEquals(dto.slug(), category.getSlug());
        assertEquals(dto.name(), category.getName());
        assertEquals(dto.description(), category.getDescription());
    }

    @Test
    void findCategoryBySlug_withUnknownSlug_shouldThrowCategoryNotFoundException() {
        when(categoryRepository.findBySlug("some-slug")).thenThrow(new CategoryNotFoundException());
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findCategoryBySlug("some-slug"));
    }
}