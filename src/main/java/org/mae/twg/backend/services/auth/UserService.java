package org.mae.twg.backend.services.auth;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.profile.FavouriteTourDTO;
import org.mae.twg.backend.dto.profile.ProfileDTO;
import org.mae.twg.backend.dto.profile.TelegramDataDTO;
import org.mae.twg.backend.dto.profile.UserDTO;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.exceptions.UserNotFound;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.auth.UserRole;
import org.mae.twg.backend.models.travel.Tour;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.UserRepo;
import org.mae.twg.backend.services.ImageService;
import org.mae.twg.backend.services.ModelType;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final TourService tourService;

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    public User save(User user) {
        return userRepo.save(user);
    }

    public UserDTO update(ProfileDTO profileDTO) {
        User user = findByUsername(profileDTO.getUsername());
        user.setEmail(profileDTO.getEmail());
        user.setPhone(profileDTO.getPhone());
        user.setFirstName(profileDTO.getFirstName());
        user.setLastName(profileDTO.getLastName());
        user.setPatronymic(profileDTO.getPatronymic());
        save(user);

        return new UserDTO(user);
    }

    public void setTelegramId(TelegramDataDTO telegramDataDTO) {
        User user = findByUsername(telegramDataDTO.getUsername());
        user.setTelegramId(telegramDataDTO.getTelegramId());
        userRepo.saveAndFlush(user);
    }

    public List<Long> getAdminTelegramIds() {
        return userRepo.findAllByUserRoleAndTelegramIdIsNotNull(UserRole.TWG_ADMIN).stream()
                .map(User::getTelegramId)
                .map(Long::valueOf)
                .toList();
    }

    public User create(User user) {
        String exceptionText = "";
        if (userRepo.existsByUsername(user.getUsername())) {
            exceptionText += ("Пользователь с таким username уже существует.\n");
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            exceptionText += ("Пользователь с таким email уже существует.\n");
        }
        if (userRepo.existsByPhone(user.getPhone())) {
            exceptionText += ("Пользователь с таким телефоном уже существует.\n");
        }
        if (!exceptionText.isEmpty()) {
            throw new ValidationException(exceptionText);
        }
        return save(user);
    }

    public void deleteByUsername(String username) {
        User user = findByUsername(username);
        user.setIsEnabled(false);
        userRepo.saveAndFlush(user);
    }

    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id=" + id + " not found"));
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User with username=" + username + " not found"));
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
    public UserDTO uploadImages(MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        String url = imageService.saveImage(ModelType.USER, image);
        User user = findById(id);
        user.setMediaPath(url);
        userRepo.saveAndFlush(user);
        return new UserDTO(user);
    }

    public UserDTO deleteImages(String image) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        imageService.deleteImages(Collections.singletonList(image));
        User user = findById(id);
        user.setMediaPath(null);
        userRepo.save(user);
        return new UserDTO(findById(id));
    }


    public List<TourDTO> getFavouriteTours(Localization localization) {
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);
        return tourService.modelsToDTOs(user.getFavourites().stream(), localization);
    }

    @Transactional
    public void addTourToFavourite(FavouriteTourDTO tourDTO) {
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);

        Tour tour = tourService.findById(tourDTO.getTourId());
        user.getFavourites().add(tour);
    }

    @Transactional
    public void deleteTourFromFavourite(FavouriteTourDTO tourDTO) {
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);

        Tour tour = tourService.findById(tourDTO.getTourId());
        user.getFavourites().remove(tour);
    }

    @Transactional
    public Boolean checkInFavourite(Long tourId) {
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);

        Tour tour = tourService.findById(tourId);
        return user.getFavourites().contains(tour);
    }


}