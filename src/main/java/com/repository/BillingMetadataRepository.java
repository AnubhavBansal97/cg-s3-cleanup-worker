package com.repository;

import com.entity.BillingMetadataEntity;
import com.enums.CloudNames;
import com.enums.CloudProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingMetadataRepository extends JpaRepository<BillingMetadataEntity, String>{

    @Query(value="select * from cg_multicloud_billing_metadata where account_uuid= ? and customer_hash= ? and cloud= ? and month>= ?",nativeQuery = true)
    List<BillingMetadataEntity> getAccountBilllingMetadata(final String accountId,final String customerHash,final Integer cloudProvider,final String lastMonth);
}
