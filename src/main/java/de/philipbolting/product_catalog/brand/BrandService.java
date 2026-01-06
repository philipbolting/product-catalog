package de.philipbolting.product_catalog.brand;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Transactional
    public Brand createBrand(BrandDTO dto) {
        if (brandRepository.findBySlug(dto.slug()).isPresent()) {
            throw new IllegalArgumentException("Brand with slug " + dto.slug() + " already exists");
        }
        if (brandRepository.findByName(dto.name()).isPresent()) {
            throw new IllegalArgumentException("Brand with name " + dto.name() + " already exists");
        }
        var brand = new Brand(dto.slug(), dto.name(), dto.description());
        return brandRepository.save(brand);
    }
}
