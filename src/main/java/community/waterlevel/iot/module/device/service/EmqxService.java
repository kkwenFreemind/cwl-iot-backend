package community.waterlevel.iot.module.device.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * EMQX API Service
 * Handles EMQX HTTP API calls for device management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmqxService {

    private final WebClient emqxWebClient;

    /**
     * Creates MQTT credentials and topics for a device
     */
    public Mono<EmqxDeviceCredentials> createDeviceCredentials(UUID deviceId, Long deptId) {
        String username = generateUsername(deviceId, deptId);
        String password = generatePassword();
        String clientId = generateClientId(deviceId);
        String telemetryTopic = generateTelemetryTopic(deptId, deviceId);
        String commandTopic = generateCommandTopic(deptId, deviceId);

        // Create user in EMQX
        Map<String, Object> userRequest = new HashMap<>();
        userRequest.put("user_id", username);
        userRequest.put("password", password);
        userRequest.put("is_superuser", false);

        log.info("Creating EMQX user: {} for device: {}", username, deviceId);

        return emqxWebClient.post()
            .uri("/authentication/password_based:built_in_database/users")
            .bodyValue(userRequest)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                response -> response.bodyToMono(String.class)
                    .doOnNext(body -> log.error("EMQX API error response: {}", body))
                    .then(Mono.error(new RuntimeException("EMQX API call failed with status: " + response.statusCode()))))
            .bodyToMono(String.class)
            .doOnSuccess(response -> log.info("Successfully created EMQX user: {} with response: {}", username, response))
            .doOnError(e -> log.error("Failed to create EMQX user: {}", username, e))
            .then(Mono.fromCallable(() -> EmqxDeviceCredentials.builder()
                .username(username)
                .password(password)
                .clientId(clientId)
                .telemetryTopic(telemetryTopic)
                .commandTopic(commandTopic)
                .build()));
    }

    /**
     * Deletes MQTT user from EMQX
     */
    public Mono<Void> deleteDeviceCredentials(String username) {
        log.info("Deleting EMQX user: {}", username);
        
        return emqxWebClient.delete()
            .uri("/authentication/password_based:built_in_database/users/{user_id}", username)
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                response -> response.bodyToMono(String.class)
                    .doOnNext(body -> log.error("EMQX API delete error response: {}", body))
                    .then(Mono.error(new RuntimeException("EMQX API delete failed with status: " + response.statusCode()))))
            .bodyToMono(Void.class)
            .doOnSuccess(v -> log.info("Successfully deleted EMQX user: {}", username))
            .doOnError(e -> log.error("Failed to delete EMQX user: {}", username, e));
    }

    private String generateUsername(UUID deviceId, Long deptId) {
        return String.format("device_%d_%s", deptId, deviceId.toString().substring(0, 8));
    }

    private String generatePassword() {
        // Generate a random 16-character password
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String generateClientId(UUID deviceId) {
        return String.format("client_%s", deviceId.toString().substring(0, 8));
    }

    private String generateTelemetryTopic(Long deptId, UUID deviceId) {
        return String.format("tenants/%d/devices/%s/telemetry", deptId, deviceId.toString().substring(0, 8));
    }

    private String generateCommandTopic(Long deptId, UUID deviceId) {
        return String.format("tenants/%d/devices/%s/commands", deptId, deviceId.toString().substring(0, 8));
    }

    /**
     * EMQX Device Credentials DTO
     */
    @lombok.Builder
    @lombok.Data
    public static class EmqxDeviceCredentials {
        private String username;
        private String password;
        private String clientId;
        private String telemetryTopic;
        private String commandTopic;
    }
}