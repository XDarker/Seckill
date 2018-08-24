package com.xdarker.exception;

/**
 * 秒杀相关业务异常
 * Created by XDarker
 * 2018/8/24 14:40
 */
public class SeckillException extends RuntimeException{

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
