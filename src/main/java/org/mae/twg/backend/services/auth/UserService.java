package org.mae.twg.backend.services.auth;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.travel.response.TourDTO;
import org.mae.twg.backend.exceptions.UserNotFound;
import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.travel.enums.Localization;
import org.mae.twg.backend.repositories.business.UserRepo;
import org.mae.twg.backend.services.travel.TourService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
    private final UserRepo userRepo;
    private final TourService tourService;

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

    public List<TourDTO> getFavouriteTours(Localization localization) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return tourService.modelsToDTOs(user.getFavourites().stream(), localization);
    }
}