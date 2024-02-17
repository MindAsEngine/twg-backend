package org.mae.twg.backend.models.admin;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admins")
public class Admin implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "admin_id")
    private Long id;

    @NonNull
    @Column(name = "username", unique = true)
    private String username;

    @NonNull
    @Column(name = "phone", unique = true)
    private String phone;

    @NonNull
    @Column(name = "email", unique = true)
    private String email;

    @NonNull
    @Column(name = "first_name")
    private String firstName;

    @NonNull
    @Column(name = "last_name")
    private String lastName;

    @NonNull
    @Column(name = "patronymic")
    private String patronymic = "";

    @NonNull
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    private AdminRole adminRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (adminRole == AdminRole.ROLE_ADMIN) {
            return List.of(AdminRole.values());
        }
        return List.of(adminRole);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
