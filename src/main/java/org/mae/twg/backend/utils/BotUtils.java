package org.mae.twg.backend.utils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mae.twg.backend.services.auth.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class BotUtils {
    @Value("${config.bot.botname}")
    private String botName;
    @Value("${config.bot.baseurl}")
    private String botBaseUrl;
    @NonNull
    private UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    public String getBotIntegrationUrl(String username) {
        return "https://t.me/" + botName + "?start=" + username;
    }

    public String getBotIntegrationUrl() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getBotIntegrationUrl(username);
    }

    public void sendTourNotifications() {
        List<Long> userIds = userService.getAdminTelegramIds();

        String requestUrl = botBaseUrl + "/notifications/send/tour";
        try {
            restTemplate.postForEntity(requestUrl, userIds, String.class);
        } catch (RestClientException e) {
            log.warn("An error occurred while sending notifications: " + e);
        }
    }

    public void sendCallNotifications() {
        List<Long> userIds = userService.getAdminTelegramIds();

        String requestUrl = botBaseUrl + "/notifications/send/call";
        try {
            restTemplate.postForEntity(requestUrl, userIds, String.class);
        } catch (RestClientException e) {
            log.warn("An error occurred while sending notifications: " + e);
        }
    }
}
