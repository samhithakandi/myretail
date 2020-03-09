package com.samhitha.myretail.exception;

import com.samhitha.myretail.controller.ProductController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {ProductController.class})
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(value
            = { HttpClientErrorException.class })
    protected ResponseEntity<Object> handleNotFound(
            HttpClientErrorException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getStatusText(),
                new HttpHeaders(), ex.getStatusCode(), request);
    }

    @ExceptionHandler(value
            = { NumberFormatException.class })
    protected ResponseEntity<Object> handleBadRequest(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Input may be invalid. Please verify.";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = { HttpServerErrorException.class })
    protected ResponseEntity<Object> handleServerError(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "Server has an error. Please try later.";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
