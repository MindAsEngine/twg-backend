package org.mae.twg.backend.services.auth;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
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
        log.debug("Start UserService.save");
        return userRepo.save(user);
    }

    public UserDTO update(ProfileDTO profileDTO) {
        log.debug("Start UserService.update");
        User user = findByUsername(profileDTO.getUsername());
        String exceptionText = "";

        if (!user.getEmail().equals(profileDTO.getEmail()) && userRepo.existsByEmail(profileDTO.getEmail())) {
            exceptionText += ("Пользователь с таким email уже существует.\n");
        }
        if (!user.getPhone().equals(profileDTO.getPhone()) && userRepo.existsByPhone(profileDTO.getPhone())) {
            exceptionText += ("Пользователь с таким телефоном уже существует.\n");
        }
        if (!exceptionText.isEmpty()) {
            log.warn(exceptionText);
            throw new ValidationException(exceptionText);
        }
        user.setEmail(profileDTO.getEmail());
        user.setPhone(profileDTO.getPhone());
        user.setFirstName(profileDTO.getFirstName());
        user.setLastName(profileDTO.getLastName());
        user.setPatronymic(profileDTO.getPatronymic());
        userRepo.saveAndFlush(user);
        log.debug("End UserService.update");
        return new UserDTO(user);
    }

    public List<UserDTO> getUsersByRole(UserRole role) {
        log.debug("Start UserService.getUsersByRole");
        List<User> users = userRepo.findAllByUserRoleAndIsEnabledTrue(role);
        if (users.isEmpty()) {
            log.error("Users with role " + role.name() + " not found");
            throw new ObjectNotFoundException("Users with role " + role.name() + " not found");
        }
        log.debug("End UserService.getUsersByRole");
        return users.stream().map(UserDTO::new).toList();
    }

    public void setTelegramId(TelegramDataDTO telegramDataDTO) {
        log.debug("Start UserService.setTelegramId");
        User user = findByUsername(telegramDataDTO.getUsername());
        user.setTelegramId(telegramDataDTO.getTelegramId());
        userRepo.saveAndFlush(user);
        log.debug("End UserService.setTelegramId");
    }

    public List<Long> getAdminTelegramIds() {
        log.debug("Start UserService.getAdminTelegramIds");
        return userRepo.findAllByUserRoleAndTelegramIdIsNotNull(UserRole.TWG_ADMIN).stream()
                .filter(User::getIsEnabled)
                .map(User::getTelegramId)
                .map(Long::valueOf)
                .toList();
    }

    public User create(User user) {
        log.debug("Start UserService.create");
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
            log.warn(exceptionText);
            throw new ValidationException(exceptionText);
        }
        log.debug("End UserService.create");
        return save(user);
    }

    public void deleteByUsername(String username) {
        log.debug("Start UserService.deleteByUsername");
        User user = findByUsername(username);
        user.setIsEnabled(false);
        userRepo.saveAndFlush(user);
        log.debug("End UserService.deleteByUsername");
    }

    public User findById(Long id) {
        log.debug("Start UserService.findById");
        return userRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("User with id=" + id + " not found");
                    return new ObjectNotFoundException("User with id=" + id + " not found");
                });
    }

    public User findByUsername(String username) {
        log.debug("Start UserService.findByUsername");
        return userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User with username=" + username + " not found");
                    return new ObjectNotFoundException("User with username=" + username + " not found");
                });
    }

    public void refreshLastLogin(User user) {
        log.debug("Start UserService.refreshLastLogin");
        user.setLastLogin(LocalDateTime.now());
        userRepo.saveAndFlush(user);
        log.debug("End UserService.refreshLastLogin");
    }

    public User getById(Long id) throws UserNotFound {
        log.debug("Start UserService.getById");
        return userRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id=" + id + "не найден");
                    return new UserNotFound("Пользователь с id=" + id + "не найден");
                });
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Start UserService.loadUserByUsername");
        return userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Пользователь c username=" + username + " не найден");
                    return new UsernameNotFoundException("Пользователь c username=" + username + " не найден");
                });
    }

    @Transactional
    public UserDTO uploadImages(MultipartFile image) throws IOException {
        log.debug("Start UserService.uploadImages");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        String url = imageService.saveImage(ModelType.USER, image);
        User user = findById(id);
        user.setMediaPath(url);
        userRepo.saveAndFlush(user);
        log.debug("End UserService.uploadImages");
        return new UserDTO(user);
    }

    public UserDTO deleteImages(String image) {
        log.debug("Start UserService.deleteImages");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = ((User) authentication.getPrincipal()).getId();
        imageService.deleteImages(Collections.singletonList(image));
        User user = findById(id);
        user.setMediaPath(null);
        userRepo.save(user);
        log.debug("End UserService.deleteImages");
        return new UserDTO(findById(id));
    }


    public List<TourDTO> getFavouriteTours(Localization localization) {
        log.debug("Start UserService.getFavouriteTours");
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);
        log.debug("End UserService.getFavouriteTours");
        return tourService.modelsToDTOs(user.getFavourites().stream(), localization);
    }

    @Transactional
    public void addTourToFavourite(FavouriteTourDTO tourDTO) {
        log.debug("Start UserService.addTourToFavourite");
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);

        Tour tour = tourService.findById(tourDTO.getTourId());
        user.getFavourites().add(tour);
        log.debug("End UserService.addTourToFavourite");
    }

    @Transactional
    public void deleteTourFromFavourite(Long tourId) {
        log.debug("Start UserService.deleteTourFromFavourite");
        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        User user = userRepo.getReferenceById(id);

        Tour tour = tourService.findById(tourId);
        user.getFavourites().remove(tour);
        log.debug("End UserService.deleteTourFromFavourite");
    }

    @Transactional
    public Boolean checkInFavourite(Long tourId) {
        log.debug("Start UserService.checkInFavourite");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Boolean result = userRepo.checkForFavourite(tourId, username);
        log.debug("End UserService.checkInFavourite");
        return result;
    }


}