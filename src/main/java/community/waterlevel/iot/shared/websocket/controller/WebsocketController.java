package community.waterlevel.iot.shared.websocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import community.waterlevel.iot.shared.websocket.model.ChatMessage;

import java.security.Principal;

/**
 * WebsocketController is a REST controller that provides endpoints for
 * WebSocket messaging, supporting both broadcast and point-to-point
 * communication.
 * <p>
 * It exposes APIs for sending messages to all connected clients or to specific
 * users, leveraging Spring's messaging infrastructure. This controller is
 * useful for real-time notifications, chat, and other interactive features in
 * the IoT backend.
 *
 * @author Ray.Hao
 * @since 2.3.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@RestController
@RequestMapping("/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {

    /**
     * SimpMessagingTemplate for sending WebSocket messages.
     */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message The message content to broadcast
     * @return Notification message from the server
     */
    @MessageMapping("/sendToAll")
    @SendTo("/topic/notice")
    public String sendToAll(String message) {
        return "Notification: " + message;
    }

    /**
     * Sends a point-to-point message from one user to another.
     * <p>
     * Simulates a scenario where Zhang San sends a message to Li Si.
     *
     * @param principal The current user (sender)
     * @param username  The username of the recipient
     * @param message   The message content
     */
    @MessageMapping("/sendToUser/{username}")
    public void sendToUser(Principal principal, @DestinationVariable String username, String message) {
        // Sender
        String sender = principal.getName();
        // Receiver
        String receiver = username;

        log.info("Sender:{}; Receiver:{}", sender, receiver);
        // Send message to the specified user, path: /user/{receiver}/queue/greeting
        messagingTemplate.convertAndSendToUser(receiver, "/queue/greeting", new ChatMessage(sender, message));
    }

}
