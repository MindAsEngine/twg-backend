package org.mae.twg.backend.services.auth;

import org.mae.twg.backend.dto.auth.JwtAuthenticationResponse;
import org.mae.twg.backend.dto.auth.SignInRequest;
import org.mae.twg.backend.dto.auth.SignUpRequest;
import org.mae.twg.backend.dto.auth.TokenRefreshRequest;
import org.mae.twg.backend.exceptions.TokenValidationException;
import org.mae.twg.backend.models.auth.RefreshToken;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.auth.UserRole;
import org.mae.twg.backend.repositories.auth.RefreshTokenRepo;
import org.mae.twg.backend.utils.auth.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;
    private final Long refreshTokenExpirationS;

    public AuthService(RefreshTokenRepo refreshTokenRepo,
                       UserService userService,
                       JwtUtils jwtUtils,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       @Value("${config.token.refresh_expiration_hours}") Long refreshExpirationHours) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenExpirationS = refreshExpirationHours * 3600;
    }

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
        var refreshToken = createRefreshToken(user);
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
        var refreshToken = createRefreshToken(user);
        return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
    }

    public JwtAuthenticationResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken token = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenValidationException(
                        "Refresh token '" + requestRefreshToken +"' not found"));
        verifyExpiration(token);
        String accessToken = jwtUtils.generateToken(token.getUser());
        return new JwtAuthenticationResponse(accessToken, token.getToken());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpirationS));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepo.saveAndFlush(refreshToken);
        return refreshToken;
    }

    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new TokenValidationException("Refresh token "
                    + token.getToken() +" was expired. Please make a new signin request");
        }
    }
}
