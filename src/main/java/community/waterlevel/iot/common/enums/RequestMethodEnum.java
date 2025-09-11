package community.waterlevel.iot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumeration of HTTP request methods, corresponding to common RESTful API actions.
 * Used to annotate or determine the request type (GET, POST, PUT, PATCH, DELETE, ALL)
 * in controller layers. Primarily applied for logging, permission control, or
 * identifying request methods in custom annotations.
 *
 * @author Ray
 * @since 2.10.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Getter
@AllArgsConstructor
public enum RequestMethodEnum {

    /**
     * HTTP GET method.
     */
    GET("GET"),

    /**
     * HTTP POST method.
     */
    POST("POST"),

    /**
     * HTTP PUT method.
     */
    PUT("PUT"),

    /**
     * HTTP PATCH method.
     */
    PATCH("PATCH"),

    /**
     * HTTP DELETE method.
     */
    DELETE("DELETE"),

    /**
     * Represents all HTTP methods (wildcard).
     */
    ALL("All");

    /**
     * String representation of the HTTP method.
     */
    private final String type;

    /**
     * Finds the corresponding RequestMethodEnum by string type.
     *
     * @param type the string representation of the HTTP method
     * @return the matching RequestMethodEnum, or ALL if not found
     */
    public static RequestMethodEnum find(String type) {
        for (RequestMethodEnum value : RequestMethodEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return ALL;
    }
}
