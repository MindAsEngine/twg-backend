package org.mae.twg.backend.services.auth;

import lombok.AllArgsConstructor;
import org.mae.twg.backend.dto.auth.JwtAuthenticationResponse;
import org.mae.twg.backend.dto.auth.SignInRequest;
import org.mae.twg.backend.dto.auth.SignUpRequest;
import org.mae.twg.backend.dto.auth.TokenRefreshRequest;
import org.mae.twg.backend.dto.profile.UserDTO;
import org.mae.twg.backend.exceptions.TokenValidationException;
import org.mae.twg.backend.models.auth.RefreshToken;
import org.mae.twg.backend.models.auth.Role;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.auth.UserRole;
import org.mae.twg.backend.repositories.auth.RefreshTokenRepo;
import org.mae.twg.backend.utils.auth.JwtUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("AuthService")
@AllArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;

    public User createUser(SignUpRequest request, UserRole role) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .patronymic(request.getPatronymic() == null ? "No patronymic" : request.getPatronymic())
                .userRole(role)
                .lastLogin(LocalDateTime.now())
                .build();
    }

    public UserDTO addUserWithRole(SignUpRequest request, UserRole role) {
        User user = createUser(request, role);
        userService.save(user);
        return new UserDTO(user);
    }

    public void deleteByUsername(String username) {
        userService.deleteByUsername(username);
    }

    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        User user = createUser(request, UserRole.USER);

        userService.create(user);
        userService.refreshLastLogin(user);

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

        userService.refreshLastLogin(user);

        var jwt = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.createRefreshToken(user);
        return new JwtAuthenticationResponse(jwt, refreshToken.getToken());
    }

    @Transactional
    public JwtAuthenticationResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken token = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenValidationException(
                        "Refresh token '" + requestRefreshToken +"' not found"));
        jwtUtils.verifyExpiration(token);
        String accessToken = jwtUtils.generateToken(token.getUser());
        userService.refreshLastLogin(token.getUser());
        return new JwtAuthenticationResponse(accessToken, token.getToken());
    }

    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        User user = (User) authentication.getPrincipal();

        Optional<RefreshToken> refreshToken = refreshTokenRepo.findByUser(user);
        if (refreshToken.isEmpty()) {
            return;
        }
        refreshTokenRepo.delete(refreshToken.get());
    }

    public boolean hasAccess(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("User isn't authorized");
        }

        return authentication.getAuthorities()
                        .stream().map(grantedAuthority -> (Role) grantedAuthority)
                        .anyMatch(r -> r.includes(role));
    }
}
