package com.xdarker.dto;

import com.xdarker.common.Const;
import com.xdarker.pojo.SecKillOrder;

/**
 * 执行秒杀执行后的结果
 * Created by XDarker
 * 2018/8/24 14:36
 */
public class SeckillExecution {
    private Long seckillId;

    //秒杀执行状态
    private Integer state;

    //状态表示
    private String stateInfo;

    //秒杀成功对象
    private SecKillOrder successKilled;

    public SeckillExecution(Long seckillId, Const.SeckillStateEnum stateEnum, SecKillOrder successKilled) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SeckillExecution(Long seckillId, Const.SeckillStateEnum stateEnum) {
        this.seckillId = seckillId;
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SecKillOrder getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SecKillOrder successKilled) {
        this.successKilled = successKilled;
    }
}

