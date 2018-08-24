package com.xdarker.service;

import com.xdarker.dto.Exposer;
import com.xdarker.dto.SeckillExecution;
import com.xdarker.exception.RepeatKillException;
import com.xdarker.exception.SeckillCloseException;
import com.xdarker.exception.SeckillException;
import com.xdarker.pojo.SecKillOrder;
import com.xdarker.pojo.SeckillGoods;

import java.util.List;

/**秒杀业务接口
 * Created by XDarker
 * 2018/8/24 14:29
 */
public interface ISeckillService {

    /**
     * 查询所有秒杀记录
     * @return
     */
    List<SeckillGoods> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    SeckillGoods getById(Long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(Long seckillId);

    /**
     * 执行秒杀操作
     * @param seckilled
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(Long seckilled, Long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException;

    SecKillOrder getSecKillOrderByUserPhoneIdSeckillGoodsId(Long seckillGoodsId, Long userPhoneId);

    int getStockById(Long secKillId);
}
