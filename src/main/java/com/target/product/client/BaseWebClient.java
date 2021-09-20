package com.target.product.client;

import com.target.product.constant.ProductConstant;
import com.target.product.exceptionHandler.TargetProductException;
import io.micrometer.core.instrument.Metrics;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Data
@Slf4j
abstract class BaseWebClient {

    WebClient targetWebClient;

    @Autowired
    ReactorResourceFactory reactorResourceFactory;

    abstract int getConnectionTimeoutMillis();

    abstract int getReadTimeoutSeconds();

    abstract String getBaseUrl();

    void init() {
        HttpClient client = HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectionTimeoutMillis()).responseTimeout(Duration.ofMillis(getReadTimeoutSeconds()));
        this.targetWebClient = WebClient.builder().baseUrl(getBaseUrl()).clientConnector(new ReactorClientHttpConnector(client)).build();
    }

    protected <B, T> Mono<T> invokeProductService(String url, HttpMethod httpMethod, HttpHeaders headers, Class<T> responseObject, B productId, String extraLogInfo) {
        log.info("BaseWebClient, method=invokeProductService, clientName=" + extraLogInfo);
        long start = System.currentTimeMillis();
        init();
        return targetWebClient.method(httpMethod).uri(uriBuilder -> uriBuilder.path(url).queryParam("excludes", "taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics").queryParam("key", "candidate")
                        .build(productId)).retrieve().bodyToMono(responseObject).log()
                .doOnSuccess(t -> {
                    Metrics.timer(ProductConstant.TARGET_CALL_SUCCESS).record(Duration.ofMillis(System.currentTimeMillis() - start));
                    log.info("BaseWebClient, method=invokeGetService, status=success, request_duration=" + (System.currentTimeMillis() - start));
                })
                .doOnError(throwable -> {
                    Metrics.timer(ProductConstant.TARGET_CALL_FAILURE).record(Duration.ofMillis(System.currentTimeMillis() - start));
                    log.error("BaseWebClient, method=invokeGetService, status=error, request_duration=" + (System.currentTimeMillis() - start) + " exception=" + throwable);
                    throw new TargetProductException("Error in fetching data from api");
                });
    }


}
