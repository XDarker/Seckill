package com.xdarker.dto;

import lombok.Data;

/**
 * 暴露秒杀地址DTO
 * Created by XDarker
 * 2018/8/24 14:32
 */
@Data
public class Exposer {
    /**
     * 是否开启秒杀
     */
    private boolean exposed;

    //一种加密措施
    private String md5;

    private Long seckillId;

    //系统当前时间(毫秒)
    private Long now;

    private Long start;

    private Long end;

    /**
     * 是否开启MD5 和 秒杀ID
     * @param exposed
     * @param md5
     * @param seckillId
     */
    public Exposer(boolean exposed, String md5, Long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    /**
     * 是否开启当前系统时间 开始 结束时间
     * @param exposed
     * @param now
     * @param start
     * @param end
     */
    public Exposer(boolean exposed, Long now, Long start, Long end) {
        this.exposed = exposed;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    /**
     * 是否开启 秒杀ID
     * @param exposed
     * @param seckillId
     */
    public Exposer(boolean exposed, Long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, Long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }


    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
