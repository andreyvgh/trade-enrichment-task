package com.verygoodbank.tes.web.repository.impl;

import com.verygoodbank.tes.web.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductCache productCache;

    @Override
    public String getProductName(int productId) {
        return productCache.getProductName(productId);
    }
}
