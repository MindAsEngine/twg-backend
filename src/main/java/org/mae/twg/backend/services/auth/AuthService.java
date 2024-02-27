package org.mae.twg.backend.services.auth;

import lombok.AllArgsConstructor;
import org.mae.twg.backend.dto.auth.JwtAuthenticationResponse;
import org.mae.twg.backend.dto.auth.SignInRequest;
import org.mae.twg.backend.dto.auth.SignUpRequest;
import org.mae.twg.backend.dto.auth.TokenRefreshRequest;
import org.mae.twg.backend.exceptions.TokenValidationException;
import org.mae.twg.backend.models.auth.*;
import org.mae.twg.backend.repositories.auth.RefreshTokenRepo;
import org.mae.twg.backend.utils.auth.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("AuthService")
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic() == null ? "No patronymic" : request.getPatronymic())
                .userRole(UserRole.USER)
                .build();

        userService.create(user);

        var jwt = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.createRefreshToken(user);
        return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .loadUserByUsername(request.getUsername());

        var jwt = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.createRefreshToken(user);
        return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
    }

    public JwtAuthenticationResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken token = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenValidationException(
                        "Refresh token '" + requestRefreshToken +"' not found"));
        jwtUtils.verifyExpiration(token);
        String accessToken = jwtUtils.generateToken(token.getUser());
        return new JwtAuthenticationResponse(accessToken, token.getToken());
    }

    public boolean hasAccess(Role role) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream().map(grantedAuthority -> (Role) grantedAuthority)
                        .anyMatch(r -> r.includes(role));
    }
}
