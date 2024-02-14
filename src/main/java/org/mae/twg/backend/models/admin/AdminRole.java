package org.mae.twg.backend.models.admin;

import org.springframework.security.core.GrantedAuthority;

public enum AdminRole implements GrantedAuthority {
    ROLE_MODERATOR,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
