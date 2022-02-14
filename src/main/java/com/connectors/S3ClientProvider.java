package com.connectors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import javax.annotation.PostConstruct;
import com.config.ExternalConfigurations;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class S3ClientProvider {

    private final ExternalConfigurations externalConfig;

    @Getter
    private AmazonS3 s3Client;

    @PostConstruct
    private void init() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials())).withRegion(Regions.US_WEST_2).build();

    }

    private AWSCredentials getAwsCredentials() {
        try {
            return new BasicAWSCredentials(externalConfig.getAwsS3Credentials()
                    .getAccessKey(), externalConfig.getAwsS3Credentials().getSecretKey());
        } catch (Exception e) {
            log.error("Unable to load the AWS S3 credentials from the configuration.");
            throw new AmazonClientException(
                    "Unable to load the AWS S3 credentials from the configuration.", e);
        }
    }


    public String getS3BucketName(){
        try{
            return externalConfig.getS3Config().getBaseBucketName();
        }catch(Exception e){
            log.error("Unable to get the bucket name from which files need to be accessed");
            throw new AmazonClientException(
                    "Unable to get the bucket name from which files need to be accessed",e);
        }
    }

}