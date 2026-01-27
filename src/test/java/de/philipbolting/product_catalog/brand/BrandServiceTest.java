package de.philipbolting.product_catalog.brand;

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
class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;
    @Mock
    private BrandRepository brandRepository;

    @Test
    void createBrand_withDuplicateSlug_shouldThrowException() {
        final var dto = new BrandDTO("some-slug", "Some Brand", "Some description");
        when(brandRepository.findBySlug(dto.slug())).thenReturn(Optional.of(dto.toBrand()));
        assertThrows(SlugAlreadyExistsException.class, () -> brandService.createBrand(dto));
    }

    @Test
    void createBrand_withDuplicateName_shouldThrowException() {
        final var dto = new BrandDTO("some-slug", "Some Brand", "Some description");
        when(brandRepository.findByName(dto.name())).thenReturn(Optional.of(dto.toBrand()));
        assertThrows(NameAlreadyExistsException.class, () -> brandService.createBrand(dto));
    }

    @Test
    void createBrand_withUniqueSlugAndName_shouldReturnBrand() {
        final var dto = new BrandDTO("some-slug", "Some Brand", "Some description");
        when(brandRepository.findBySlug(dto.slug())).thenReturn(Optional.empty());
        when(brandRepository.findByName(dto.name())).thenReturn(Optional.empty());
        when(brandRepository.save(dto.toBrand())).thenReturn(dto.toBrand());
        var brand = brandService.createBrand(dto);
        assertNotNull(brand);
        assertEquals(dto.slug(), brand.slug());
        assertEquals(dto.name(), brand.name());
        assertEquals(dto.description(), brand.description());
    }

    @Test
    void findBrandBySlug_withExistingSlug_shouldReturnBrand() {
        final var dto = new BrandDTO("some-slug", "Some Brand", "Some description");
        when(brandRepository.findBySlug(dto.slug())).thenReturn(Optional.of(dto.toBrand()));
        var brand = brandService.findBrandBySlug(dto.slug());
        assertNotNull(brand);
        assertEquals(dto.slug(), brand.slug());
        assertEquals(dto.name(), brand.name());
        assertEquals(dto.description(), brand.description());
    }

    @Test
    void findBrandBySlug_withUnknownSlug_shouldThrowBrandNotFoundException() {
        when(brandRepository.findBySlug("some-slug")).thenThrow(new NotFoundException());
        assertThrows(NotFoundException.class, () -> brandService.findBrandBySlug("some-slug"));
    }
}