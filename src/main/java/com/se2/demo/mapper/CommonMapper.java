package com.se2.demo.mapper;

import com.se2.demo.dto.request.*;
import com.se2.demo.dto.response.*;
import com.se2.demo.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommonMapper {

    Brand toEntity(BrandRequest request);

    BrandResponse toResponse(Brand entity);

    List<BrandResponse> toBrandResponseList(List<Brand> entities);

    Color toEntity(ColorRequest request);

    ColorResponse toResponse(Color entity);

    List<ColorResponse> toColorResponseList(List<Color> entities);

    Size toEntity(SizeRequest request);

    SizeResponse toResponse(Size entity);

    List<SizeResponse> toSizeResponseList(List<Size> entities);

    TargetCustomer toEntity(TargetCustomerRequest request);

    TargetCustomerResponse toResponse(TargetCustomer entity);

    List<TargetCustomerResponse> toTargetCustomerResponseList(List<TargetCustomer> entities);

    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(source = "parentCategory.id", target = "parentId")
    CategoryResponse toResponse(Category entity);

    List<CategoryResponse> toCategoryResponseList(List<Category> entities);
}
