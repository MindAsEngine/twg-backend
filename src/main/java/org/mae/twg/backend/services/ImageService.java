package org.mae.twg.backend.services;

import jakarta.validation.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class ImageService {
    @Value("${upload.path}")
    private String parent_path;
    //@Cacheable("images")
    public Resource loadFileAsResource(String folder, String filename) throws MalformedURLException {
        log.debug("Start ImageService.loadFileAsResource");
        Path fileStorageLocation = Paths.get(parent_path + "/" + folder).toAbsolutePath().normalize();
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        log.debug("End ImageService.loadFileAsResource");
        return new UrlResource(filePath.toUri());
    }

    public String saveImage(ModelType modelType, MultipartFile image) throws IOException {
        log.debug("Start ImageService.saveImage");
        if (!isValidImageType(image.getContentType())) {
            log.error("Недопустимый тип файла");
            throw new ValidationException("Недопустимый тип файла");
        }
        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + image.getOriginalFilename();
        String uploadPath = parent_path + "/" + modelType.toString();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                log.error("IOException");
                throw new IOException();
            }
        }
        image.transferTo(new File(uploadPath + "/" + resultFilename));
        String path = new String("/" + modelType.toString() + "/" + resultFilename);
        log.debug("End ImageService.saveImage");
        return path;
    }
    public List<String> saveImages(ModelType modelType, List<MultipartFile> images) throws IOException {
        log.debug("Start ImageService.saveImages");
        List<String> paths = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!isValidImageType(image.getContentType())) {
                log.error("Недопустимый тип файла");
                throw new ValidationException("Недопустимый тип файла");
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + image.getOriginalFilename();
            String uploadPath = parent_path + "/" + modelType.toString();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                if(!uploadDir.mkdirs()) {
                    log.error("IOException");
                    throw new IOException();
                }
            }
            image.transferTo(new File(uploadPath + "/" + resultFilename));
            paths.add("/" + modelType.toString() + "/" + resultFilename);
        }
        log.debug("End ImageService.saveImages");
        return paths;
    }

    public void deleteImages(List<String> mediaPaths) {
        log.debug("Start ImageService.deleteImages");
        for (String mediaPath : mediaPaths) {
            File fileToDelete = new File(parent_path + mediaPath);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
        log.debug("End ImageService.deleteImages");
    }

    public boolean isValidImageType(String contentType) {
        return contentType.equals(MediaType.IMAGE_JPEG_VALUE) ||
                contentType.equals(MediaType.IMAGE_PNG_VALUE) ||
                contentType.equals(MediaType.IMAGE_GIF_VALUE);
    }
}
