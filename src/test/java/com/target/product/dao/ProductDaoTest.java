
package com.target.product.dao;

import com.target.product.domain.ProductTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductDaoTest {

    @Autowired
    ProductDao productDao;

    public List<ProductTable> data() {
        List<ProductTable> productList = Arrays.asList(new ProductTable(13860428l, new Double(1300.00), LocalDate.now()),
                new ProductTable(13860429l, new Double(2300.00), LocalDate.now()),
                new ProductTable(14860429l, new Double(3300.00), LocalDate.now()),
                new ProductTable(15860429l, new Double(3300.00), LocalDate.now()));
        return productList;
    }

    @BeforeEach
    public void setUp() throws IOException {

        // Flux.fromIterable(data()).flatMap(productDao::delete);
        productDao.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(productDao::save)
                .doOnNext((productTable -> {
                    // System.out.println("Inserted"+ productTable);
                })).blockLast();

    }

    @Test
    public void getAllItems() {

        StepVerifier.create(productDao.findAll()).expectSubscription().expectNextCount(4).verifyComplete();
    }

    @Test
    public void getProductById() {
        StepVerifier.create(productDao.findById(13860428l))
                .expectSubscription()
                .expectNextMatches((productTable -> productTable.getPrice().compareTo(new Double(1300.00)) == 0))
                .verifyComplete();
    }

}

