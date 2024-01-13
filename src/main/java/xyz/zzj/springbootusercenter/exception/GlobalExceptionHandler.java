package xyz.zzj.springbootusercenter.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.zzj.springbootusercenter.connon.BaseResponse;
import xyz.zzj.springbootusercenter.connon.ErrorCode;
import xyz.zzj.springbootusercenter.connon.ResultUtils;

/**
 * @BelongsProject: springboot-user-center
 * @BelongsPackage: xyz.zzj.springbootusercenter.exception
 * @Author: zengzhaojun
 * @CreateTime: 2024-01-13  14:04
 * @Description: TODO
 * @Version: 1.0
 */

/**
 *  全局异常处理器
 */

@RestControllerAdvice  //Spring是切面功能
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)   //处理被抛出的指定异常
    public BaseResponse BusinessExceptionHandler(BusinessException e){
        log.info(e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)   //处理被抛出的指定异常
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.info("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }


}

