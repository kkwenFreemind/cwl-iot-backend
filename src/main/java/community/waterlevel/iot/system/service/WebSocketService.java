package community.waterlevel.iot.system.service;

/**
 * WebSocket service interface for managing real-time communication and
 * notifications.
 * <p>
 * Provides methods for handling user connection events, broadcasting dictionary
 * data changes,
 * and sending system notifications to users via WebSocket.
 * </p>
 *
 * @author Ray.Hao
 * @since 3.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
public interface WebSocketService {

    /**
     * Handles the event when a user establishes a WebSocket connection.
     *
     * @param username  the username of the connected user
     * @param sessionId the session ID of the connection
     */
    void userConnected(String username, String sessionId);

    /**
     * Handles the event when a user disconnects from the WebSocket.
     *
     * @param username the username of the disconnected user
     */
    void userDisconnected(String username);

    /**
     * Broadcasts a notification to all clients that a dictionary entry has changed.
     *
     * @param dictCode the code of the dictionary that has changed
     */
    void broadcastDictChange(String dictCode);

    /**
     * Sends a system notification message to a specific user via WebSocket.
     *
     * @param username the username of the recipient
     * @param message  the notification message object
     */
    void sendNotification(String username, Object message);
}
