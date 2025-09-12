package community.waterlevel.iot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP Configuration for Community Water Level IoT System.
 * 
 * This configuration class enables AspectJ auto-proxy support to allow
 * AOP functionality throughout the application, including logging aspects
 * and other cross-cutting concerns.
 * 
 * Key Features:
 * - Enables @Aspect annotation processing
 * - Supports method-level logging via @Log annotation
 * - Allows for audit trail and security aspects
 * - Enables performance monitoring aspects
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @version 1.0.0
 * @since 2025-09-12
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
    // Configuration class to enable AOP functionality
    // No additional methods needed - the annotation handles the setup
}