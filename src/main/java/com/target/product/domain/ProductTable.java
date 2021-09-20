package com.target.product.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.LocalDate;


@Table(value="PRODUCT_DETAILS")
@Data
@AllArgsConstructor
public class ProductTable {
    @PrimaryKey(value = "PRODUCT_ID")
    private Long productId;

    @Column(value = "PRICE")
    private Double price;

    @Column(value = "create_timestamp")
    private LocalDate date ;
}
