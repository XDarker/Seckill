package com.xdarker.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 秒杀商品库存表对应的实体类
 * Created by XDarker
 * 2018/8/22 21:32
 */
@Data
public class SeckillGoods {

    private Long seckillId;
    private String name;
    private Integer number;
    private Date createTime;
    private Date startTime;
    private Date endTime;

    public SeckillGoods(){
        super();
    }

    public SeckillGoods(Long seckillId, String name, Integer number, Date createTime, Date startTime, Date endTime) {
        this.seckillId = seckillId;
        this.name = name;
        this.number = number;
        this.createTime = createTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        return "SeckillGoods{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", createTime=" + createTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}
