package com.se2.demo.service;

import com.se2.demo.dto.request.GenderRequest;
import com.se2.demo.dto.response.GenderResponse;

import java.util.List;

public interface GenderService {
    List<GenderResponse> getAllGenders();

    GenderResponse getGenderById(Integer id);

    GenderResponse createGender(GenderRequest request);

    GenderResponse updateGender(Integer id, GenderRequest request);

    void deleteGender(Integer id);
}
