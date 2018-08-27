package com.xdarker.service.impl;

import com.alibaba.fastjson.JSON;
import com.xdarker.common.Const;
import com.xdarker.service.IRedisClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * Redis集群服务接口实现
 * Created by XDarker
 * 2018/8/24 21:42
 */
@Service("iRedisClusterService")
public class RedisClusterServiceImpl implements IRedisClusterService {

    @Autowired
    private JedisCluster jedisCluster;

    /**
     * redis设置缓存 带有设置过期时间功能
     * @param key
     * @param value
     * @return
     */
    public String setex(String key, String value){
        //过期时间单位:秒
        return jedisCluster.setex(key, Const.EXPIRETIME, value);
    }

    /**
     * redis设置缓存 永不过期
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value){
        return jedisCluster.set(key,value);
    }

    /**
     * 根据key 获取value
     * @param key
     * @return
     */
    public String get(String key){
        return jedisCluster.get(key);
    }

    /**
     * 再次设置redis key的过期时间
     * @param key
     * @return
     */
    public Long expire(String key){
        return jedisCluster.expire(key,Const.EXPIRETIME1);
    }

    /**
     * 删除
     * */
    public boolean delete(String key) {

        long ret =  jedisCluster.del(key);
        return ret > 0;

    }

    /**
     * 增加值
     * */
    public <T> Long incr(String key) {


        return  jedisCluster.incr(key);

    }

    /**
     * 减少值
     * */
    public <T> Long decr(String key) {

        return  jedisCluster.decr(key);


    }

    /**
     * Sting TO Bean
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return  (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * Bean To String
     * @param value
     * @param <T>
     * @return
     */
    public <T> String beanToString(T value) {
        if(value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return ""+value;
        }else if(clazz == String.class) {
            return (String)value;
        }else if(clazz == long.class || clazz == Long.class) {
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }


}
