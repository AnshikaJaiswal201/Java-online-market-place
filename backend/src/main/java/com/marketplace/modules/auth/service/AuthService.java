package com.marketplace.modules.auth.service;

import com.marketplace.modules.auth.dto.AuthResponse;
import com.marketplace.modules.auth.dto.LoginRequest;
import com.marketplace.modules.auth.dto.RegisterRequest;
import com.marketplace.modules.auth.dto.UserProfileResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    UserProfileResponse getCurrentUserProfile(String email);
}
