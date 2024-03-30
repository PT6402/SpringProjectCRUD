package demo.project_crud.controller;

import java.util.HashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    //catch exception from SQL 
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleSQLServerException(DataIntegrityViolationException ex) {
        var errors = new HashMap<String, String>();
        errors.put("SQL Server Error", ex.getMessage());
        return ResponseEntity.badRequest().body(errors);
    }
}
