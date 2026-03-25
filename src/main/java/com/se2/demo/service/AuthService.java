package com.se2.demo.service;

import com.se2.demo.dto.request.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}
