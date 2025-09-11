package community.waterlevel.iot.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import community.waterlevel.iot.system.model.event.DictEvent;
import community.waterlevel.iot.system.service.WebSocketService;
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
 * Implementation of the WebSocket service for unified message delivery and online user management.
 * <p>
 * This service handles WebSocket message broadcasting, user online status tracking, notification delivery,
 * and dictionary event notifications. It integrates with Spring's messaging infrastructure and supports
 * both targeted and broadcast messaging for real-time communication.
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
public class WebSocketServiceImpl implements WebSocketService {

    private final Map<String, UserOnlineInfo> onlineUsers = new ConcurrentHashMap<>();

    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("WebSocket message template initialized");
    }

    @Override
    /**
     * Handles user connection events, registering the user as online and notifying listeners.
     *
     * @param username the username of the connected user
     * @param sessionId the session ID (may be null)
     */
    public void userConnected(String username, String sessionId) {
        String actualSessionId = sessionId != null ? sessionId : "session-" + System.nanoTime();
        UserOnlineInfo info = new UserOnlineInfo(username, actualSessionId, System.currentTimeMillis());
        onlineUsers.put(username, info);
        log.info("User [{}] is online, current number of online users：{}", username, onlineUsers.size());

        notifyOnlineUsersChangeInternal();
    }

    @Override
    /**
     * Handles user disconnection events, removing the user from the online list and notifying listeners.
     *
     * @param username the username of the disconnected user
     */
    public void userDisconnected(String username) {
        onlineUsers.remove(username);
        notifyOnlineUsersChangeInternal();
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
     * Gets the current number of online users.
     *
     * @return the number of online users
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * Checks if a user is currently online.
     *
     * @param username the username to check
     * @return true if the user is online, false otherwise
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    /**
     * Manually triggers a notification to update the number of online users to all clients.
     */
    public void notifyOnlineUsersChange() {
        log.info(
                "Manually trigger the notification of the number of online users, the current number of online users：{}",
                onlineUsers.size());
        sendOnlineUserCount();
    }

    private void sendOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("The message template has not been initialized, so the number of online users cannot be sent.");
            return;
        }

        try {
            int count = onlineUsers.size();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("Number of online users sent: {}", count);
        } catch (Exception e) {
            log.error("Failed to send the number of online users.", e);
        }
    }

    private void notifyOnlineUsersChangeInternal() {
        if (messagingTemplate == null) {
            log.warn(
                    "The message template has not been initialized, so the number of online users cannot be notified.");
            return;
        }
        sendOnlineUserCount();
    }

    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }

    @Override
    /**
     * Broadcasts a dictionary change event to all clients.
     *
     * @param dictCode the code of the changed dictionary
     */
    public void broadcastDictChange(String dictCode) {
        DictEvent event = new DictEvent(dictCode);
        sendDictEvent(event);
    }

    private void sendDictEvent(DictEvent event) {
        if (messagingTemplate == null) {
            log.warn(
                    "The message template has not been initialized, so dictionary update notifications cannot be sent.");
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("Dictionary event notification sent, dictCode: {}", event.getDictCode());
        } catch (JsonProcessingException e) {
            log.error("Failed to send dictionary event", e);
        }
    }

    @Override
    /**
     * Sends a notification message to a specific user.
     *
     * @param username the recipient username
     * @param message the message object to send
     */
    public void sendNotification(String username, Object message) {
        if (messagingTemplate == null) {
            log.warn("The message template has not been initialized, so the user message cannot be sent.");
            return;
        }

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", messageJson);
            log.info("Sent message to user [{}]", username);
        } catch (JsonProcessingException e) {
            log.error("Failed to send message to user [{}]", username, e);
        }
    }

    /**
     * Broadcasts a system message to all connected clients.
     *
     * @param message the message content to broadcast
     */
    public void broadcastMessage(String message) {
        if (messagingTemplate == null) {
            log.warn("The message template has not been initialized, so the broadcast message cannot be sent.");
            return;
        }

        try {
            SystemMessage systemMessage = new SystemMessage("System", message, System.currentTimeMillis());
            String messageJson = objectMapper.writeValueAsString(systemMessage);
            messagingTemplate.convertAndSend("/topic/public", messageJson);
            log.info("Broadcast message sent: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to send broadcast message", e);
        }
    }

    @Data
    public static class SystemMessage {
        private String sender;
        private String content;
        private long timestamp;

        public SystemMessage(String sender, String content, long timestamp) {
            this.sender = sender;
            this.content = content;
            this.timestamp = timestamp;
        }
    }
}
