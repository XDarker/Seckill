package com.xdarker.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by XDarker
 * 2018/8/26 15:26
 */
@Data
public class SeckillUser {

    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private String head;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
