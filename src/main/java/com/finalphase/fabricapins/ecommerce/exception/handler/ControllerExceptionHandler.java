package com.finalphase.fabricapins.ecommerce.exception.handler;

import com.finalphase.fabricapins.ecommerce.exception.BusinessException;
import com.finalphase.fabricapins.ecommerce.exception.DatabaseException;
import com.finalphase.fabricapins.ecommerce.exception.InsufficientStockException;
import com.finalphase.fabricapins.ecommerce.exception.ResourceNotFoundException;
import com.finalphase.fabricapins.ecommerce.exception.model.CustomError;
import com.finalphase.fabricapins.ecommerce.exception.model.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resorceNotFound(ResourceNotFoundException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError err = new CustomError(Instant.now(), status.value(), status.getReasonPhrase(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> databaseException(DatabaseException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(Instant.now(), status.value(), status.getReasonPhrase(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> databaseException(MethodArgumentNotValidException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
        ValidationError err = new ValidationError(Instant.now(), status.value(), status.getReasonPhrase(), "Erro na validação dos campos da requisição" , request.getRequestURI());
        for(FieldError f : e.getBindingResult().getFieldErrors()){
            err.addError(f.getField(), f.getDefaultMessage());
        };
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CustomError> businessException(BusinessException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.PRECONDITION_FAILED;
        CustomError err = new CustomError(Instant.now(), status.value(), status.getReasonPhrase(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomError> badCredentials(BadCredentialsException e,HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Usuário ou senha inválidos",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomError> accessDenied(AccessDeniedException e,HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        CustomError err = new CustomError(Instant.now(), status.value(), status.getReasonPhrase(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomError> httpMessageNotReadable(HttpMessageNotReadableException e,HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Erro ao ler JSON da requisição";
        CustomError err = new CustomError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<CustomError> businessException(InsufficientStockException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(Instant.now(), status.value(), status.getReasonPhrase(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomError> genericException(Exception e,HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        // Log completo interno
        //implementar log
//        private static final Logger log =
//                LoggerFactory.getLogger(ControllerExceptionHandler.class);
//        log.error("Erro interno não tratado", e);
        e.printStackTrace();
        CustomError err = new CustomError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Erro interno do servidor",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomError> missingServletRequestParameterException(MissingServletRequestParameterException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError err = new CustomError(Instant.now(), status.value(), "Parâmetro obrigatório ausente", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomError> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e , HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        String message = String.format(
                "Valor inválido para o parâmetro '%s'",
                e.getName()
        );
        CustomError err = new CustomError(Instant.now(), status.value(), "Parâmetro inválido", message, request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }


}
