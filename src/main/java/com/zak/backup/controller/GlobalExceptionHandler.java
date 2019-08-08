package com.zak.backup.controller;

import com.zak.backup.exception.BackupException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author zak
 * @version 1.0
 * @date 2019/8/5 9:19
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BackupException.class)
    public Map exceptionHandler(BackupException e) {
        log.error("内部错误: {}", e.getMessage());
        Map<String, Object> map = new HashMap<>(3);
        map.put("status", 400);
        map.put("message", e.getMessage());
        return map;
    }


    @ExceptionHandler(Exception.class)
    public Map exceptionHandler(Exception e) {
        log.error("内部错误", e);
        Map<String, Object> map = new HashMap<>(2);
        map.put("status", 500);
        map.put("message", e.getMessage());
        return map;
    }
}
