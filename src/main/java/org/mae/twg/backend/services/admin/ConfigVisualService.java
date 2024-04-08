package org.mae.twg.backend.services.admin;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.repositories.admin.ConfigVisualRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConfigVisualService {
    @NonNull
    private final ConfigVisualRepo configRepo;
    @PostConstruct
    public void init() {
        for (ConfigDisplayEnum displayEnum : ConfigDisplayEnum.values()) {
            if (!configRepo.exists(displayEnum.name())) {
                log.info("Init " + displayEnum.name() + " visibility: true");
                configRepo.add(displayEnum.name(),
                        String.valueOf(true));
            }
        }
    }
    public Map<ConfigDisplayEnum, Boolean> put(Map<ConfigDisplayEnum, Boolean> config) {
        log.debug("Start ConfigVisualRepo.put");
        for (Map.Entry<ConfigDisplayEnum, Boolean> entry : config.entrySet()) {
            String key = entry.getKey().name();
            String value = entry.getValue().toString();
            configRepo.add(key, value);
        }
        log.debug("End ConfigVisualRepo.put");
        return getAll();
    }
    public Map<ConfigDisplayEnum, Boolean> getAll() {
        log.debug("Start ConfigVisualRepo.getAll");
        Map<String, String> configs = configRepo.findAll();
        Map<ConfigDisplayEnum, Boolean> result = new HashMap<>();
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            result.put(ConfigDisplayEnum.valueOf(entry.getKey()), Boolean.valueOf(entry.getValue()));
        }
        log.debug("End ConfigVisualRepo.getAll");
        return result;
    }
}
