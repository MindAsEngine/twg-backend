package org.mae.twg.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.models.ConfigParam;
import org.mae.twg.backend.repositories.ConfigRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigService {
    @NonNull
    private final ConfigRepo configRepo;

    @Value("${config.jwt.refresh.expiration_hours}")
    private Long refreshExpirationHours;
    @Value("${config.jwt.access.expiration_hours}")
    private Long accessExpirationHours;
    private final String refreshExpirationKey = "refresh_token_expiration";
    private final String accessExpirationKey = "access_token_expiration";

    @PostConstruct
    public void init() {
        if (!configRepo.existsById(refreshExpirationKey)) {
            configRepo.save(new ConfigParam(
                    refreshExpirationKey,
                    String.valueOf(refreshExpirationHours)));
        }
        if (!configRepo.existsById(accessExpirationKey)) {
            configRepo.save(new ConfigParam(
                    accessExpirationKey,
                    String.valueOf(accessExpirationHours)));
        }
    }

    public Integer getRefreshExpiration() {
        return Integer.valueOf(
                get(refreshExpirationKey)
                        .getValue());
    }

    public Integer getAccessExpiration() {
        return Integer.valueOf(
                get(accessExpirationKey)
                        .getValue());
    }

    public ConfigParam put(String key, String value) {
        Optional<ConfigParam> current = configRepo.findById(key);
        if (current.isEmpty()) {
            return configRepo.save(new ConfigParam(key, value));
        } else {
            ConfigParam cur_config = current.get();
            cur_config.setValue(value);
            return configRepo.save(current.get());
        }
    }

    public ConfigParam get(String key) {
        return configRepo.findById(key)
                .orElseThrow(() -> new ObjectNotFoundException("Config " + key + " not found"));
    }

    public List<ConfigParam> getAll() {
        return configRepo.findAll();
    }

    public void delete(String key) {
        configRepo.deleteById(key);
    }
}
