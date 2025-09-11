package community.waterlevel.iot.system.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * System log JPA entity class for recording application operation logs.
 * Captures detailed information about HTTP requests, responses, and execution
 * context.
 * Supports soft delete functionality for log data retention.
 *
 * @author Ray.Hao
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
@Entity
@Table(name = "sys_log")
@Getter
@Setter
@SQLDelete(sql = "UPDATE sys_log SET is_deleted = 1 WHERE id = ?")
@SQLRestriction("is_deleted = 0")
public class LogJpa {

    /**
     * Primary key for the log record.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Module name where the logged operation occurred.
     * Used to categorize logs by functional modules for easier filtering.
     */
    @Column(name = "module", nullable = false, length = 50)
    private String module;

    /**
     * HTTP request method used for the operation.
     * Common values: GET, POST, PUT, DELETE, PATCH.
     */
    @Column(name = "request_method")
    private String requestMethod;

    /**
     * Request parameters sent with the HTTP request.
     * Stored as text to accommodate large parameter sets.
     */
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    /**
     * Response content returned by the server.
     * Stored as text to accommodate large response bodies.
     */
    @Column(name = "response_content", columnDefinition = "TEXT")
    private String responseContent;

    /**
     * Log content describing the operation performed.
     * Human-readable description of what action was taken.
     */
    @Column(name = "content")
    private String content;

    /**
     * Request URI path that was accessed.
     * The endpoint path without domain and query parameters.
     */
    @Column(name = "request_uri")
    private String requestUri;

    /**
     * IP address of the client making the request.
     * Used for security monitoring and geographical analysis.
     */
    @Column(name = "ip")
    private String ip;

    /**
     * Province/state derived from the client's IP address.
     * Geographical location information for analytics.
     */
    @Column(name = "province")
    private String province;

    /**
     * City derived from the client's IP address.
     * More specific geographical location information.
     */
    @Column(name = "city")
    private String city;

    /**
     * Browser name used by the client.
     * Extracted from User-Agent header for compatibility analysis.
     */
    @Column(name = "browser")
    private String browser;

    /**
     * Version of the browser used by the client.
     * Detailed browser version information for debugging purposes.
     */
    @Column(name = "browser_version")
    private String browserVersion;

    /**
     * Operating system of the client device.
     * Extracted from User-Agent header for platform analysis.
     */
    @Column(name = "os")
    private String os;

    /**
     * Execution time of the operation in milliseconds.
     * Performance metric for monitoring system response times.
     */
    @Column(name = "execution_time")
    private Long executionTime;

    /**
     * Method name that was executed.
     * The specific controller or service method that handled the request.
     */
    @Column(name = "method")
    private String method;

    /**
     * ID of the user who performed the operation.
     * Links the log entry to the user for audit trail purposes.
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * Timestamp when the log entry was created.
     * Records the exact time when the operation occurred.
     */
    @Column(name = "create_time")
    private java.time.LocalDateTime createTime;

    /**
     * Logical delete flag for soft delete functionality.
     * 0 = Not deleted, 1 = Deleted. Default value is 0.
     */
    @Column(name = "is_deleted")
    private Integer isDeleted = 0;
}
