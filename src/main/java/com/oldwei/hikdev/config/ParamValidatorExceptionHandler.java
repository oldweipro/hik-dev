package com.oldwei.hikdev.config;

import com.oldwei.hikdev.entity.HikDevResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ParamValidatorExceptionHandler {
    /**
     * 处理所有参数校验时抛出的异常
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HikDevResponse globalValidationException(ValidationException ex) {
        // 获取所有异常
        List<String> errors = new LinkedList<>();
        if(ex instanceof ConstraintViolationException){
            ConstraintViolationException exs = (ConstraintViolationException) ex;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                errors.add(item.getMessage());
            }
        }
        return new HikDevResponse().code(400).msg(String.join(",", errors));
    }

    /**
     * 处理所有校验失败的异常（MethodArgumentNotValidException异常）
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HikDevResponse globalMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // 获取所有异常
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return new HikDevResponse().code(400).msg(String.join(",", errors));
    }

    /**
     * Controller参数绑定错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public HikDevResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return new HikDevResponse().code(400).msg(ex.getMessage());
    }
}
