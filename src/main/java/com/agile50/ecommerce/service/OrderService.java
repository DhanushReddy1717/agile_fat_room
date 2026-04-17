package com.agile50.ecommerce.service;

import com.agile50.ecommerce.exception.InsufficientInventoryException;
import com.agile50.ecommerce.exception.PaymentFailedException;
import com.agile50.ecommerce.model.Order;
import com.agile50.ecommerce.model.OrderItem;
import com.agile50.ecommerce.model.OrderStatus;
import com.agile50.ecommerce.payment.PaymentGateway;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;

    public OrderService(InventoryService inventoryService, PaymentGateway paymentGateway) {
        this.inventoryService = inventoryService;
        this.paymentGateway = paymentGateway;
    }

    public void processOrder(Order order) throws InsufficientInventoryException, PaymentFailedException {
        order.setStatus(OrderStatus.PROCESSING);

        for (OrderItem item : order.getItems()) {
            String productId = item.getProduct().getId();
            if (!inventoryService.hasSufficientStock(productId, item.getQuantity())) {
                order.setStatus(OrderStatus.FAILED);
                throw new InsufficientInventoryException("Insufficient stock for product: " + productId);
            }
        }

        List<OrderItem> reservedItems = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            inventoryService.deductStock(item.getProduct().getId(), item.getQuantity());
            reservedItems.add(item);
        }

        boolean paymentSuccess = paymentGateway.charge(order.getTotalAmount());
        if (!paymentSuccess) {
            for (OrderItem item : reservedItems) {
                inventoryService.restoreStock(item.getProduct().getId(), item.getQuantity());
            }
            order.setStatus(OrderStatus.FAILED);
            throw new PaymentFailedException("Payment failed for order: " + order.getOrderId());
        }

        order.setStatus(OrderStatus.COMPLETED);
    }
}
