package community.waterlevel.iot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Base64;

/**
 * EMQX API Configuration
 * Configures WebClient for EMQX HTTP API communication
 */
@Slf4j
@Configuration
public class EmqxConfig {

    @Value("${emqx.api.url}")
    private String baseUrl;

    @Value("${emqx.api.key}")
    private String apiKey;

    @Value("${emqx.api.secret}")
    private String apiSecret;

    /**
     * Configures WebClient for EMQX API calls
     */
    @Bean
    public WebClient emqxWebClient() {
        // Use API key and secret for authentication
        String auth = apiKey + ":" + apiSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        log.info("Configuring EMQX WebClient with base URL: {}", baseUrl);
        log.debug("Using API key: {}", apiKey);

        return WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Basic " + encodedAuth)
            .defaultHeader("Content-Type", "application/json")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .responseTimeout(Duration.ofSeconds(10))
            ))
            .build();
    }
}