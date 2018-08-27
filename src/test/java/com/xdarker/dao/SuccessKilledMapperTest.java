package com.xdarker.dao;

import com.xdarker.pojo.SecKillOrder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by XDarker
 * 2018/8/26 12:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SuccessKilledMapperTest {

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Test
    public void insertSuccessKilled() {

        int result = successKilledMapper.insertSuccessKilled(1000L, 13735475375L);
        System.out.println(result);
    }

    @Test
    public void queryByIdWithSeckill() {
        SecKillOrder secKillOrder = successKilledMapper.queryByIdWithSeckill(1000L, 13735475375L);
        log.info("secKillOrder :{}",secKillOrder);
    }
}