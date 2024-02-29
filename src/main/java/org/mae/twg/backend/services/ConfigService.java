package org.mae.twg.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.dto.admin.ConfigDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.repositories.admin.RedisRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigService {
    @NonNull
    private final RedisRepo<String, String> configRepo;

    @Value("${config.jwt.refresh.expiration_hours}")
    private Long refreshExpirationHours;
    @Value("${config.jwt.access.expiration_hours}")
    private Long accessExpirationHours;
    private final String refreshExpirationKey = "refresh_token_expiration";
    private final String accessExpirationKey = "access_token_expiration";

    @PostConstruct
    public void init() {
        if (!configRepo.exists(refreshExpirationKey)) {
            configRepo.add(refreshExpirationKey,
                    String.valueOf(refreshExpirationHours));
        }
        if (!configRepo.exists(accessExpirationKey)) {
            configRepo.add(accessExpirationKey,
                    String.valueOf(accessExpirationHours));
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

    public ConfigDTO put(String key, String value) {
        configRepo.add(key, value);
        return new ConfigDTO(key, value);
    }

    public ConfigDTO get(String key) {
        if (configRepo.exists(key)) {
            throw new ObjectNotFoundException("Config " + key + " not found");
        }
        return new ConfigDTO(key, configRepo.find(key));
    }

    public List<ConfigDTO> getAll() {
        Map<String, String> configs = configRepo.findAll();
        return configs.entrySet().stream()
                .map(entry -> new ConfigDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void delete(String key) {
        configRepo.delete(key);
    }
}
