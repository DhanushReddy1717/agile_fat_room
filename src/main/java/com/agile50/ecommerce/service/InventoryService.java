package com.agile50.ecommerce.service;

import com.agile50.ecommerce.model.Product;

import java.util.HashMap;
import java.util.Map;

public class InventoryService {
    private final Map<String, Integer> stockByProductId = new HashMap<>();

    public void addStock(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Stock quantity must be greater than zero");
        }

        stockByProductId.merge(product.getId(), quantity, Integer::sum);
    }

    public int getAvailableStock(String productId) {
        return stockByProductId.getOrDefault(productId, 0);
    }

    public boolean hasSufficientStock(String productId, int requestedQuantity) {
        return getAvailableStock(productId) >= requestedQuantity;
    }

    public void deductStock(String productId, int quantity) {
        int current = getAvailableStock(productId);
        stockByProductId.put(productId, current - quantity);
    }

    public void restoreStock(String productId, int quantity) {
        stockByProductId.merge(productId, quantity, Integer::sum);
    }
}
