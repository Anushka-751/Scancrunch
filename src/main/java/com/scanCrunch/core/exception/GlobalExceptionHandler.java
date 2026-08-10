package com.scanCrunch.core.exception;

import java.util.HashMap;
import java.util.Map;

import com.scanCrunch.core.util.ResponseBuilder;
import com.scanCrunch.domain.payment.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- Auth / Security module additions ----
    // These use the standardized {success, message, timestamp} envelope
    // (ResponseBuilder) per the security module's API error contract,
    // rather than the existing ApiResponse<T> used above - that type
    // carries a "data" payload other modules rely on and is left as-is.

    // Invalid, malformed, or tampered JWT
    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJwt(
            InvalidJwtException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // Expired JWT
    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleJwtExpired(
            JwtExpiredException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // OTP rate limit exceeded
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimitExceeded(
            RateLimitExceededException ex) {

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // Explicit unauthorized (e.g. auth utilities / current-user checks)
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(
            UnauthorizedException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // Explicit forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(
            ForbiddenException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // Spring Security access-denied (e.g. role/authority checks)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseBuilder.error("You do not have permission to perform this action."));
    }

    // Any authentication failure surfaced inside a controller/service
    // rather than by the JWT filter (e.g. bad credentials on login)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            AuthenticationException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseBuilder.error("Authentication failed."));
    }

    // Email already registered (used by /api/auth/check-email consumers)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // Generic request validation failure raised manually by services
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            ValidationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseBuilder.error(ex.getMessage()));
    }

    // ---- End auth / security module additions ----

    // User Already Exists
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserExists(
            UserAlreadyExistsException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Food Unavailable
    @ExceptionHandler(FoodUnavailableException.class)
    public ResponseEntity<ApiResponse<Object>> handleFoodUnavailable(
            FoodUnavailableException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Duplicate Wishlist Item
    @ExceptionHandler(DuplicateWishlistException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateWishlist(
            DuplicateWishlistException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Payment Exception
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<Object>> handlePaymentException(
            PaymentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation Failed", errors));
    }

    // Duplicate Email
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateEmail(
            DuplicateEmailException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Invalid OTP
    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidOtp(
            InvalidOtpException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Expired OTP
    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpExpired(
            OtpExpiredException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // OTP Not Found
    @ExceptionHandler(OtpNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleOtpNotFound(
            OtpNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Registration Exception
    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiResponse<Object>> handleRegistrationException(
            RegistrationException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Account Not Found
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountNotFound(
            AccountNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Invalid Reset OTP
    @ExceptionHandler(InvalidResetOtpException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidResetOtp(
            InvalidResetOtpException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Reset OTP Expired
    @ExceptionHandler(ResetOtpExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleResetOtpExpired(
            ResetOtpExpiredException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Reset OTP Not Found
    @ExceptionHandler(ResetOtpNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResetOtpNotFound(
            ResetOtpNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Password Mismatch
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handlePasswordMismatch(
            PasswordMismatchException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Password Reset Exception
    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<ApiResponse<Object>> handlePasswordResetException(
            PasswordResetException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Receipt Not Found
    @ExceptionHandler(ReceiptNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleReceiptNotFound(
            ReceiptNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Duplicate Receipt
    @ExceptionHandler(DuplicateReceiptException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateReceipt(
            DuplicateReceiptException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Receipt Generation Failure
    @ExceptionHandler(ReceiptGenerationException.class)
    public ResponseEntity<ApiResponse<Object>> handleReceiptGeneration(
            ReceiptGenerationException ex) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Email Delivery Failure
    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailDelivery(
            EmailDeliveryException ex) {

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Runtime Exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    // Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex) {

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Something went wrong", null));
    }
}