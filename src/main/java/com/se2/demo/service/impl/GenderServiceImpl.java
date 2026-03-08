package com.se2.demo.service.impl;

import com.se2.demo.dto.request.GenderRequest;
import com.se2.demo.dto.response.GenderResponse;
import com.se2.demo.mapper.CommonMapper;
import com.se2.demo.model.entity.Gender;
import com.se2.demo.repository.GenderRepository;
import com.se2.demo.service.CloudinaryService;
import com.se2.demo.service.GenderService;
import com.se2.demo.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenderServiceImpl implements GenderService {

    private final GenderRepository genderRepository;
    private final CommonMapper commonMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public List<GenderResponse> getAllGenders() {
        return commonMapper.toGenderResponseList(genderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public GenderResponse getGenderById(Integer id) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + id));
        return commonMapper.toResponse(gender);
    }

    @Override
    @Transactional
    public GenderResponse createGender(GenderRequest request) {
        Gender gender = commonMapper.toEntity(request);

        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String logoUrl = cloudinaryService.uploadFile(request.getLogoFile(), "genders");
            gender.setLogoUrl(logoUrl);
        }

        return commonMapper.toResponse(genderRepository.save(gender));
    }

    @Override
    @Transactional
    public GenderResponse updateGender(Integer id, GenderRequest request) {
        Gender existingGender = genderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + id));

        existingGender.setName(request.getName());
        existingGender.setDescription(request.getDescription());

        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String logoUrl = cloudinaryService.uploadFile(request.getLogoFile(), "genders");
            existingGender.setLogoUrl(logoUrl);
        }

        return commonMapper.toResponse(genderRepository.save(existingGender));
    }

    @Override
    @Transactional
    public void deleteGender(Integer id) {
        Gender existingGender = genderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gender not found with id: " + id));
        genderRepository.delete(existingGender);
    }
}
