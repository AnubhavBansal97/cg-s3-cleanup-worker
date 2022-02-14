package com.config;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ExternalConfigurations {

    private AwsS3Credentials awsS3Credentials;
    private S3Config s3Config;
    private BmsConfig bmsConfig;


    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class AwsS3Credentials {
        private String accessKey;
        private String secretKey;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class S3Config {
        private String baseBucketName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class BmsConfig {

        private String hostName;
        private Integer port;
        private String activeAccountsUrl;
    }
}
