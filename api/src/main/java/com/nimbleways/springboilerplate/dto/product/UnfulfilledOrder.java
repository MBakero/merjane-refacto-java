package com.nimbleways.springboilerplate.dto.product;

import org.springframework.http.HttpStatus;

public record UnfulfilledOrder(Long id, String code, String message, HttpStatus status) {
}
