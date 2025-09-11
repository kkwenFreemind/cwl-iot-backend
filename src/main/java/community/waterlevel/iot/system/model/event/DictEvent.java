package community.waterlevel.iot.system.model.event;

import lombok.Data;

/**
 * DictEvent is an event object representing a dictionary update action in the
 * system.
 * <p>
 * This class encapsulates the dictionary code and the timestamp of the update,
 * and is used for event-driven dictionary synchronization and notification in
 * the IoT backend.
 *
 * @author Ray.Hao
 * @since 3.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class DictEvent {
    /**
     * Dictionary code identifier for the updated dictionary category.
     * Specifies which dictionary category has been modified to enable targeted
     * cache invalidation.
     */
    private String dictCode;

    /**
     * Event occurrence timestamp for chronological tracking.
     * Unix timestamp recording when the dictionary update event was generated.
     */
    private long timestamp;

    /**
     * Constructor for creating dictionary update events.
     * Initializes the event with the specified dictionary code and current
     * timestamp
     * for immediate event processing and cache synchronization.
     *
     * @param dictCode the code of the dictionary category that was updated
     */
    public DictEvent(String dictCode) {
        this.dictCode = dictCode;
        this.timestamp = System.currentTimeMillis();
    }
}
