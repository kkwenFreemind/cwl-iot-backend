package community.waterlevel.iot.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing user online status and related statistics.
 * <p>
 * Maintains the online status of users, provides real-time online user statistics,
 * and notifies clients of online user count changes via WebSocket messaging.
 * </p>
 *
 * @author Ray.Hao
 * @since 3.0.0
 *
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Service
@Slf4j
public class UserOnlineService {

    private final Map<String, UserOnlineInfo> onlineUsers = new ConcurrentHashMap<>();

    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserOnlineService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Registers a user as online and adds their session information.
     *
     * @param username  the username of the user
     * @param sessionId the session ID (if null, a new one is generated)
     */
    public void userConnected(String username, String sessionId) {
        // Generate session ID if not provided
        String actualSessionId = sessionId != null ? sessionId : "session-" + System.nanoTime();
        UserOnlineInfo info = new UserOnlineInfo(username, actualSessionId, System.currentTimeMillis());
        onlineUsers.put(username, info);
        log.info("User [{}] connected, current online user count: {}", username, onlineUsers.size());

        notifyOnlineUsersChange();
    }

    /**
     * Removes a user from the online list when they disconnect.
     *
     * @param username the username of the user
     */
    public void userDisconnected(String username) {
        onlineUsers.remove(username);
        log.info("User [{}] disconnected, current online user count: {}", username, onlineUsers.size());

        notifyOnlineUsersChange();
    }

    /**
     * Retrieves a list of currently online users.
     *
     * @return a list of online user DTOs
     */
    public List<UserOnlineDTO> getOnlineUsers() {
        return onlineUsers.values().stream()
                .map(info -> new UserOnlineDTO(info.getUsername(), info.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the current count of online users.
     *
     * @return the number of online users
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * Checks if a specific user is currently online.
     *
     * @param username the username to check
     * @return true if the user is online, false otherwise
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    /**
     * Notifies clients of changes in the online user count via WebSocket.
     * If the messaging template is not initialized, logs a warning.
     */
    private void notifyOnlineUsersChange() {
        if (messagingTemplate == null) {
            log.warn("Messaging template is not initialized, cannot send online user count");
            return;
        }
        sendOnlineUserCount();
    }

    /**
     * Sends the current online user count to subscribed clients via WebSocket.
     * If the messaging template is not initialized, logs a warning.
     */
    private void sendOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("Messaging template is not initialized, cannot send online user count");
            return;
        }

        try {
            int count = onlineUsers.size();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("Sent online user count: {}", count);
        } catch (Exception e) {
            log.error("Failed to send online user count", e);
        }
    }

    /**
     * Internal class representing online user session information.
     */
    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    /**
     * Data Transfer Object for online user information.
     */
    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    /**
     * Event class for representing changes in the online users list.
     */
    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }
}
