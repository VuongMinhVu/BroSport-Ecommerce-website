package com.se2.demo.service;

import com.se2.demo.dto.request.BrandRequest;
import com.se2.demo.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    List<BrandResponse> getAllBrands();

    BrandResponse getBrandById(Integer id);

    BrandResponse createBrand(BrandRequest request);

    BrandResponse updateBrand(Integer id, BrandRequest request);

    void deleteBrand(Integer id);
}
