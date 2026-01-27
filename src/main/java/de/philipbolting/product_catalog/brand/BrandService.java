package de.philipbolting.product_catalog.brand;

import de.philipbolting.product_catalog.error.NameAlreadyExistsException;
import de.philipbolting.product_catalog.error.NotFoundException;
import de.philipbolting.product_catalog.error.SlugAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Transactional
    public BrandDTO createBrand(BrandDTO dto) {
        if (brandRepository.findBySlug(dto.slug()).isPresent()) {
            throw new SlugAlreadyExistsException();
        }
        if (brandRepository.findByName(dto.name()).isPresent()) {
            throw new NameAlreadyExistsException();
        }
        return BrandDTO.fromBrand(brandRepository.save(dto.toBrand()));
    }

    public BrandDTO findBrandBySlug(String slug) {
        return BrandDTO.fromBrand(brandRepository.findBySlug(slug).orElseThrow(NotFoundException::new));
    }
}
