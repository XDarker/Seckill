package com.xdarker.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 秒杀成功明细表
 * Created by XDarker
 * 2018/8/22 21:39
 */
@Data
public class SuccessKilled {

    private Long seckillId;
    private Long userPhone;
    private Short state;
    private Date createTime;

    //变通
    //多对一 符合属性
    private SeckillGoods seckill;

    private SuccessKilled(){
        super();
    }

    public SuccessKilled(Long seckillId, Long userPhone, Short state, Date createTime, SeckillGoods seckill) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
        this.seckill = seckill;
    }
    public SuccessKilled(Long seckillId, Long userPhone, Short state, Date createTime) {
        this.seckillId = seckillId;
        this.userPhone = userPhone;
        this.state = state;
        this.createTime = createTime;
    }

    public SeckillGoods getSeckill() {
        return seckill;
    }

    public void setSeckill(SeckillGoods seckill) {
        this.seckill = seckill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(Short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
