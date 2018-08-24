package com.xdarker.exception;

/**
 * 重复秒杀异常
 * Created by XDarker
 * 2018/8/24 14:41
 */
public class RepeatKillException extends  SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
