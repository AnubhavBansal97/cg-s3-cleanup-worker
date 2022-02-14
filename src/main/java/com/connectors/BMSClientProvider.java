package com.connectors;

import com.config.ExternalConfigurations;
import com.enums.CloudProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.exceptions.BmsClientException;
import com.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;


@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BMSClientProvider {

    private final ExternalConfigurations externalConfig;

    private final HttpUtils httpUtils;
    private final ObjectMapper objectMapper ;

    private static final String CUSTOMER_HASH_LIST_PATH = "/Dashboard/internal/v1/customers";
    private static final String ACCOUNTS_PER_CUSTOMER_HASH = "/Dashboard/internal/v1/customers/%s/accounts";

    public List<String> listCustomerHashes() throws BmsClientException{
        URIBuilder builder;
        HttpGet request;
        try {
            builder = new URIBuilder(getCustomerHashListPath());
            request = new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            log.error("URISyntaxException caught while generating URI for fetching customerHash list", e);
            throw new BmsClientException("URISyntaxException caught while generating URI for fetching customerHash list", e);
        }

        try {
            JsonNode responseBody = httpUtils.executeRequest(request);
            return (List<String>) objectMapper.treeToValue(responseBody, List.class);
        } catch (BmsClientException e) {
            log.error("Caught exception while fetching customerHash list for request :{}", request, e);
            throw new BmsClientException("Caught exception while fetching customerHash list", e);
        }
        catch (JsonProcessingException e) {
            log.error(
                    "Caught exception while de-serializing bms-service response for fetching customerHash list to List " +
                            "for request: {}", request, e);
            throw new BmsClientException(
                    "Caught exception while de-serializing bms-service response for fetching customerHash list to List", e);
        }
    }

    public List<Map<String, Object>> getAccountsPerCustomerHash(String customerHash) throws BmsClientException{


        checkEmptyString(customerHash, "customerHash can't be null/empty");
        URIBuilder builder;
        HttpGet request;
        try {
            builder = new URIBuilder(getAccountsPerCustomerHashPath(customerHash));
            request = new HttpGet(builder.build());

        } catch (URISyntaxException e) {
            log.error("URISyntaxException caught while generating URI for fetching accounts for customerHash {}", customerHash,  e);
            throw new BmsClientException("URISyntaxException caught while generating URI for fetching accounts per customerHash", e);
        }

        try{
            JsonNode responseBody = httpUtils.executeRequest(request);
            return  (List<Map<String, Object>>) objectMapper.treeToValue(responseBody, List.class);
        } catch (BmsClientException e) {
            log.error("Caught exception while fetching accounts for request :{} and customerHash :{}"
                    , request, customerHash, e);
            throw new BmsClientException("Caught exception while fetching accounts", e);
        }
        catch (JsonProcessingException e) {
            log.error(
                    "Caught exception while de-serializing bms-service response for fetching accounts to List " +
                            "for request: {}, customerHash: {}", request, customerHash, e);
            throw new BmsClientException(
                    "Caught exception while de-serializing bms-service response for fetching accounts to List", e);
        }
    }

    private String getCustomerHashListPath(){
        return HttpUtils.getBaseUrl(externalConfig.getBmsConfig()) + CUSTOMER_HASH_LIST_PATH;
    }

    private void checkEmptyString(final String parameter, final String errorStatement) throws BmsClientException {
        if (StringUtils.isEmpty(parameter)) {
            log.error(errorStatement);
            throw new BmsClientException(errorStatement);
        }
    }

    private String getAccountsPerCustomerHashPath(String customerHash) {
        return HttpUtils.getBaseUrl(externalConfig.getBmsConfig()) + String.format(ACCOUNTS_PER_CUSTOMER_HASH, customerHash);
    }
}
