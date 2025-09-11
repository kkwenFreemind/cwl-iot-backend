package community.waterlevel.iot.system.model.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * UserSessionDTO is a data transfer object representing user session
 * information.
 * <p>
 * This class encapsulates the username, associated session IDs, and the last
 * active time, and is used for session management and monitoring in the IoT
 * backend.
 *
 * @author Ray.Hao
 * @since 3.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class UserSessionDTO {

    /**
     * System username for session identification.
     * Links session data to the specific user account for tracking purposes.
     */
    private String username;

    /**
     * Collection of active session identifiers.
     * Set of unique session IDs for tracking concurrent user sessions.
     */
    private Set<String> sessionIds;

    /**
     * Last activity timestamp for session lifecycle management.
     * Unix timestamp indicating the most recent user activity for timeout calculations.
     */
    private long lastActiveTime;

    /**
     * Constructor for initializing user session tracking.
     * Creates a new session container with empty session set and current timestamp.
     *
     * @param username the username to associate with this session tracking object
     */
    public UserSessionDTO(String username) {
        this.username = username;
        this.sessionIds = new HashSet<>();
        this.lastActiveTime = System.currentTimeMillis();
    }
}
