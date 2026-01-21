package de.philipbolting.product_catalog.category;

import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.NotFoundException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
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
    @Mock
    private CategoryTreeRepository categoryTreeRepository;

    @Test
    void createCategory_withDuplicateSlug_shouldThrowException() {
        final var duplicateSlug = "some-parent-slug/some-child-slug";
        final var duplicateCategory = new CategoryTree(2L, 1L, "Existing Child Category", duplicateSlug, "001001", 1);
        final var dto = new CategoryDTO(duplicateSlug, "New Child Category", "Some description");
        when(categoryTreeRepository.findBySlug(duplicateSlug)).thenReturn(Optional.of(duplicateCategory));
        assertThrows(SlugAlreadyExistsException.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void createCategory_withDuplicateName_shouldThrowException() {
        final var duplicateName = "Some Child Category";
        final var parentCategory = new CategoryTree(1L, null, "Some Parent Category", "some-parent-slug", "001", 0);
        final var duplicateCategory = new CategoryTree(2L, 1L, duplicateName, parentCategory.getSlug() + "/existing-child-slug", "001001", 1);
        final var dto = new CategoryDTO(parentCategory.getSlug()  + "/new-child-slug", duplicateName, "Some description");
        when(categoryTreeRepository.findBySlug(dto.slug())).thenReturn(Optional.empty());
        when(categoryTreeRepository.findBySlug(parentCategory.getSlug() )).thenReturn(Optional.of(parentCategory));
        when(categoryTreeRepository.findByParentIdAndName(parentCategory.getId(), dto.name())).thenReturn(Optional.of(duplicateCategory));
        assertThrows(NameAlreadyExistsException.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void createCategory_withUniqueSlugAndName_shouldReturnCategory() {
        final var dto = new CategoryDTO("some-slug", "Some Category", "Some description");
        when(categoryTreeRepository.findBySlug("some-slug")).thenReturn(Optional.empty());
        when(categoryRepository.save(dto.toCategory())).thenReturn(dto.toCategory());
        var category = categoryService.createCategory(dto);
        assertNotNull(category);
        assertEquals("some-slug", category.getSlug());
        assertEquals("Some Category", category.getName());
        assertEquals("Some description", category.getDescription());
    }

    @Test
    void createCategory_withParentAndUniqueSlugAndName_shouldReturnCategory() {
        final var parentCategory = new CategoryTree(123L, null, "Some Parent Category", "some-parent-slug", "001", 0);
        final var dto = new CategoryDTO("some-parent-slug/some-child-slug", "Some Child Category", "Some child description");
        when(categoryTreeRepository.findBySlug("some-parent-slug/some-child-slug")).thenReturn(Optional.empty());
        when(categoryTreeRepository.findBySlug("some-parent-slug")).thenReturn(Optional.of(parentCategory));
        when(categoryTreeRepository.findByParentIdAndName(123L, "Some Child Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(dto.toCategory())).thenReturn(dto.toCategory());
        var category = categoryService.createCategory(dto);
        assertNotNull(category);
        assertEquals("some-parent-slug/some-child-slug", category.getSlug());
        assertEquals("Some Child Category", category.getName());
        assertEquals("Some child description", category.getDescription());
    }

    @Test
    void findCategoryBySlug_withExistingSlug_shouldReturnCategory() {
        final var parentCategory = new Category(null, 1, "some-parent-slug", "Some Parent Category", "Some parent description");
        final var childCategory = new Category(parentCategory, 1, "some-child-slug", "Some Child Category", "Some child description");
        final var categoryTreeInfo = new CategoryTree(2L, parentCategory.getId(), "Some Parent Category", "some-parent-slug/some-child-slug", "001", 0);
        when(categoryTreeRepository.findBySlug("some-parent-slug/some-child-slug")).thenReturn(Optional.of(categoryTreeInfo));
        when(categoryRepository.findById(categoryTreeInfo.getId())).thenReturn(Optional.of(childCategory));
        var category = categoryService.findCategoryBySlug("some-parent-slug/some-child-slug");
        assertNotNull(category);
        assertEquals("some-parent-slug/some-child-slug", category.getSlug());
        assertEquals("Some Child Category", category.getName());
        assertEquals("Some child description", category.getDescription());
    }

    @Test
    void findCategoryBySlug_withUnknownSlug_shouldThrowCategoryNotFoundException() {
        when(categoryTreeRepository.findBySlug("some-unknown-slug")).thenThrow(new NotFoundException());
        assertThrows(NotFoundException.class, () -> categoryService.findCategoryBySlug("some-unknown-slug"));
    }
}