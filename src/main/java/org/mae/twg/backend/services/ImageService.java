package org.mae.twg.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
public class ImageService {
    @Value("${upload.path}")
    private String parent_path;
    public Resource loadFileAsResource(String folder, String filename) throws MalformedURLException {
        Path fileStorageLocation = Paths.get(parent_path + "/" + folder).toAbsolutePath().normalize();
        Path filePath = fileStorageLocation.resolve(filename).normalize();
        return new UrlResource(filePath.toUri());
    }

    public String saveImage(ModelType modelType, MultipartFile image) throws IOException {
        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + "." + image.getOriginalFilename();
        String uploadPath = parent_path + "/" + modelType.toString();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new IOException();
            }
        }
        image.transferTo(new File(uploadPath + "/" + resultFilename));
        String path = new String("/" + modelType.toString() + "/" + resultFilename);
        return path;
    }
    public List<String> saveImages(ModelType modelType, List<MultipartFile> images) throws IOException {
        List<String> paths = new ArrayList<>();
        for (MultipartFile image : images) {
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + image.getOriginalFilename();
            String uploadPath = parent_path + "/" + modelType.toString();
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                if(!uploadDir.mkdirs()) {
                    throw new IOException();
                }
            }
            image.transferTo(new File(uploadPath + "/" + resultFilename));
            paths.add("/" + modelType.toString() + "/" + resultFilename);
        }
        return paths;
    }

    public void deleteImages(List<String> mediaPaths) {
        for (String mediaPath : mediaPaths) {
            File fileToDelete = new File(parent_path + mediaPath);
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }
        }
    }
}
