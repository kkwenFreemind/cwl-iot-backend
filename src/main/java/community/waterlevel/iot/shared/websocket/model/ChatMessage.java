package community.waterlevel.iot.shared.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChatMessage is a data model representing a message exchanged via WebSocket in
 * the system.
 * <p>
 * It contains the sender's identifier and the message content, and is used for
 * real-time communication between users or system notifications.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    /**
     * The sender of the message
     */
    private String sender;

    /**
     * The content of the message
     */
    private String content;

}

