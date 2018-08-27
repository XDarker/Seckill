package com.xdarker.controller;

import com.xdarker.common.ResponseCode;
import com.xdarker.common.ServerResponse;
import com.xdarker.pojo.SeckillUser;
import com.xdarker.service.ISeckillUserService;
import com.xdarker.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by XDarker
 * 2018/8/26 22:47
 */
@Controller
@RequestMapping("/user/")
@Slf4j
public class UserController {

    private final ISeckillUserService iSeckillUserService;
    @Autowired
    public UserController(ISeckillUserService iSeckillUserService) {
        this.iSeckillUserService = iSeckillUserService;
    }

    @RequestMapping("to_register")
    public String toRegister() {
        return "register";
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(SeckillUser user){
        return  iSeckillUserService.register(user);
//        if(resp.isSuccess()){
//            return "login";
//        }
//        return "register";
    }

    @RequestMapping("to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("do_login")
    @ResponseBody
    public ServerResponse<SeckillUser> doLogin(@Valid LoginVo loginVo,
                                               HttpServletResponse resp, HttpSession session) {

        log.info(loginVo.toString());
//       //参数校验
//        String passInput = loginVo.getPassword();
//        String moblie = loginVo.getMobile();
//        if (StringUtils.isEmpty(passInput)){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if(StringUtils.isEmpty(moblie)){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
//        }
//        if (!ValidatorUtil.isMobile(moblie)){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
//        }
        //登录
        ServerResponse<SeckillUser> response = iSeckillUserService.login(loginVo, resp);
        if (response.isSuccess()) {
            //登陆成功
            //将对象序列化后 存储到redis集群中
            //String userJson = JsonUtil.obj2String(response.getData());

            //内置带有设置过期时间 1小时
            //iRedisClusterService.setex("user:"+session.getId(),userJson);

            return response;
        }
        return ServerResponse.createByErrorMessage(ResponseCode.PASSWORD_ERROR.getDesc());
    }

    @RequestMapping(value = "to_checkId" ,method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse to_checkId(Long id){

        ServerResponse<SeckillUser> resp = iSeckillUserService.getById(id);
        if(resp.getData() != null){//说明手机号存在
            return ServerResponse.createByError();
        }
        return resp;//手机号不存在
    }
}

