package org.mae.twg.backend.services.auth;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.models.admin.Admin;
import org.mae.twg.backend.models.admin.AdminRole;
import org.mae.twg.backend.repositories.AdminRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepo adminRepo;

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    public Admin save(Admin admin) {
        return adminRepo.save(admin);
    }


    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    public Admin create(Admin admin) {
        if (adminRepo.existsByUsername(admin.getUsername())) {
            // TODO: Заменить на свои исключения
            throw new ValidationException("Пользователь с таким именем уже существует");
        }

        if (adminRepo.existsByEmail(admin.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }

        return save(admin);
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    public Admin getByUsername(String username) {
        return adminRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public Admin getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }


    /**
     * Выдача прав администратора текущему пользователю
     * <p>
     * Нужен для демонстрации
     */
    @Deprecated
    public void getAdmin() {
        var user = getCurrentUser();
        user.setAdminRole(AdminRole.ROLE_ADMIN);
        save(user);
    }
}