package com.se2.demo.controller;

import com.se2.demo.dto.request.SportRequest;
import com.se2.demo.dto.response.SportResponse;
import com.se2.demo.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @GetMapping
    public ResponseEntity<List<SportResponse>> getAllSports() {
        return ResponseEntity.ok(sportService.getAllSports());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportResponse> getSportById(@PathVariable Integer id) {
        return ResponseEntity.ok(sportService.getSportById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SportResponse> createSport(@ModelAttribute SportRequest request) {
        return new ResponseEntity<>(sportService.createSport(request), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SportResponse> updateSport(@PathVariable Integer id, @ModelAttribute SportRequest request) {
        return ResponseEntity.ok(sportService.updateSport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSport(@PathVariable Integer id) {
        sportService.deleteSport(id);
        return ResponseEntity.noContent().build();
    }
}
