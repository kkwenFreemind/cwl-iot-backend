package community.waterlevel.iot.system.model.bo;

import lombok.Data;

/**
 * VisitCount is a business object representing the number of visits for a
 * specific date.
 * <p>
 * This class encapsulates the date (in yyyy-MM-dd format) and the corresponding
 * visit count, used for reporting and analytics in the IoT backend.
 *
 * @author Ray
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Data
public class VisitCount {

    /**
     * Target date for visit statistics in yyyy-MM-dd format.
     * Specific calendar date for which visit counts are recorded.
     */
    private String date;

    /**
     * Total visit count for the specified date.
     * Number of page visits or user sessions recorded on the target date.
     */
    private Integer count;
}
