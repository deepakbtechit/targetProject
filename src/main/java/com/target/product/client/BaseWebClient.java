package com.target.targetProject.client;

import com.target.targetProject.exceptionHandler.TargetProductException;
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

@Data
@Slf4j
abstract class BaseWebClient {


    public static final String API_CALL_LATENCY = "PRODUCT_API_CALL_LATENCY";
    public static final String TARGET_CALL_FAILURE = "EXTERNAL_TARGET_CALL_FAILURE";
    public static final String TARGET_CALL_SUCCESS = "EXTERNAL_TARGET_CALL_SUCCESS";
    WebClient targetWebClient;

    @Autowired
    ReactorResourceFactory reactorResourceFactory;

    abstract int getConnectionTimeoutMillis ();
    abstract int getReadTimeoutSeconds ();
    abstract boolean getKeepAlive();
    abstract String getBaseUrl();

    void init () {
         this.targetWebClient = WebClient.builder().baseUrl(getBaseUrl()).clientConnector(new ReactorClientHttpConnector(reactorResourceFactory, httpClient ->
                httpClient.followRedirect(true).tcpConfiguration(tcpClient ->
                        tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectionTimeoutMillis()).doOnConnected(connection -> {
                            connection.markPersistent(getKeepAlive()).addHandlerLast(new ReadTimeoutHandler(getReadTimeoutSeconds()));
                        })))).build();
    }

     protected <B,T> Mono<T> invokeProductService(String url, HttpMethod httpMethod, HttpHeaders headers, Class<T> responseObject, B productId, String extraLogInfo) {
         log.info("BaseWebClient, method=invokeProductService, clientName="+ extraLogInfo);
         long start = System.currentTimeMillis();
         init();
         return targetWebClient.method(httpMethod).uri(uriBuilder -> uriBuilder.path(url).queryParam("excludes","taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics")
                         .queryParam("key","candidate")
                 .build(productId)).retrieve().bodyToMono(responseObject).log()
                 .doOnError(throwable -> {
                     Metrics.counter(TARGET_CALL_FAILURE).increment();
                     log.error("BaseWebClient, method=invokeGetService, status=error, request_duration="+ (System.currentTimeMillis()-start) + " exception="+throwable);
                     throw new TargetProductException("Error in fetching data from api");
                 })
                 .doOnSuccess(t -> {
                     Metrics.counter(TARGET_CALL_SUCCESS).increment();
                     log.info("BaseWebClient, method=invokeGetService, status=success, request_duration="+ (System.currentTimeMillis()-start));
                 });
    }


}
