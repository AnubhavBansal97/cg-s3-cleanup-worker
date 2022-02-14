package com.utils;

import com.config.ExternalConfigurations;
import com.exceptions.BmsClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Configuration
public class HttpUtils {
    private final ObjectMapper objectMapper;

    @Bean
    public CloseableHttpClient httpClient() {
        return HttpClientBuilder.create().build();
    }

    public static boolean isRequestSuccess(CloseableHttpResponse response) {
        return response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300;
    }

    public static String getBaseUrl(ExternalConfigurations.BmsConfig bmsConfig) {
        return   "http://"
                + bmsConfig.getHostName()
                + ":"
                + bmsConfig.getPort();
    }

    public JsonNode executeRequest(final HttpRequestBase httpRequest) throws BmsClientException {
        try (CloseableHttpResponse response = httpClient().execute(httpRequest)) {
            if (!isRequestSuccess(response)) {
                log.error("service call did not return 2XX response. Response status received: {}.",
                        response.getStatusLine().getStatusCode());
                throw new BmsClientException("service did not give 2XX");
            }

            return objectMapper.readValue(response.getEntity().getContent(), JsonNode.class);
        } catch (BmsClientException e) {
            log.error("Caught exception while making a call to the service.", e);
            throw new BmsClientException("Caught exception while making a call to service", e);
        } catch (IOException e) {
            log.error("Caught exception while reading response content of the service.", e);
            throw new BmsClientException("Caught exception while reading response content of the service", e);
        }
    }
}
