package org.mae.twg.backend.services.auth;

import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.JwtAuthenticationResponse;
import org.mae.twg.backend.dto.SignInRequest;
import org.mae.twg.backend.dto.SignUpRequest;
import org.mae.twg.backend.models.business.UserRole;
import org.mae.twg.backend.models.business.User;
import org.mae.twg.backend.utils.auth.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final UserService userService;
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


        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtUtils.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}