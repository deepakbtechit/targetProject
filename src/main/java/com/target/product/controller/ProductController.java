package com.target.targetProject.controller;

import com.target.targetProject.domain.ErrorInfo;
import com.target.targetProject.domain.ProductDetails;
import com.target.targetProject.domain.ProductInformation;
import com.target.targetProject.domain.ProductTable;
import com.target.targetProject.exceptionHandler.TargetProductException;
import com.target.targetProject.service.KafkaSender;
import com.target.targetProject.service.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import jnr.ffi.annotations.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@RestController
@Slf4j
public class ProductController {

    public static final String PRODUCT_DETAILS_API = "GET_PRODUCT_DETAILS_API";

    @Autowired
    ProductService productService;

    @Autowired
    KafkaSender kafkaSender;

    @GetMapping("/products/{id}")
    public Mono<ResponseEntity<ProductInformation>> getProductDetails(@PathVariable Long id) {
        Metrics.counter(PRODUCT_DETAILS_API).increment();
        return productService.getProductResponse(id).flatMap(productResponse -> {
                    kafkaSender.send("Product details :"+ productResponse);
                    return Mono.just(ResponseEntity.status(HttpStatus.OK).body(productResponse));
                }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ProductInformation(null,null,null,new ErrorInfo("PRODUCT_NOT_FOUND","No data found in database"))));
    }

    @PutMapping("/products/{id}")
    public  Mono<ResponseEntity<ProductTable>>  updateProduct(@PathVariable("id") Long id, @RequestBody @Valid ProductInformation product){
        return productService.updateProductDetails(id, product).defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductTable(null,null,null)));
    }
}
