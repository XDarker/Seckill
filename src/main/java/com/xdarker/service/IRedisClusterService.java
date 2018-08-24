package com.xdarker.service;

/**
 * Redis集群服务接口
 * Created by XDarker
 * 2018/8/24 21:41
 */
public interface IRedisClusterService {

    String set(String key, String value);

    String setex(String key, String value);

    String get(String key);

    Long expire(String key);

    boolean delete(String key);

    <T> Long incr(String key);

    <T> Long decr(String key);

    <T> T stringToBean(String str, Class<T> clazz);

    <T> String beanToString(T value);


}
