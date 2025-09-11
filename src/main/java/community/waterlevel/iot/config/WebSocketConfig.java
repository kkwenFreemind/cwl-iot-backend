package community.waterlevel.iot.config;

import cn.hutool.core.util.StrUtil;
import community.waterlevel.iot.core.security.model.SysUserDetails;
import community.waterlevel.iot.core.security.token.TokenManager;
import community.waterlevel.iot.system.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for WebSocket message broker.
 * Sets up STOMP endpoints, message broker prefixes, and client inbound channel interceptors
 * for authentication, user binding, and connection lifecycle management.
 * Integrates JWT-based authentication and user session tracking for secure real-time communication.
 *
 * @author Ray.Hao
 * @since 3.0.0
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @since 2025/09/11 
 */
@EnableWebSocketMessageBroker
@Configuration
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final TokenManager tokenManager;
  private final WebSocketService webSocketService;

  /**
   * Constructs the WebSocketConfig with required dependencies.
   *
   * @param tokenManager     the token manager for JWT authentication
   * @param webSocketService the WebSocket service for user connection management
   */
  public WebSocketConfig(TokenManager tokenManager, @Lazy WebSocketService webSocketService) {
    this.tokenManager = tokenManager;
    this.webSocketService = webSocketService;
  }

  /**
   * Registers a STOMP endpoint for client WebSocket connections.
   * <p>
   * Configures the endpoint at {@code /ws} and allows cross-origin requests from
   * any origin.
   *
   * @param registry the {@link StompEndpointRegistry} to configure
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setAllowedOriginPatterns("*");
  }

  /**
   * Configures the message broker for WebSocket communication.
   * <p>
   * Sets application destination prefix, enables simple broker for topics and
   * queues, and configures user destination prefix.
   *
   * @param registry the {@link MessageBrokerRegistry} to configure
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/app");
    registry.enableSimpleBroker("/topic", "/queue");
    registry.setUserDestinationPrefix("/user");
  }

  /**
   * Configures the client inbound channel interceptor for WebSocket connections.
   * <p>
   * Core features:
   * <ul>
   * <li>Parses and validates JWT tokens on connection establishment, binding user
   * identity to the session.</li>
   * <li>Triggers user offline notification on connection close.</li>
   * <li>Handles invalid or missing tokens defensively.</li>
   * </ul>
   *
   * @param registration the {@link ChannelRegistration} to configure
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      @Override
      public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
          return ChannelInterceptor.super.preSend(message, channel);
        }

        try {
          // Handle client connection request
          if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Security validation process:
            // 1. Get Authorization value from header
            // 2. Validate Bearer token format
            // 3. Parse and verify JWT
            // 4. Bind user identity to session
            String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            // Defensive check: ensure Authorization header exists and is valid
            if (StrUtil.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
              log.warn("Illegal connection request: missing valid Authorization header");
              throw new AuthenticationCredentialsNotFoundException("Missing authorization header");
            }

            // Extract and process JWT token (remove Bearer prefix)
            String token = authorization.substring(7);
            Authentication authentication = tokenManager.parseToken(token);

            // Handle token parsing failure
            if (authentication == null) {
              log.error("Token parsing failed: {}", token);
              throw new BadCredentialsException("Invalid token");
            }

            // Get user details
            SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
            if (userDetails == null || StrUtil.isBlank(userDetails.getUsername())) {
              log.error("Invalid user credentials: {}", token);
              throw new BadCredentialsException("Invalid user credentials");
            }

            String username = userDetails.getUsername();
            log.info("WebSocket connection established: user [{}]", username);

            // Bind user identity to session (important for @SendToUser, etc.)
            accessor.setUser(authentication);

            // Record user online status
            webSocketService.userConnected(username, accessor.getSessionId());

          }
          // Handle client disconnect request
          else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            // Only trigger offline event for successfully authenticated connections
            // Prevent dirty data from unauthenticated connections
            Authentication authentication = (Authentication) accessor.getUser();
            if (authentication != null && authentication.isAuthenticated()) {
              String username = ((SysUserDetails) authentication.getPrincipal()).getUsername();
              log.info("WebSocket connection closed: user [{}]", username);

              // Record user offline status
              webSocketService.userDisconnected(username);
            }
          }
        } catch (AuthenticationException ex) {
          // Force close connection on authentication failure
          log.error("Connection authentication failed: {}", ex.getMessage());
          throw ex;
        } catch (Exception ex) {
          // Catch all other unknown exceptions
          log.error("WebSocket connection processing error:", ex);
          throw new MessagingException("Connection processing failed");
        }

        return ChannelInterceptor.super.preSend(message, channel);
      }
    });
  }
}