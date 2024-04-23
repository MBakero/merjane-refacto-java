package com.nimbleways.springboilerplate.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 3796260213979652019L;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    public BaseException(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String toString() {
        return String.format("Exception[code=%s, message=%s, httpStatus=%s]", this.getCode(), this.getMessage(),
                this.getHttpStatus());
    }


}
