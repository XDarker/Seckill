package com.xdarker.service;

import com.xdarker.common.ServerResponse;
import com.xdarker.pojo.SeckillUser;
import com.xdarker.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by XDarker
 * 2018/8/26 15:44
 */
public interface ISeckillUserService {
    ServerResponse<SeckillUser> getById(Long id);

    ServerResponse<SeckillUser> login(LoginVo loginVo, HttpServletResponse response);

    ServerResponse<SeckillUser> getByToken(String token, HttpServletResponse response);

    ServerResponse<String> register(SeckillUser user);

}
