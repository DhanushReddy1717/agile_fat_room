package com.agile50.ecommerce.service;

import com.agile50.ecommerce.exception.InsufficientInventoryException;
import com.agile50.ecommerce.exception.PaymentFailedException;
import com.agile50.ecommerce.model.Order;
import com.agile50.ecommerce.model.OrderItem;
import com.agile50.ecommerce.model.OrderStatus;
import com.agile50.ecommerce.model.Product;
import com.agile50.ecommerce.payment.PaymentGateway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {

    @Test
    void processOrder_success_updatesInventoryAndStatus() throws Exception {
        InventoryService inventoryService = new InventoryService();
        Product product = new Product("P-1", "Phone", new BigDecimal("500.00"));
        inventoryService.addStock(product, 10);

        PaymentGateway paymentGateway = amount -> true;
        OrderService orderService = new OrderService(inventoryService, paymentGateway);

        Order order = new Order("O-1", List.of(new OrderItem(product, 2)));

        orderService.processOrder(order);

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(8, inventoryService.getAvailableStock(product.getId()));
    }

    @Test
    void processOrder_failsWhenInventoryIsInsufficient() {
        InventoryService inventoryService = new InventoryService();
        Product product = new Product("P-2", "Keyboard", new BigDecimal("50.00"));
        inventoryService.addStock(product, 1);

        PaymentGateway paymentGateway = amount -> true;
        OrderService orderService = new OrderService(inventoryService, paymentGateway);

        Order order = new Order("O-2", List.of(new OrderItem(product, 2)));

        assertThrows(InsufficientInventoryException.class, () -> orderService.processOrder(order));
        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertEquals(1, inventoryService.getAvailableStock(product.getId()));
    }

    @Test
    void processOrder_failsWhenPaymentFails_andRestoresInventory() {
        InventoryService inventoryService = new InventoryService();
        Product product = new Product("P-3", "Mouse", new BigDecimal("25.00"));
        inventoryService.addStock(product, 4);

        PaymentGateway paymentGateway = amount -> false;
        OrderService orderService = new OrderService(inventoryService, paymentGateway);

        Order order = new Order("O-3", List.of(new OrderItem(product, 3)));

        assertThrows(PaymentFailedException.class, () -> orderService.processOrder(order));
        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertEquals(4, inventoryService.getAvailableStock(product.getId()));
    }
}
