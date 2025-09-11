package community.waterlevel.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;



/**
 * Community Water Level IoT Backend Application
 * 
 * This is the main entry point for the Community Water Level IoT monitoring system.
 * The application provides a comprehensive backend solution for real-time water level
 * monitoring in community areas, featuring IoT sensor integration, data analytics,
 * alert mechanisms, and administrative management capabilities.
 * 
 * Key Features:
 * - Real-time water level data collection from IoT sensors
 * - Historical data analysis and trend monitoring
 * - Alert and notification system for abnormal water levels
 * - User management and role-based access control
 * - RESTful APIs for mobile and web applications
 * - Data visualization and reporting capabilities
 * 
 * Technology Stack:
 * - Spring Boot 3.5.0 for rapid application development
 * - Spring Security 6 for authentication and authorization
 * - MyBatis-Plus for database operations
 * - Redis for caching and session management
 * - MySQL for persistent data storage
 * 
 * @author Chang Xiu-Wen, AI-Enhanced
 * @version 1.0.0
 * @since 2025-09-11
 */
@SpringBootApplication
@ConfigurationPropertiesScan 
public class CommunityWaterLevelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityWaterLevelApplication.class, args);
    }

}
