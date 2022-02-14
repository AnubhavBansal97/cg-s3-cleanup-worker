package com.dao.daoImpl;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.connectors.S3ClientProvider;
import com.enums.CloudNames;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
public class S3FilesGetterImpl {

    private final S3ClientProvider s3ClientProvider;
    private Integer subStringStartIndex=39;

    @Autowired
    public S3FilesGetterImpl(S3ClientProvider s3ClientProvider){
        this.s3ClientProvider=s3ClientProvider;
    }

    public Set<String> getS3FilesForMonth(String accountId, String customerHash, CloudNames cloudName){
        try{
            String prefix=String.format("%s/%s/%s/semi-enrich/month/",cloudName.getValue(),customerHash,accountId);
            System.out.println(prefix);
            ListObjectsRequest lor=new ListObjectsRequest()
                    .withBucketName(s3ClientProvider.getS3BucketName())
                    .withPrefix(prefix);

            Set<String> s3Files=new HashSet<>();
            ObjectListing objectListing=s3ClientProvider.getS3Client().listObjects(lor);
            List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();
            Set<S3ObjectSummary> s3FilesSet = summaries.stream().collect(Collectors.toSet());
            for(S3ObjectSummary summary:s3FilesSet){
                s3Files.add(String.format("s3://%s/%s",s3ClientProvider.getS3BucketName(),summary.getKey()));
            }

            while(objectListing.isTruncated()){
                summaries = objectListing.getObjectSummaries();
                s3FilesSet = summaries.stream().collect(Collectors.toSet());
                for(S3ObjectSummary summary:s3FilesSet){
                    s3Files.add(String.format("s3://%s/%s",s3ClientProvider.getS3BucketName(),summary.getKey()));
                }
                objectListing = s3ClientProvider.getS3Client().listNextBatchOfObjects(objectListing);
            }

            return s3Files;

        }catch(Exception e){
            log.error("Unable to list the objects listing",e);
            throw e;
        }


    }


    public boolean deleteS3Files(String fileObject){
        try{
            String fileObj=fileObject.substring(this.subStringStartIndex);
            s3ClientProvider.getS3Client().deleteObject(s3ClientProvider.getS3BucketName(),fileObj);
            return true;
        }catch(Exception e){
            log.error("Unable to delete the file with path: {} from S3 Bucket.",fileObject,e);
            throw e;
        }
    }
}
