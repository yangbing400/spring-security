package com.six.landing.config.exception;

/**
 * 用户验证异常
 */
public class UsernameIsExitedException extends BaseException {

    public UsernameIsExitedException(String msg) {
        super(msg);
    }

    public UsernameIsExitedException(String msg, Throwable t) {
        super(msg, t);
    }
}