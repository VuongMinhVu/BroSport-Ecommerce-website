package com.se2.demo.mapper;

import com.se2.demo.dto.response.RatingResponse;
import com.se2.demo.model.entity.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {
    RatingResponse toResponse(Rating rating);
}
