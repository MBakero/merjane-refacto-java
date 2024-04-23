package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.UnfulfilledOrder;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ErrorCodes;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.BaseException;
import com.nimbleways.springboilerplate.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {


    @Autowired
    private ProductService ps;

    @Autowired
    private ProductRepository pr;

    @Autowired
    private OrderRepository or;

    public List<UnfulfilledOrder> handleProcessSaleCommand(Long orderId) {
        Order order = or.findById(orderId).get();
        Set<Product> products = order.getItems();

        List<UnfulfilledOrder> unfulfilled = new ArrayList<>();

        //FIXME: add comment explaining the existence of this array, or safely remove it
        List<Long> ids = new ArrayList<>();
        ids.add(orderId);

        for (Product p : products) {
            try {
                switch (p.getType()) {
                    case NORMAL -> handleSellNormalProduct(p);
                    case SEASONAL -> handleSellSeasonalProduct(p);
                    case EXPIRABLE -> handleSellExpirableProduct(p);
                    case FLASHSALE -> handleSellFlashSaleProduct(p);
                }
            } catch (Exception e) {
                if (e instanceof BaseException) {
                    BaseException be = (BaseException) e;
                    unfulfilled.add(new UnfulfilledOrder(p.getId(), be.getCode(), be.getMessage(), be.getHttpStatus()));
                } else throw e;
            }

        }
        //TODO: we can use this data to be stored for later fetch or return it direclty to the client for more consistency
        return unfulfilled;
    }

    public void handleSellNormalProduct(Product p) {
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            int leadTime = p.getLeadTime();
            if (leadTime > 0) {
                ps.notifyDelay(leadTime, p);
            }
        }
    }

    public void handleSellSeasonalProduct(Product p) {
        if ((LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate())
                && p.getAvailable() > 0)) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            ps.handleSeasonalProduct(p);
        }
    }

    public void handleSellExpirableProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            ps.handleExpiredProduct(p);
        }
    }

    public void handleSellFlashSaleProduct(Product p) {
        if (!DateUtils.isDateInBetween(p.getPeriodStartDate(), p.getPeriodEndDate())) {
            throw new BaseException(ErrorCodes.FLASHSALE_NOT_AVAILABLE_ANYMORE.name(), ErrorCodes.FLASHSALE_NOT_AVAILABLE_ANYMORE.message(), HttpStatus.BAD_REQUEST);
        }
        if (p.getMaxToSell() <= 0) {
            throw new BaseException(ErrorCodes.FLASHSALE_SOLDOUT.name(), ErrorCodes.FLASHSALE_SOLDOUT.message(), HttpStatus.BAD_REQUEST);
        }
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            p.setMaxToSell(p.getMaxToSell() - 1);
            pr.save(p);
        } else {
            ps.handleFlashSaleProduct(p);
        }
    }

}
