package community.waterlevel.iot.core.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Response object for authentication tokens.
 * Contains token type, access token, refresh token, and expiration time.
 * Used as the standard response for successful authentication requests.
 *
 * @author Ray.Hao
 * @since 0.0.1
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11
 */
@Schema(description = "Authentication token response object")
@Data
@Builder
public class AuthenticationToken {

    /**
     * The type of the token (e.g., "Bearer").
     */
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

    /**
     * The access token string issued to the client.
     */
    @Schema(description = "Access token")
    private String accessToken;

    /**
     * The refresh token string used to obtain new access tokens.
     */
    @Schema(description = "Refresh token")
    private String refreshToken;

    /**
     * The expiration time of the access token, in seconds.
     */
    @Schema(description = "Expiration time (seconds)")
    private Integer expiresIn;

}
