package org.mae.twg.backend.services.auth;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.auth.UserDTO;
import org.mae.twg.backend.dto.travel.response.CountryDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.exceptions.UserNotFound;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.Country;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.UserRepo;
import org.mae.twg.backend.services.ImageService;
import org.mae.twg.backend.services.ModelType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
    private final UserRepo userRepo;
    private final ImageService imageService;

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    public User save(User user) {
        return userRepo.save(user);
    }


    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    public User create(User user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            // TODO: Заменить на свои исключения
            throw new ValidationException("Пользователь с таким именем уже существует");
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }

        return save(user);
    }

    public User findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + id + " not found"));
        return user;
    }

    public void refreshLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepo.saveAndFlush(user);
    }

    public User getById(Long id) throws UserNotFound {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFound("Пользователь с id=" + id + "не найден"));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь c username=" + username + " не найден"));
    }

    @Transactional
    public UserDTO uploadImages(Long id, MultipartFile image) throws IOException {
        String url = imageService.saveImage(ModelType.USER, image);
        User user = findById(id);
        user.setMediaPath(url);
        userRepo.saveAndFlush(user);
        return new UserDTO(user);
    }

    public UserDTO deleteImages(Long id, String image) {
        imageService.deleteImages(Collections.singletonList(image));
        User user = findById(id);
        user.setMediaPath(null);
        userRepo.save(user);
        return new UserDTO(findById(id));
    }

}