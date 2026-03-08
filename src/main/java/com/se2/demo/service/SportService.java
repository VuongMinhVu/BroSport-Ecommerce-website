package com.se2.demo.service;

import com.se2.demo.dto.request.SportRequest;
import com.se2.demo.dto.response.SportResponse;

import java.util.List;

public interface SportService {
    List<SportResponse> getAllSports();

    SportResponse getSportById(Integer id);

    SportResponse createSport(SportRequest request);

    SportResponse updateSport(Integer id, SportRequest request);

    void deleteSport(Integer id);
}
