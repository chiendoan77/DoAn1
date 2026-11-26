package com.doan1.booking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BookingExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> invalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null ? "Giá trị không hợp lệ." : error.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Vui lòng kiểm tra lại thông tin đặt lịch.");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> requestFailure(ResponseStatusException exception) {
        String detail = exception.getReason() == null ? "Yêu cầu đặt lịch không thành công." : exception.getReason();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), detail);
        return ResponseEntity.status(exception.getStatusCode()).body(problem);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> concurrentUpdate(OptimisticLockingFailureException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Lịch hẹn đã được cập nhật bởi yêu cầu khác. Vui lòng tải lại.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> duplicateBooking(DataIntegrityViolationException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Yêu cầu đặt lịch trùng hoặc dữ liệu không hợp lệ.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }
}
