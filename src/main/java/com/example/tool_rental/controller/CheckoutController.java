package com.example.tool_rental.controller;

import com.example.tool_rental.model.CheckoutRequest;
import com.example.tool_rental.model.RentalAgreement;
import com.example.tool_rental.service.CheckoutService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for handling checkout operations.
 * Provides endpoints for processing checkout requests and managing related exceptions.
 */
@RestController
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<RentalAgreement> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        RentalAgreement rentalAgreement = checkoutService.checkout(checkoutRequest);
        return ResponseEntity.ok(rentalAgreement);
    }

    /*
      In a real application with more than one controller, these exception handlers
      could be extracted into a global @ControllerAdvice to avoid cluttering the controller.
      This would help keep the controller focused on business logic while delegating
      exception handling to a dedicated class that could be reused by other controllers.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
