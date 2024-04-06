package org.mae.twg.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Schema(description = "Ответ на ошибку сервера")
@Log4j2
public class ResponseErrorDTO {
    @Schema(description = "HTTP статус", example = "404")
    private final HttpStatus status;
    @Schema(description = "Сообщение из логов", example = "Не авторизован")
    private final String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата и время", example = "2024-01-01 11:11:11")
    private final LocalDateTime time = LocalDateTime.now();

    public ResponseErrorDTO(HttpStatus status, String message) {
        log.error("HttpStatus: " + status + "; message: " + message);
        this.status = status;
        this.message = message;
    }
}
