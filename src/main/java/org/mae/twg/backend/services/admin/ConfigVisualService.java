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
        if (!configRepo.exists(ConfigDisplayEnum.MAP.name())) {
            log.info("Init MAP visibility: true");
            configRepo.add(ConfigDisplayEnum.MAP.name(),
                    String.valueOf(true));
        }
        if (!configRepo.exists(ConfigDisplayEnum.ABOUT_US.name())) {
            log.info("Init ABOUT_US visibility: true");
            configRepo.add(ConfigDisplayEnum.ABOUT_US.name(),
                    String.valueOf(true));
        }
        if (!configRepo.exists(ConfigDisplayEnum.AUTHOR_TOURS.name())) {
            log.info("Init AUTHOR_TOURS visibility: true");
            configRepo.add(ConfigDisplayEnum.AUTHOR_TOURS.name(),
                    String.valueOf(true));
        }
        if (!configRepo.exists(ConfigDisplayEnum.NO_TUR.name())) {
            log.info("Init NO_TUR visibility: true");
            configRepo.add(ConfigDisplayEnum.NO_TUR.name(),
                    String.valueOf(true));
        }
        if (!configRepo.exists(ConfigDisplayEnum.NEWS.name())) {
            log.info("Init NEWS visibility: true");
            configRepo.add(ConfigDisplayEnum.NEWS.name(),
                    String.valueOf(true));
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
