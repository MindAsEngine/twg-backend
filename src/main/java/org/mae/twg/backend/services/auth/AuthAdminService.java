package org.mae.twg.backend.services.auth;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.auth.JwtAuthenticationResponse;
import org.mae.twg.backend.dto.auth.SignInRequest;
import org.mae.twg.backend.dto.auth.SignUpRequest;
import org.mae.twg.backend.models.admin.Admin;
import org.mae.twg.backend.models.admin.AdminRole;
import org.mae.twg.backend.utils.auth.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthAdminService {
    private final AdminService adminService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var admin = Admin.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic() == null ? "No patronymic" : request.getPatronymic())
                .adminRole(AdminRole.ROLE_MODERATOR)
                .build();

        adminService.create(admin);

        var jwt = jwtUtils.generateToken(admin);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = adminService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtUtils.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}