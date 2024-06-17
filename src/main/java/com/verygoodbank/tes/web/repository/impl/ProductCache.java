package com.verygoodbank.tes.web.repository.impl;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class ProductCache {
    private static final int MAX_ENTRIES = 1000;
    public static final float LOAD_FACTOR = 0.75f;
    private final Map<Integer, String> productMap;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final String filePath;

    public ProductCache() {
        //decided not introduce parameter but in real
        // system it could be configurable
        this.filePath = "product.csv";
        this.productMap = new LinkedHashMap<>(MAX_ENTRIES, LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    public String getProductName(int productId) {
        lock.readLock().lock();
        try {
            String productName = productMap.get(productId);
            if (productName != null) {
                return productName;
            }
        } finally {
            lock.readLock().unlock();
        }

        lock.writeLock().lock();
        try {
            String productName = productMap.get(productId);
            if (productName == null) {
                productName = loadProductNameFromFile(productId);
                productMap.put(productId, productName);
            }
            return productName;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private String loadProductNameFromFile(int productId) {
        try {
            Resource resource = new ClassPathResource(filePath);
            if (resource.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    reader.readLine();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split(",");
                        int id = Integer.parseInt(fields[0].trim());
                        String productName = fields[1].trim();
                        if (id == productId) {
                            return productName;
                        }
                    }
                }
            } else {
                throw new IOException("File not found: " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load product data", e);
        }
        return "Missing Product Name";
    }
}