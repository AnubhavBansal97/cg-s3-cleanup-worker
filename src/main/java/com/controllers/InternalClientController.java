package com.controllers;

import com.connectors.S3ClientProvider;
import com.dao.daoImpl.BillingMetadataDaoImpl;
import com.dao.daoImpl.S3FilesGetterImpl;
import com.connectors.BMSClientProvider;
import com.enums.CloudNames;
import com.enums.CloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;


@RestController
@RequestMapping()
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InternalClientController {

    private final S3ClientProvider s3ClientProvider;
    private final BillingMetadataDaoImpl billingMetadataDaoImpl;
    private final BMSClientProvider bmsClientProvider;
    private final S3FilesGetterImpl s3FilesGetterImpl;

    @GetMapping("/test/{accountId}/{customerHash}")
    public void getBucketDetails(@NotNull @PathVariable("accountId") String accountIdtemp,@NotNull @PathVariable("customerHash") String custom) {
        try{
            List<String> customerHashList=bmsClientProvider.listCustomerHashes();
            for(String customerHash:customerHashList){

                List<Map<String,Object> > accounts=bmsClientProvider.getAccountsPerCustomerHash(customerHash);
                for(Map<String,Object> account:accounts){
                    String accountId=account.get("id").toString();
                    CloudProvider cloud=CloudProvider.valueOf(account.get("provider").toString());
                    System.out.println(account);
                    billingMetadataDaoImpl.deleteS3FilesOutsideRange(customerHash,account.get("id").toString(),CloudProvider.valueOf(account.get("provider").toString()));
                }

            }
        }catch(Exception e){


        }
    }

}
