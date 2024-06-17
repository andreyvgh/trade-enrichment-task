package com.verygoodbank.tes.repository;

import com.verygoodbank.tes.web.repository.impl.ProductCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ProductCacheTest {

    private ProductCache productCache;
    @BeforeEach
    void setUp() {
        productCache= new ProductCache();
    }

    @Test
    public void testGetProductNameFoundInCache() {
        productCache.getProductName(1);
        String productName = productCache.getProductName(1);
        assertEquals("Treasury Bills Domestic", productName);
    }

    @Test
    public void testGetProductNameNotFoundInCache() {
        String productName = productCache.getProductName(999);
        assertEquals("Missing Product Name", productName);
    }

    @Test
    public void testLoadProductNameFromFile() {
        String productName = productCache.getProductName(2);
        assertEquals("Corporate Bonds Domestic", productName);
    }

    @Test
    public void testLoadProductNameFromFileNotFound() {
        String productName = productCache.getProductName(999);
        assertEquals("Missing Product Name", productName);
    }
}
