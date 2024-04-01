package org.mae.twg.backend.utils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.mae.twg.backend.services.auth.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BotUtils {
    @Value("${config.bot.botname}")
    private String botName;
    @Value("${config.bot.baseurl}")
    private String botBaseUrl;
    @NonNull
    private UserService userService;

    public String getBotIntegrationUrl(String username) {
        return "https://t.me/" + botName + "?start=" + username;
    }

    public String getBotIntegrationUrl() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getBotIntegrationUrl(username);
    }

    public void sendNotifications() {
        List<Long> userIds = userService.getAdminTelegramIds();

        RestTemplate template = new RestTemplate();
        String requestUrl = botBaseUrl + "/notifications/send";

        template.postForEntity(requestUrl, userIds, String.class);
    }
}
