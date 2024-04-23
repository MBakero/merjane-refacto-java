package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.enums.ErrorCodes;
import com.nimbleways.springboilerplate.utils.BaseException;
import com.nimbleways.springboilerplate.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    ProductRepository pr;

    @Autowired
    NotificationService ns;

    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        pr.save(p);
        ns.sendDelayNotification(leadTime, p.getName());
    }

    public void handleSeasonalProduct(Product p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            ns.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            pr.save(p);
        } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            ns.sendOutOfStockNotification(p.getName());
            pr.save(p);
        } else {
            notifyDelay(p.getLeadTime(), p);
        }
    }

    public void handleExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            pr.save(p);
        } else {
            ns.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
            pr.save(p);
        }
    }

    public void handleFlashSaleProduct(Product p) {
        if (!DateUtils.isLeadTimeExcedeDeadline(p.getLeadTime(), p.getPeriodEndDate())) {
            if (p.getLeadTime() > 0) notifyDelay(p.getLeadTime(), p);
            ns.sendOutOfStockNotification(p.getName());
            throw new BaseException(ErrorCodes.FLASHSALE_NEEDS_SUPPLY.name(), ErrorCodes.FLASHSALE_NEEDS_SUPPLY.message(), HttpStatus.ACCEPTED);
        } else throw new BaseException(ErrorCodes.FLASHSALE_CANT_BE_AVAILABLE.name(), ErrorCodes.FLASHSALE_CANT_BE_AVAILABLE.message(), HttpStatus.GONE);

    }
}