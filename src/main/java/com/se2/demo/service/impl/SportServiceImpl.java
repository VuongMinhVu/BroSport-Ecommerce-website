package com.se2.demo.service.impl;

import com.se2.demo.dto.request.SportRequest;
import com.se2.demo.dto.response.SportResponse;
import com.se2.demo.mapper.CommonMapper;
import com.se2.demo.model.entity.Sport;
import com.se2.demo.repository.SportRepository;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.service.SportService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SportServiceImpl implements SportService {

    private final SportRepository sportRepository;
    private final CommonMapper commonMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<SportResponse> getAllSports() {
        return commonMapper.toSportResponseList(sportRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SportResponse getSportById(Integer id) {
        Sport sport = sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found with id: " + id));
        return commonMapper.toResponse(sport);
    }

    @Override
    @Transactional
    public SportResponse createSport(SportRequest request) {
        Sport sport = commonMapper.toEntity(request);

        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String logoUrl = cloudinaryService.uploadFile(request.getLogoFile(), "sports");
            sport.setLogoUrl(logoUrl);
        }

        return commonMapper.toResponse(sportRepository.save(sport));
    }

    @Override
    @Transactional
    public SportResponse updateSport(Integer id, SportRequest request) {
        Sport existingSport = sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found with id: " + id));

        existingSport.setName(request.getName());
        existingSport.setDescription(request.getDescription());

        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String logoUrl = cloudinaryService.uploadFile(request.getLogoFile(), "sports");
            existingSport.setLogoUrl(logoUrl);
        }

        return commonMapper.toResponse(sportRepository.save(existingSport));
    }

    @Override
    @Transactional
    public void deleteSport(Integer id) {
        Sport existingSport = sportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sport not found with id: " + id));
        sportRepository.delete(existingSport);
    }
}
