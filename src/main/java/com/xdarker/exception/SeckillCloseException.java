package com.xdarker.exception;

/**
 * 秒杀关闭异常
 * Created by XDarker
 * 2018/8/24 14:42
 */
public class SeckillCloseException extends  SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
