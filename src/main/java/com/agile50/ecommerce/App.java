package com.agile50.ecommerce;

import com.agile50.ecommerce.exception.InsufficientInventoryException;
import com.agile50.ecommerce.exception.PaymentFailedException;
import com.agile50.ecommerce.model.Order;
import com.agile50.ecommerce.model.OrderItem;
import com.agile50.ecommerce.model.Product;
import com.agile50.ecommerce.payment.PaymentGateway;
import com.agile50.ecommerce.service.InventoryService;
import com.agile50.ecommerce.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

public class App {
    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService();
        Product laptop = new Product("P-100", "Laptop", new BigDecimal("1200.00"));
        inventoryService.addStock(laptop, 5);

        PaymentGateway gateway = amount -> true;
        OrderService orderService = new OrderService(inventoryService, gateway);

        Order order = new Order("ORD-1", List.of(new OrderItem(laptop, 1)));

        try {
            orderService.processOrder(order);
            System.out.println("Order status: " + order.getStatus());
        } catch (InsufficientInventoryException | PaymentFailedException e) {
            System.err.println("Order failed: " + e.getMessage());
        }
    }
}
