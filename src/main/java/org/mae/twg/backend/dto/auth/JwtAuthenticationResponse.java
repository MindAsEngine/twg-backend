package org.mae.twg.backend.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Ответ c токеном доступа")
public class JwtAuthenticationResponse {
    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    @NonNull
    private String token;
    @Schema(description = "Токен обновления", example = "e2a8a9c2-3a3f-42aa-8e32-f2a62f2852e4")
    @NonNull
    private String refreshToken;
    @Schema(description = "Тип токена", example = "Bearer")
    private String tokenType = "Bearer";


}