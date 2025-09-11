package community.waterlevel.iot.common.constant;

/**
 * Defines constant keys for standard JWT claim fields used in the application's tokens.
 * These claims represent user identity, roles, data scope, and other metadata
 * included in the JWT payload for authentication and authorization purposes.
 * 
 * @author haoxr
 * @since 2023/11/24
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 * 
 */
public interface JwtClaimConstants {

    /**
     * The type of the token (e.g., access, refresh).
     */
    String TOKEN_TYPE = "tokenType";

    /**
     * The unique identifier of the user.
     */
    String USER_ID = "userId";

    /**
     * The department ID to which the user belongs.
     */
    String DEPT_ID = "deptId";

    /**
     * The data access scope for the user (e.g., all, department, custom).
     */
    String DATA_SCOPE = "dataScope";

    /**
     * The authorities or roles granted to the user.
     */
    String AUTHORITIES = "authorities";

}