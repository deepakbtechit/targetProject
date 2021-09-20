package com.target.targetProject.dao;

import com.target.targetProject.domain.ProductTable;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface ProductDao extends ReactiveCassandraRepository<ProductTable, Long> {
}
