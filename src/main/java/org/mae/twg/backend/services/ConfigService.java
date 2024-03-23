package org.mae.twg.backend.services;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.admin.ConfigDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.repositories.admin.RedisRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConfigService {
    @NonNull
    private final RedisRepo<String, String> configRepo;

    @Value("${config.jwt.refresh.expiration_hours}")
    private Long refreshExpirationHours;
    @Value("${config.jwt.access.expiration_hours}")
    private Long accessExpirationHours;

    @PostConstruct
    public void init() {
        if (!configRepo.exists(ConfigEnum.REFRESH_EXPIRATION_KEY.name())) {
            log.info("Init REFRESH EXPIRATION CONFIG: " + refreshExpirationHours + " hours");
            configRepo.add(ConfigEnum.REFRESH_EXPIRATION_KEY.name(),
                    String.valueOf(refreshExpirationHours));
        }
        if (!configRepo.exists(ConfigEnum.ACCESS_EXPIRATION_KEY.name())) {
            log.info("Init ACCESS EXPIRATION CONFIG: " + accessExpirationHours + " hours");
            configRepo.add(ConfigEnum.ACCESS_EXPIRATION_KEY.name(),
                    String.valueOf(accessExpirationHours));
        }
    }

    public Integer getRefreshExpiration() {
        try {
            return Integer.valueOf(
                    get(ConfigEnum.REFRESH_EXPIRATION_KEY)
                            .getValue());
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Integer getAccessExpiration() {
        try {
            return Integer.valueOf(
                    get(ConfigEnum.ACCESS_EXPIRATION_KEY)
                            .getValue());
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ConfigDTO put(ConfigEnum key, String value) {
        configRepo.add(key.name(), value);
        return new ConfigDTO(key.name(), value);
    }

    public ConfigDTO get(ConfigEnum key) {
        if (!configRepo.exists(key.name())) {
            throw new ObjectNotFoundException("Config " + key + " not found");
        }
        return new ConfigDTO(key.name(), configRepo.find(key.name()));
    }

    public List<ConfigDTO> getAll() {
        Map<String, String> configs = configRepo.findAll();
        return configs.entrySet().stream()
                .map(entry -> new ConfigDTO(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void delete(ConfigEnum key) {
        configRepo.delete(key.name());
    }
}
