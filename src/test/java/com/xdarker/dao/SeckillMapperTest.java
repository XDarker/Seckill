package com.xdarker.dao;

import com.xdarker.pojo.SeckillGoods;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by XDarker
 * 2018/8/23 22:02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SeckillMapperTest {

    @Autowired
    private SeckillMapper seckillMapper;

    @Test
    public void reduceNumber() {
    }

    @Test
    public void queryById() {

        SeckillGoods seckillGoods = seckillMapper.queryById(1000L);
        log.info("result:{}",seckillGoods);
        System.out.println(seckillGoods);
    }

    @Test
    public void queryAll() {
    }

    @Test
    public void getStockById() {
    }
}