package org.mae.twg.backend.services.admin;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ValidationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.dto.admin.ConfigDTO;
import org.mae.twg.backend.exceptions.ObjectNotFoundException;
import org.mae.twg.backend.repositories.admin.ConfigBusinessRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConfigBusinessService {
    @NonNull
    private final ConfigBusinessRepo configRepo;
    @NonNull
    private final CurrencyService currencyService;

    @Value("${config.jwt.refresh.expiration_hours}")
    private Long refreshExpirationHours;
    @Value("${config.jwt.access.expiration_hours}")
    private Long accessExpirationHours;

    @PostConstruct
    public void init() {
        if (!configRepo.exists(ConfigBusinessEnum.REFRESH_EXPIRATION_KEY.name())) {
            log.info("Init REFRESH EXPIRATION CONFIG: " + refreshExpirationHours + " hours");
            configRepo.add(ConfigBusinessEnum.REFRESH_EXPIRATION_KEY.name(),
                    String.valueOf(refreshExpirationHours));
        }
        if (!configRepo.exists(ConfigBusinessEnum.ACCESS_EXPIRATION_KEY.name())) {
            log.info("Init ACCESS EXPIRATION CONFIG: " + accessExpirationHours + " hours");
            configRepo.add(ConfigBusinessEnum.ACCESS_EXPIRATION_KEY.name(),
                    String.valueOf(accessExpirationHours));
        }
        if (!configRepo.exists(ConfigBusinessEnum.USD_TO_UZS.name())) {
            log.info("Init USD TO UZS: 0.00008");
            configRepo.add(ConfigBusinessEnum.USD_TO_UZS.name(), "0.00008");
        }
        if (!configRepo.exists(ConfigBusinessEnum.USD_TO_RUB.name())) {
            log.info("Init USD TO RUB: 90");
            configRepo.add(ConfigBusinessEnum.USD_TO_RUB.name(), "90.0");
        }
    }

    public Integer getRefreshExpiration() {
        try {
            return Integer.valueOf(
                    get(ConfigBusinessEnum.REFRESH_EXPIRATION_KEY)
                            .getValue());
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Integer getAccessExpiration() {
        try {
            return Integer.valueOf(
                    get(ConfigBusinessEnum.ACCESS_EXPIRATION_KEY)
                            .getValue());
        } catch (ObjectNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ConfigDTO put(ConfigBusinessEnum key, String value) {
        if (key == ConfigBusinessEnum.USD_TO_USD) {
            throw new ValidationException("У нас так не принято");
        }
        if (key == ConfigBusinessEnum.USD_TO_UZS || key == ConfigBusinessEnum.USD_TO_RUB) {
            currencyService.putCurrency(key, value);
        }
        configRepo.add(key.name(), value);
        return new ConfigDTO(key, value);
    }

    public ConfigDTO get(ConfigBusinessEnum key) {
        if (key == ConfigBusinessEnum.USD_TO_USD) {
            return new ConfigDTO(key, "1.0");
        }
        if (!configRepo.exists(key.name())) {
            init();
            if (!configRepo.exists(key.name())) {
                throw new ObjectNotFoundException("Config " + key + " not found");
            }
        }
        return new ConfigDTO(key, configRepo.find(key.name()));
    }

    public List<ConfigDTO> getAll() {
        Map<String, String> configs = configRepo.findAll();
        return configs.entrySet().stream()
                .map(entry -> new ConfigDTO(ConfigBusinessEnum.valueOf(entry.getKey()), entry.getValue()))
                .toList();
    }

    public void delete(ConfigBusinessEnum key) {
        configRepo.delete(key.name());
    }
}
