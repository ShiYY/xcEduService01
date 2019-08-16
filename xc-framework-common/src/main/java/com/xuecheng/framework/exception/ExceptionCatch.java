package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常捕获类
 */
@ControllerAdvice // 控制器增强
public class ExceptionCatch {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);
    
    // 定义map, 配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    // 定义map的builder对象, 去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap
        .builder();
    
    static {
        // 定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
        builder.put(NullPointerException.class,  CommonCode.INVALID_PARAM);
    }
    
    /**
     * 捕获CustomException此类异常
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody // 将抛出的异常信息转换为json返回
    public ResponseResult customException(CustomException customException) {
        // 记录日志
        LOGGER.error("catch customException: {}", customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }
    
    /**
     * 捕获Exception此类异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception) {
        // 记录日志
        LOGGER.error("catch exception: {}", exception.getMessage());
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build(); // EXCEPTIONS构建成功
        }
        // 从EXCEPTIONS中查找异常类型所对应的错误代码
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode != null) {
            // 如果找到了将错误代码响应给用户
            return new ResponseResult(resultCode);
        }
        // 如果找不到响应标准错误代码9999给用户
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }
}
