package com.target.product.client;

import com.target.product.aop.ApiLatencyLog;
import com.target.product.domain.ProductDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Component
public class TargetWebClient extends BaseWebClient {

    @Value("${api.product.baseurl}")
    String baseUrl;

    @Value("${api.product.url}")
    private String myRetailEndpoint;

    @Value("${api.product.connectionTimeOut}")
    private int connectionTimeOut;

    @Value("${api.product.readTimeOut}")
    private int readTimeOut;

    @Value("${api.product.keepAlive}")
    private Boolean keepAlive;

    private static final String ORDER_SERVICE = "orderService";

    @ApiLatencyLog
    public Mono<ProductDetails> getProductDescription(Long productId) {
        return invokeProductService(myRetailEndpoint, HttpMethod.GET, null, ProductDetails.class, productId, "MyRetailApi");
    }

    @Override
    int getConnectionTimeoutMillis() {
        return connectionTimeOut;
    }

    @Override
    int getReadTimeoutSeconds() {
        return readTimeOut;
    }


    @Override
    String getBaseUrl() {
        return baseUrl;
    }
}
