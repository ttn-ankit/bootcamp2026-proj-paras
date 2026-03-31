package org.example.ecommerce.Config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.s3")
public class S3StorageProperties {
    private String bucket;
    private String region;
    private Duration urlTtl = Duration.ofMinutes(15);
    private String accessKeyId;
    private String secretAccessKey;
}