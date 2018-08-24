package com.xdarker.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * Redis集群配置
 * Created by XDarker
 * 2018/8/24 22:24
 */
@Configuration
@ConditionalOnClass({JedisCluster.class})
public class RedisConfig {
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
//    @Value("${spring.redis.timeout}")
//    private int timeout;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private String maxWaitMillis;
    @Value("${spring.redis.commandTimeout}")
    private int commandTimeout;



    @Bean
    public JedisCluster getJedisCluster() {
        String[] cNodes = clusterNodes.split(",");
        Set<HostAndPort> nodes =new HashSet<>();
        //分割出集群节点
        for(String node : cNodes) {
            String[] hp = node.split(":");
            nodes.add(new HostAndPort(hp[0],Integer.parseInt(hp[1])));
        }
        JedisPoolConfig jedisPoolConfig =new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(maxWaitMillis.substring(0,maxWaitMillis.length()-2)));
        //创建集群对象
//      JedisCluster jedisCluster = new JedisCluster(nodes,commandTimeout);
        return new JedisCluster(nodes,commandTimeout,jedisPoolConfig);
    }


}