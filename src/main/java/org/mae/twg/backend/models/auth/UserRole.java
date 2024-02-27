package org.mae.twg.backend.models.auth;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

public enum UserRole implements Role {
    USER, AGENT, TWG_ADMIN, GOD;

    private final Set<Role> children = new HashSet<>();

    static {
        AGENT.children.add(USER);
        TWG_ADMIN.children.add(AGENT);
        GOD.children.add(TWG_ADMIN);
    }
    @Component("UserRole")
    @Getter
    static class SpringComponent {
        private final UserRole USER = UserRole.USER;
        private final UserRole AGENT = UserRole.AGENT;
        private final UserRole TWG_ADMIN = UserRole.TWG_ADMIN;
        private final UserRole GOD = UserRole.GOD;
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
