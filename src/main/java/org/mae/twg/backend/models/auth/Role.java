package org.mae.twg.backend.models.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public interface Role extends GrantedAuthority {
    boolean includes(Role role);

    static Set<Role> roots() {
        return Set.of(UserRole.GOD);
    }
}
