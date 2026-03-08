package com.se2.demo.controller;

import com.se2.demo.dto.request.GenderRequest;
import com.se2.demo.dto.response.GenderResponse;
import com.se2.demo.service.GenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genders")
@RequiredArgsConstructor
public class GenderController {

    private final GenderService genderService;

    @GetMapping
    public ResponseEntity<List<GenderResponse>> getAllGenders() {
        return ResponseEntity.ok(genderService.getAllGenders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenderResponse> getGenderById(@PathVariable Integer id) {
        return ResponseEntity.ok(genderService.getGenderById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenderResponse> createGender(@ModelAttribute GenderRequest request) {
        return new ResponseEntity<>(genderService.createGender(request), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenderResponse> updateGender(@PathVariable Integer id,
            @ModelAttribute GenderRequest request) {
        return ResponseEntity.ok(genderService.updateGender(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGender(@PathVariable Integer id) {
        genderService.deleteGender(id);
        return ResponseEntity.noContent().build();
    }
}
