package com.six.landing.config.exception;

import com.alibaba.fastjson.JSON;
import com.six.landing.config.security.data.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
*token验证异常
 */
public class TokenException extends BaseException {

    private static final long serialVersionUID = 1L;
    ResponseBody responseBody=new ResponseBody();
    public TokenException(String message,HttpServletResponse response) {

        super(message);
    }
}
