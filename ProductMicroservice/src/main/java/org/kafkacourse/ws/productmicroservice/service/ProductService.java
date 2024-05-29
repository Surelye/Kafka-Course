package org.kafkacourse.ws.productmicroservice.service;

import org.kafkacourse.ws.productmicroservice.service.dto.CreateProductDto;

import java.util.concurrent.ExecutionException;

public interface ProductService {
    String createProduct(CreateProductDto createProductDto) throws ExecutionException, InterruptedException;
}
