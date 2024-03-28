package org.mae.twg.backend.controllers.images;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.services.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@Tag(name = "Фотографии")
@Log4j2
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    @GetMapping(value = "/{imageFolder}/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Отдать фотографию")
    public ResponseEntity<byte[]> serveFile(@PathVariable String imageFolder,
                                            @PathVariable String imageName) throws IOException {
        try {
            Resource resource = imageService.loadFileAsResource(imageFolder, imageName);
            log.info("Отправка фотографии: " + "/" + imageFolder + "/" + imageName);
            return ResponseEntity.ok().body(resource.getContentAsByteArray());
        } catch (FileNotFoundException e) {
            log.error("Файл не найден:" + "/" + imageFolder + "/" + imageName);
            return ResponseEntity.notFound().build();
        }
    }


}
