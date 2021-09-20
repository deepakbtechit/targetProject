package com.target.product.exceptionHandler;

import com.target.product.domain.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TargetProductException.class)
    ResponseEntity<ErrorInfo> handleTargetException(TargetProductException ex) {
        log.error("ControllerAdvice, method=handleWebClientResponseException");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorInfo("TARGET_SERVICE_EXCEPTION", "Error while fetching data from api "));
    }

    @ExceptionHandler(WebClientResponseException.class)
    ResponseEntity<ErrorInfo> handleWebClientResponseException(WebClientResponseException ex) {
        log.error("ControllerAdvice, method=handleWebClientResponseException");
        return ResponseEntity.status(ex.getRawStatusCode()).body(new ErrorInfo("INTERNAL_SERVER_EXCEPTION", "Target service is temporarily down. Please try after sometime "));
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorInfo> handleRuntimeException(RuntimeException ex) {
        log.error("ControllerAdvice, method=handleWebClientResponseException");
        ErrorInfo errorInfo = new ErrorInfo("INTERNAL_SERVER_EXCEPTION", "Target service is temporarily down. Please try after sometime ");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorInfo> handleException(Exception ex) {
        log.error("ControllerAdvice, method=handleWebClientResponseException");
        ErrorInfo errorInfo = new ErrorInfo("INTERNAL_SERVER_EXCEPTION", "Something went wrong.Please try after sometime ");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
    }
}
