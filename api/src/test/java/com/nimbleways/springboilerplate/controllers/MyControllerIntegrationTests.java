package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.dto.product.UnfulfilledOrder;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.implementations.OrderService;
import com.nimbleways.springboilerplate.utils.Annotations.SetupDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;

// import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@SetupDatabase
@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    private List<Product> products;

    private Order order;


    @BeforeEach
    void init() {
        List<Product> allProducts = createProducts();
        Set<Product> orderItems = new HashSet<Product>(allProducts);
        Order order = createOrder(orderItems);
        productRepository.saveAll(allProducts);
        order = orderRepository.save(order);

        this.products = allProducts;
        this.order = order;
    }

    @AfterEach
    void flush() {
        this.products = null;
        this.order = null;
    }


    @Test
    public void processOrderShouldReturn() throws Exception {
        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());
        Order resultOrder = orderRepository.findById(order.getId()).get();
        assertEquals(resultOrder.getId(), order.getId());
    }

    @Test
    public void handleProcessSaleCommand() {

        // WHEN
        List<UnfulfilledOrder> unfulfilled = orderService.handleProcessSaleCommand(order.getId());

        // THEN
        assertEquals(4, unfulfilled.size());
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(1L, 15, 30, ProductType.NORMAL, "USB Cable", null, null, null));
        products.add(new Product(2L, 10, 0, ProductType.NORMAL, "USB Dongle", null, null, null));

        products.add(new Product(3L, 15, 30, ProductType.EXPIRABLE, "Butter", null, LocalDate.now().plusDays(26),
                null));
        products.add(new Product(4L, 90, 6, ProductType.EXPIRABLE, "Milk", null, LocalDate.now().minusDays(2), null));

        products.add(new Product(5L, 15, 30, ProductType.SEASONAL, "Watermelon", LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(58), null));
        products.add(new Product(6L, 15, 30, ProductType.SEASONAL, "Grapes", LocalDate.now().plusDays(180),
                LocalDate.now().plusDays(240), null));
        products.add(new Product(7L, 0, 200, ProductType.FLASHSALE, "flashsale not available anymore", LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5), 100));

        products.add(new Product(8L, 0, 200, ProductType.FLASHSALE, "flashsale soldout product", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(5), 0));

        products.add(new Product(9L, 10, 35, ProductType.FLASHSALE, "normal flashsale product", LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(10), 20));

        products.add(new Product(10L, 15, 0, ProductType.FLASHSALE, "new supply is coming product", LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(20), 10));

        products.add(new Product(11L, 20, 0, ProductType.FLASHSALE, "offer not available anymore product", LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(10), 20));

        return products;
    }

}
