package com.xdarker.common;

/**
 * 使用枚举
 * Created by XDarker
 * 2018/8/24 14:37
 */
public class Const {
    public static final String COOKI_NAME_TOKEN = "token";
    public static final int EXPIRETIME = 60 * 60;//秒

    public static final int EXPIRETIME1 = 60;

    public enum SeckillStateEnum{
        SUCCESS(1,"秒杀成功"),
        QUEUE(2,"秒杀排队中"),
        END(0,"秒杀结束"),

        REPEAT_KILL(-1,"重复秒杀"),
        INNER_ERROR(-2,"系统异常"),
        DATA_REWRITE(-3,"数据篡改");
        private int state;
        private String stateInfo;

        SeckillStateEnum(int state, String stateInfo) {
            this.state = state;
            this.stateInfo = stateInfo;
        }

        public int getState() {
            return state;
        }

        public String getStateInfo() {
            return stateInfo;
        }

        public static SeckillStateEnum stateOf(int index){
            for (SeckillStateEnum state : values()){
                if(state.getState() == index){
                    return state;
                }
            }
            return null;
        }
    }
}
