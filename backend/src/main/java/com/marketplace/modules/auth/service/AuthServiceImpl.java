package com.marketplace.modules.auth.service;

import com.marketplace.common.Constants;
import com.marketplace.exception.BadRequestException;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.modules.auth.dto.AuthResponse;
import com.marketplace.modules.auth.dto.LoginRequest;
import com.marketplace.modules.auth.dto.RegisterRequest;
import com.marketplace.modules.auth.dto.UserProfileResponse;
import com.marketplace.modules.auth.entity.Role;
import com.marketplace.modules.auth.entity.User;
import com.marketplace.modules.auth.repository.RoleRepository;
import com.marketplace.modules.auth.repository.UserRepository;
import com.marketplace.security.CustomUserDetails;
import com.marketplace.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new AuthResponse(jwt, userPrincipal.getUserId(), userPrincipal.getUsername(),
                userPrincipal.getFullName(), roles);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Error: Email is already registered: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName() != null ? request.getFullName().trim() : "");
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddressLine1(request.getAddressLine1());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPinCode(request.getPinCode());
        user.setIsActive(1);

        String roleStr = request.getRole() != null ? request.getRole().trim().toUpperCase() : "CUSTOMER";
        String normalizedRoleName = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;

        if (!normalizedRoleName.equals(Constants.ROLE_CUSTOMER) && !normalizedRoleName.equals(Constants.ROLE_SELLER)) {
            throw new BadRequestException("Invalid role specified. Allowed values: CUSTOMER, SELLER");
        }

        Role userRole = roleRepository.findByRoleName(normalizedRoleName)
                .orElseGet(() -> roleRepository.save(new Role(normalizedRoleName, "Role for " + normalizedRoleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Authenticate new registered user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        List<String> roleNames = savedUser.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        return new AuthResponse(jwt, savedUser.getUserId(), savedUser.getEmail(), savedUser.getFullName(), roleNames);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddressLine1(user.getAddressLine1());
        response.setCity(user.getCity());
        response.setState(user.getState());
        response.setPinCode(user.getPinCode());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toList()));

        return response;
    }
}
