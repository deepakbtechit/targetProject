package com.target.product.service;

import com.target.product.client.TargetWebClient;
import com.target.product.dao.ProductDao;
import com.target.product.domain.CurrentPrice;
import com.target.product.domain.ProductDetails;
import com.target.product.domain.ProductInformation;
import com.target.product.domain.ProductTable;
import com.target.product.exceptionHandler.TargetProductException;
import io.micrometer.core.instrument.Metrics;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


@Component
@Data
@Slf4j
public class ProductService {

    @Autowired
    TargetWebClient targetWebClient;

    @Autowired
    ProductDao productDao;

    public Mono<ProductInformation> getProductResponse(Long id) {
        return productDao.findById(id).log()
                .flatMap(productFromDb -> {
                    return targetWebClient.getProductDescription(id).flatMap(productFromApi -> {
                        return getProductDetails(productFromApi, productFromDb);
                    });
                })
                .doOnError(throwable -> {
                            Metrics.counter("DB_ERROR").increment();
                            throw new TargetProductException("Exception thrown when fetching data from cassandra" + throwable.getLocalizedMessage());
                        }
                );
    }

    public Mono<ResponseEntity<ProductTable>> updateProductDetails(Long productId, ProductInformation product) {
        return productDao.findById(productId).flatMap(productTable -> {
            Double price = product.getCurrentPrice().getValue();
            productTable.setPrice(price);
            productTable.setDate(LocalDate.now());
            return productDao.save(productTable);
        }).map(productTable -> {
            log.info("ProductService:: method=updateProductDetails,ProductId=" + productId + "  status=success");
            return new ResponseEntity<>(productTable, HttpStatus.OK);
        });

    }

    private Mono<ProductInformation> getProductDetails(ProductDetails product, ProductTable productFromDb) {
        ProductInformation response = new ProductInformation(productFromDb.getProductId(), product.getProduct().getItem().getProductDescription().getTitle(), new CurrentPrice(productFromDb.getPrice(), "USD"), null);
        return Mono.just(response);
    }
}
