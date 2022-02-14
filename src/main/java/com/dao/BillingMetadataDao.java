package com.dao;

import com.entity.BillingMetadataEntity;
import com.enums.CloudProvider;

import java.sql.Timestamp;
import java.util.*;

public interface BillingMetadataDao {

    Set<String> getAccountBilllingMetadata(final String accountId, final String customerHash, final CloudProvider cloud) throws Exception;
}
