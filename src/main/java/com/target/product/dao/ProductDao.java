package com.target.product.dao;

import com.target.product.domain.ProductTable;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface ProductDao extends ReactiveCassandraRepository<ProductTable, Long> {
}
