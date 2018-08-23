package com.xdarker.dao;

import com.xdarker.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by XDarker
 * 2018/8/22 22:14
 */
@Mapper
public interface SeckillMapper {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，表示更新库存的记录行数
     */
    int reduceNumber(@Param("seckillId") Long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
    SeckillGoods queryById(Long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<SeckillGoods> queryAll(@Param("offset") Integer offset, @Param("limit")Integer limit);


    /**
     * 根据 id 查库存
     * @param secKillId
     * @return
     */
    int getStockById(Long secKillId);

}
