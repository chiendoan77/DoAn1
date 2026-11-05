package com.doan1.identity.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class IdentityExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> invalidRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() == null ? "Giá trị không hợp lệ." : error.getDefaultMessage(),
                        (first, second) -> first,
                        LinkedHashMap::new));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Vui lòng kiểm tra lại thông tin đã nhập.");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> requestFailure(ResponseStatusException exception) {
        String detail = exception.getReason() == null ? "Yêu cầu không thành công." : exception.getReason();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), detail);
        return ResponseEntity.status(exception.getStatusCode()).body(problem);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> duplicateIdentity() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "Email, số điện thoại hoặc tên đăng nhập đã được sử dụng.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }
}
