package com.xdarker.rabbitmq;

import com.xdarker.pojo.SeckillUser;

/**
 * Created by XDarker
 * 2018/8/26 15:23
 */
public class SeckillMessage {
    private SeckillUser user;
    private Long seckillId;
    private String md5;
    private Long userPhone;

    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public SeckillUser getUser() {
        return user;
    }
    public void setUser(SeckillUser user) {
        this.user = user;
    }
    public long getSecSkillGoodsId() {
        return seckillId;
    }
    public void setSecSkillGoodsId(long seckillId) {
        this.seckillId = seckillId;
    }
}

