package com.target.product.controller;

import com.target.product.constant.ProductConstant;
import com.target.product.domain.ErrorInfo;
import com.target.product.domain.ProductInformation;
import com.target.product.domain.ProductTable;
import com.target.product.service.KafkaSender;
import com.target.product.service.ProductService;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;


@RestController
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    KafkaSender kafkaSender;

    @GetMapping("/products/{id}")
    @Cacheable(key = "#id",value = "product_information")
    public Mono<ResponseEntity<ProductInformation>> getProductDetails(@PathVariable Long id) {
        Metrics.counter(ProductConstant.PRODUCT_DETAILS_API).increment();
        return productService.getProductResponse(id).flatMap(productResponse -> {
            kafkaSender.send("Product details :" + productResponse);
            return Mono.just(ResponseEntity.status(HttpStatus.OK).body(productResponse));
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ProductInformation(null, null, null, new ErrorInfo("PRODUCT_NOT_FOUND", "No data found in database"))));
    }

    @PutMapping("/products/{id}")
    @CacheEvict(key = "#id",value = "product_information")
    public Mono<ResponseEntity<ProductTable>> updateProduct(@PathVariable("id") Long id, @RequestBody @Valid ProductInformation product) {
        return productService.updateProductDetails(id, product).defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ProductTable(null, null, null)));
    }
}
