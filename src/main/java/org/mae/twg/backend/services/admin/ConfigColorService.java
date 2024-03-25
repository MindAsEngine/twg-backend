package org.mae.twg.backend.services.admin;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.repositories.admin.ConfigColorRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class ConfigColorService {
    @NonNull
    private final ConfigColorRepo configRepo;
    @PostConstruct
    public void init() {
        if (!configRepo.exists(ConfigDisplayEnum.MAP.name())) {
            log.info("Init MAP visibility: black");
            configRepo.add(ConfigDisplayEnum.MAP.name(), "black");
        }
        if (!configRepo.exists(ConfigDisplayEnum.ABOUT_US.name())) {
            log.info("Init ABOUT_US visibility: black");
            configRepo.add(ConfigDisplayEnum.ABOUT_US.name(),"black");
        }
        if (!configRepo.exists(ConfigDisplayEnum.AUTHOR_TOURS.name())) {
            log.info("Init AUTHOR_TOURS visibility: black");
            configRepo.add(ConfigDisplayEnum.AUTHOR_TOURS.name(),"black");
        }
        if (!configRepo.exists(ConfigDisplayEnum.NO_TUR.name())) {
            log.info("Init NO_TUR visibility: black");
            configRepo.add(ConfigDisplayEnum.NO_TUR.name(),"black");
        }
        if (!configRepo.exists(ConfigDisplayEnum.NEWS.name())) {
            log.info("Init NEWS visibility: black");
            configRepo.add(ConfigDisplayEnum.NEWS.name(),"black");
        }
    }
    public Map<String, String> put(Map<ConfigDisplayEnum, String> config) {
        for (Map.Entry<ConfigDisplayEnum, String> entry : config.entrySet()) {
            String key = entry.getKey().name();
            String value = entry.getValue();
            configRepo.add(key, value);
        }
        return getAll();
    }
    public Map<String, String> getAll() {
        return configRepo.findAll();
    }
}