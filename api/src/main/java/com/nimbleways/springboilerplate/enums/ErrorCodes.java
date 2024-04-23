package com.nimbleways.springboilerplate.enums;

public enum ErrorCodes {
    FLASHSALE_NOT_AVAILABLE_ANYMORE("This flash sell offer is no longer available"),
    FLASHSALE_SOLDOUT("This product is sold out in this flash sell offer"),
    FLASHSALE_NEEDS_SUPPLY("the product is not available, however a new stock have been ordred"),
    FLASHSALE_CANT_BE_AVAILABLE("this product is not available in stock anymore");

    private String message;

    ErrorCodes(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
