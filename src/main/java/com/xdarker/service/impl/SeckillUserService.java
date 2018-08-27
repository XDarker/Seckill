package com.xdarker.service.impl;

import com.xdarker.common.ResponseCode;
import com.xdarker.common.ServerResponse;
import com.xdarker.dao.SeckillUserMapper;
import com.xdarker.pojo.SeckillUser;
import com.xdarker.service.IRedisClusterService;
import com.xdarker.service.ISeckillUserService;
import com.xdarker.util.CookieUtil;
import com.xdarker.util.MD5Util;
import com.xdarker.util.UUIDUtil;
import com.xdarker.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by XDarker
 * 2018/8/26 15:53
 */
@Service("iSeckillUserService")
public class SeckillUserService implements ISeckillUserService {

    private  final SeckillUserMapper seckillUserMapper;
    private  final IRedisClusterService iRedisClusterService;
    @Autowired
    public SeckillUserService(SeckillUserMapper seckillUserMapper, IRedisClusterService iRedisClusterService) {
        this.seckillUserMapper = seckillUserMapper;
        this.iRedisClusterService = iRedisClusterService;
    }


    @Override
    public ServerResponse<SeckillUser> getById(Long id) {
        //取缓存
        String  userString =  iRedisClusterService.get("id:"+id);
        //String 转换成对象
        SeckillUser user = iRedisClusterService.stringToBean(userString,SeckillUser.class);

        if (user != null){
            return ServerResponse.createBySuccess(user);
        }

        //取数据库
        user = seckillUserMapper.getById(id);
        if(user != null){
            //存进Redis缓存
            //先把对象 转成 String
            userString = iRedisClusterService.beanToString(user);
            iRedisClusterService.setex("id:"+id,userString);
        }
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<SeckillUser> login(LoginVo loginVo, HttpServletResponse response) {
        if (loginVo == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号对应的用户是否存在
        SeckillUser user = getById(Long.parseLong(mobile)).getData();
        if(user == null ){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //验证密码
        String dbPass = user.getPassword();
        String salt = user.getSalt();

        String calcPass = MD5Util.MD5EncodeUtf8(formPass.toUpperCase());
        if(!calcPass.equals(dbPass)){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //登陆成功
        //生成cookie
        //随机token
        String token = UUID.randomUUID().toString().replace("-", "");

        String userString = iRedisClusterService.beanToString(user);
        iRedisClusterService.setex("token:"+token,userString);

        CookieUtil.writeLoginToken(response,token);
//            Cookie cookie = new Cookie(Const.COOKI_NAME_TOKEN,token);
//            cookie.setMaxAge(MiaoshaUserKey.TOKEN_EXPIRE);
//            cookie.setPath("/");
//            response.addCookie(cookie);

        return ServerResponse.createBySuccess("登陆成功",user);
    }


    @Override
    public ServerResponse<SeckillUser> getByToken(String token, HttpServletResponse response) {
        return null;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(SeckillUser user) {
        if(MD5Util.MD5EncodeUtf8("").equals(user.getPassword().toUpperCase())){
            return ServerResponse.createByErrorMessage("注册失败,密码不能为空");
        }
        if(user.getId() == null){
            return ServerResponse.createByErrorMessage("注册失败,手机号不能为空");
        }
        //MD5第二次加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword().toUpperCase()));

        int resultCount = seckillUserMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }


}
