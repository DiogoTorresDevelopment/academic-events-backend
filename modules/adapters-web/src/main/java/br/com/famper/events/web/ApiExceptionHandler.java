package br.com.famper.events.web;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handle(IllegalArgumentException ex) {
    var pd = ProblemDetail.forStatus(400);
    pd.setTitle("Invalid request");
    pd.setDetail(ex.getMessage());
    return ResponseEntity.badRequest().body(pd);
  }
}