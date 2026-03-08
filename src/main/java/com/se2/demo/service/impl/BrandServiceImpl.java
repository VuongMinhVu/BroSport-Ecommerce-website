package com.se2.demo.service.impl;

import com.se2.demo.dto.request.BrandRequest;
import com.se2.demo.dto.response.BrandResponse;
import com.se2.demo.mapper.CommonMapper;
import com.se2.demo.model.entity.Brand;
import com.se2.demo.repository.BrandRepository;
import com.se2.demo.service.BrandService;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final CommonMapper commonMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return commonMapper.toBrandResponseList(brandRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return commonMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        Brand brand = commonMapper.toEntity(request);

        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String logoUrl = cloudinaryService.uploadFile(request.getLogoFile(), "brands");
            brand.setLogoUrl(logoUrl);
        }

        return commonMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Integer id, BrandRequest request) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        existingBrand.setName(request.getName());
        existingBrand.setDescription(request.getDescription());

        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String logoUrl = cloudinaryService.uploadFile(request.getLogoFile(), "brands");
            existingBrand.setLogoUrl(logoUrl);
        }

        return commonMapper.toResponse(brandRepository.save(existingBrand));
    }

    @Override
    @Transactional
    public void deleteBrand(Integer id) {
        Brand existingBrand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        brandRepository.delete(existingBrand);
    }
}
