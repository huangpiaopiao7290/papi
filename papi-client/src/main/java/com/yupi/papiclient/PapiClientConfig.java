package com.yupi.papiclient;

import com.yupi.papiclient.client.PClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * papi客户端配置
 */
@Configuration
@ConfigurationProperties("papi.client")
@Data
@Component
public class PapiClientConfig {
    private String accessKey;
    private String secretKey;

    @Bean
    public PClient pClient() {
        return new PClient(accessKey, secretKey);
    }
}
