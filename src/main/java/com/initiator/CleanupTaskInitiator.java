package com.initiator;

import com.connectors.BMSClientProvider;
import com.dao.daoImpl.BillingMetadataDaoImpl;
import com.enums.CloudProvider;
import com.utils.EntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CleanupTaskInitiator {


    private final BillingMetadataDaoImpl billingMetadataDaoImpl;
    private final BMSClientProvider bmsClientProvider;


    @EntryPoint
    @Scheduled(cron = "0 0 12 1 * ?")
    public void init(){
        log.info("Starting cleanup script to remove old semi enriched billing files");
        try{
            List<String> customerHashList=bmsClientProvider.listCustomerHashes();
            for(String customerHash:customerHashList){

                List<Map<String,Object>> accounts=bmsClientProvider.getAccountsPerCustomerHash(customerHash);
                for(Map<String,Object> account:accounts){
                    billingMetadataDaoImpl.deleteS3FilesOutsideRange(customerHash,account.get("id").toString(),CloudProvider.valueOf(account.get("provider").toString()));
                }
            }
        }catch(Exception e){
            log.error("Unable to delete the old semi enriched billing files");
        }
    }
}
