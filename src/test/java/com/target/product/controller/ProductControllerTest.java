package com.target.targetProject.controller;

import com.target.targetProject.dao.ProductDao;
import com.target.targetProject.domain.CurrentPrice;
import com.target.targetProject.domain.ProductInformation;
import com.target.targetProject.domain.ProductTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ProductDao productDao;

    public static final String PRODUCT_END_POINT_V1 = "/products";

    public List<ProductTable> data() {
        List<ProductTable> productList = Arrays.asList(new ProductTable(13860428l, new Double(1300.00), LocalDate.now() ),
                new ProductTable(13860429l, new Double(2300.00), LocalDate.now() ) ,
                new ProductTable(14860429l, new Double(3300.00), LocalDate.now() ),
                new ProductTable(15860429l, new Double(3300.00), LocalDate.now() ));
        return  productList;
    }

    @BeforeEach
    public void setUp() throws IOException {
        productDao.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(productDao::save)
                .doOnNext(( productTable -> {
                })).blockLast();

    }


    @Test
    public void getProductTitle() {
        webTestClient.get().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), "13860428")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ProductInformation.class)
                .consumeWith((response) -> {
                    ProductInformation  productDetails = response.getResponseBody();
                        assert productDetails.getId() == 13860428;
                        assert productDetails.getName() != null;
                });

    }

    @Test
    public void getProductPrice() {
        webTestClient.get().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), "13860428")
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.currentPrice.value",3020.00);
    }

    @Test
    public void getItem_invalid_path() {

        webTestClient.get().uri(PRODUCT_END_POINT_V1.concat( "/target/{id}"), "138604282323")
                .exchange().expectStatus().is4xxClientError();
    }

    @Test
    public void getItem_invalidId() {

        webTestClient.get().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), "138604282323")
                .exchange().expectStatus().is4xxClientError();

    }

    @Test
    public void getItem_idNotFoundInAPI() {

        webTestClient.get().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), "14860429")
                .exchange().expectStatus().is5xxServerError();

    }

    @Test
    public void getAllItems() {

        Flux<ProductInformation> itemFlux = webTestClient.get().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), "13860428")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(ProductInformation.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void updateProduct() {

        CurrentPrice price = new CurrentPrice(new Double(1850), "USD" );
        ProductInformation product = new ProductInformation(13860428l, "The Big Lebowski (Blu-ray)", price, null);

        webTestClient.put().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), "13860428")
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), ProductInformation.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo("1850.0");
    }

    @Test
    public void updateItem_invalidId() {

        CurrentPrice price = new CurrentPrice(new Double(850), "USD" );
        ProductInformation product = new ProductInformation(13860l, "The Big Lebowski (Blu-ray)", price,null);

        webTestClient.put().uri(PRODUCT_END_POINT_V1.concat( "/{id}"), 13860l)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(product), ProductInformation.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}
