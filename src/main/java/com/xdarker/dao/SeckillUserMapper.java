package com.xdarker.dao;

import com.xdarker.pojo.SeckillUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * Created by XDarker
 * 2018/8/26 15:56
 */
@Mapper
public interface SeckillUserMapper {

    SeckillUser getById(Long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    public void update(SeckillUser toBeUpdate);

    int insert(SeckillUser user);
}
