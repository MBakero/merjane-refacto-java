package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ErrorCodes;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import com.nimbleways.springboilerplate.utils.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks 
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void test() {
        // GIVEN
        Product product =new Product(null, 15, 0, ProductType.NORMAL, "RJ45 Cable", null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product);

        // THEN
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }


    @Test
    public void testHandleFlashSaleProduct_needSupply() {

        // GIVEN
        Product product = new Product(10L, 15, 0, ProductType.FLASHSALE, "new supply is coming product", LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(20), 10);

        // WHEN
        BaseException be = assertThrows(BaseException.class, () -> productService.handleFlashSaleProduct(product));

        // THEN
        assertEquals(ErrorCodes.FLASHSALE_NEEDS_SUPPLY.name(), be.getCode());
        assertEquals(ErrorCodes.FLASHSALE_NEEDS_SUPPLY.message(), be.getMessage());
        assertEquals(HttpStatus.ACCEPTED, be.getHttpStatus());

        Mockito.verify(notificationService, Mockito.times(1)).sendOutOfStockNotification(product.getName());

    }

    @Test
    public void testHandleFlashSaleProduct_cantBeAvailable() {

        //GIVEN
        Product product = new Product(11L, 20, 0, ProductType.FLASHSALE, "offer not available anymore product", LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(10), 20);

        // WHEN
        BaseException be = assertThrows(BaseException.class, () -> productService.handleFlashSaleProduct(product));

        // THEN
        assertEquals(ErrorCodes.FLASHSALE_CANT_BE_AVAILABLE.name(), be.getCode());
        assertEquals(ErrorCodes.FLASHSALE_CANT_BE_AVAILABLE.message(), be.getMessage());
        assertEquals(HttpStatus.GONE, be.getHttpStatus());
    }

    @Test
    public void testHandleSellFlashSaleProduct_notAvailableAnymore() {

        // GIVEN
        Product product = new Product(7L, 0, 200, ProductType.FLASHSALE, "flashsale not available anymore", LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5), 100);

        // WHEN
        BaseException be = assertThrows(BaseException.class, () -> orderService.handleSellFlashSaleProduct(product));

        // THEN
        assertEquals(ErrorCodes.FLASHSALE_NOT_AVAILABLE_ANYMORE.name(), be.getCode());
        assertEquals(ErrorCodes.FLASHSALE_NOT_AVAILABLE_ANYMORE.message(), be.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());

    }

    @Test
    public void testHandleSellFlashSaleProduct_soldOut() {

        // GIVEN
        Product product = new Product(8L, 0, 200, ProductType.FLASHSALE, "flashsale soldout product", LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(5), 0);

        // WHEN
        BaseException be = assertThrows(BaseException.class, () -> orderService.handleSellFlashSaleProduct(product));

        // THEN
        assertEquals(ErrorCodes.FLASHSALE_SOLDOUT.name(), be.getCode());
        assertEquals(ErrorCodes.FLASHSALE_SOLDOUT.message(), be.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, be.getHttpStatus());

    }

}