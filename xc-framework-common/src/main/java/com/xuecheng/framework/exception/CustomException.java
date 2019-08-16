package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * 自定义异常类型 继承RuntimeException对代码没有侵入性
 */
public class CustomException extends RuntimeException {
    
    ResultCode resultCode;
    
    public CustomException(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
    
    public ResultCode getResultCode() {
        return resultCode;
    }
}
