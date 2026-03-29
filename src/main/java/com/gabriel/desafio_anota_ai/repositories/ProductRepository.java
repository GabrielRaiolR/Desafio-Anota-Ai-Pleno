package com.gabriel.desafio_anota_ai.repositories;

import com.gabriel.desafio_anota_ai.domain.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository  extends MongoRepository<Product, String> {
}
