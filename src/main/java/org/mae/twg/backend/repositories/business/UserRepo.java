package org.mae.twg.backend.repositories.business;

import org.mae.twg.backend.models.auth.User;
import org.mae.twg.backend.models.auth.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    List<User> findAllByUserRoleAndTelegramIdIsNotNull(UserRole role);
    List<User> findAllByUserRoleAndIsEnabledTrue(UserRole role);
    @Query(value = """
    select
         exists(
             select *
             from favourite_tours f
             join users u on u.user_id = f.user_user_id
             where f.favourites_tour_id = :tourId and u.username = :username
         )
""", nativeQuery = true)
    Boolean checkForFavourite(@RequestParam("tourId") Long tourId,
                              @RequestParam("username") String username);
}
