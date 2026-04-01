package com.se2.demo.service;

import com.se2.demo.dto.request.ProfileUpdateRequest;
import com.se2.demo.model.entity.User;

public interface UserService {
    User getUserByEmail(String email);
    User updateProfile(String email, ProfileUpdateRequest request);
}
