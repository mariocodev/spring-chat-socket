package py.com.mcodev.springchatsocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import py.com.mcodev.springchatsocket.util.DateUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@RestController
@Slf4j
public class HealthCheckController {

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        log.info("Keep Render (chat) alive");
        return Map.of(
                "status", "UP",
                "timestamp", DateUtils.getFormatDate(
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                        DateUtils.DEFAULT_DATE_TIME_ESP)
        );
    }
}