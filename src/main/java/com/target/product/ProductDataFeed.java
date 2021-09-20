package com.target.targetProject;

import com.target.targetProject.dao.ProductDao;
import com.target.targetProject.domain.ProductTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ProductDataFeed implements CommandLineRunner {


    @Autowired
    ProductDao productDao;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }


    public List<ProductTable> data() {
        List<ProductTable> productList = Arrays.asList(new ProductTable(13860428l, new Double(300.00), LocalDate.now() ),
                new ProductTable(13860429l, new Double(300.00), LocalDate.now() ) ,
                new ProductTable(14860429l, new Double(300.00), LocalDate.now() ),
                new ProductTable(16860429l, new Double(3300.00), LocalDate.now() ));
        return  productList;
    }

    private void initialDataSetup() {

        productDao.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(productDao::save)
                .thenMany(productDao.findAll())
                .subscribe((product -> {
                    log.info("ProductDataFeed, method=initialDataSetup, status=Data Inserted");
                }));
    }
}
