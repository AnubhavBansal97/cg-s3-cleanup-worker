package com.dao.daoImpl;

import com.dao.BillingMetadataDao;
import com.entity.BillingMetadataEntity;
import com.entity.OtherFilesFromOtherMonthsObject;
import com.entity.SemiEnrichedFilesObject;
import com.enums.CloudNames;
import com.enums.CloudProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repository.BillingMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Slf4j
@Component

public class BillingMetadataDaoImpl implements BillingMetadataDao {

    private final BillingMetadataRepository billingMetadataRepository;
    private final S3FilesGetterImpl s3FilesGetterImpl;

    @Autowired
    public BillingMetadataDaoImpl(BillingMetadataRepository billingMetadataRepository,S3FilesGetterImpl s3FilesGetterImpl) {
        this.billingMetadataRepository = billingMetadataRepository;
        this.s3FilesGetterImpl=s3FilesGetterImpl;
    }

    public Set<String> getAccountBilllingMetadata(final String accountId, final String customerHash, final CloudProvider cloud) throws Exception{
        try{

            Date lastDate=new DateTime().minusMonths(13).toDate();
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM");
            String lastMonth=dateFormat.format(lastDate);

            List<BillingMetadataEntity> multicloudBillingMetadataList=billingMetadataRepository.getAccountBilllingMetadata(accountId,customerHash,cloud.getValue(),lastMonth);
            ObjectMapper objectMapper=new ObjectMapper();
            Set<String> s3FilePaths=new HashSet<String>();

            for(BillingMetadataEntity multicloudBillingMetadata:multicloudBillingMetadataList){
                SemiEnrichedFilesObject semiEnrichedFilesObject=objectMapper.treeToValue(multicloudBillingMetadata.getSemiEnrichedFiles(), SemiEnrichedFilesObject.class);
                Map<String, Map<String,OtherFilesFromOtherMonthsObject>> otherFilesFromOtherMonths=semiEnrichedFilesObject.getOtherFilesFromOtherMonths();
                Map<String,List<String>> semiEnrichedFilesMap=semiEnrichedFilesObject.getActualFilesForCurrentMonth();

                //add all the files from other months from other months in the set as well
                for(String date:otherFilesFromOtherMonths.keySet()){
                    for(String otherMonths:otherFilesFromOtherMonths.get(date).keySet()){
                        Set<String> otherMonthS3FileSet = otherFilesFromOtherMonths.get(date).get(otherMonths).getSemiEnrichedFiles().stream().collect(Collectors.toSet());
                        s3FilePaths.addAll(otherMonthS3FileSet);
                    }
                }

                //add all the file paths for given customer Hash and account uuid and for a given cloud
                for(String date:semiEnrichedFilesMap.keySet()){
                    Set<String> s3FilesSet = semiEnrichedFilesMap.get(date).stream().collect(Collectors.toSet());
                    s3FilePaths.addAll(s3FilesSet);
                }
            }
            return s3FilePaths;

        }catch(Exception e){
            log.error("Encountered an exception while getting billing metadata. AccountId: {}, CustomerHash: {}.",
                    accountId, customerHash,e);
            throw e;
        }
    }

    public Set<String> getIntersectionOfSets(final String accountId, final String customerHash, final CloudProvider cloud) throws  Exception{

        try{
            Set<String> s3FilesSet=this.s3FilesGetterImpl.getS3FilesForMonth(accountId,customerHash,CloudNames.valueOf(cloud.toString()));
            Set<String> s3FilePathsSets=this.getAccountBilllingMetadata(accountId,customerHash,cloud);
            Set<String> differnceSet=new HashSet<>(s3FilesSet);
            differnceSet.removeAll(s3FilePathsSets);
            return differnceSet;
        }catch(Exception e){
            log.error("Unable to get the files which need to be deleted",e);
            throw e;
        }

    }

    public void deleteS3FilesOutsideRange(final String accountId, final String customerHash, final CloudProvider cloud) throws Exception{
        try{
            Set<String> differenceSet=this.getIntersectionOfSets(accountId,customerHash,cloud);
            for(String fileObject:differenceSet){
                this.s3FilesGetterImpl.deleteS3Files(fileObject);
            }

        }catch(Exception e ){
            log.error("Unable to delete the files, defined by given time range",e );
            throw e;
        }
    }

}
