package com.xdarker.rabbitmq;

import com.xdarker.dao.SeckillMapper;
import com.xdarker.dao.SuccessKilledMapper;
import com.xdarker.pojo.SecKillOrder;
import com.xdarker.pojo.SeckillGoods;
import com.xdarker.pojo.SeckillUser;
import com.xdarker.service.IRedisClusterService;
import com.xdarker.service.ISeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by XDarker
 * 2018/8/26 15:31
 */
@Service
@Slf4j
public class MQReceiver {

    private final IRedisClusterService iRedisClusterService;
    private final ISeckillService iSeckillService;
    private final SuccessKilledMapper successKilledMapper;
    private final SeckillMapper seckillMapper;
    @Autowired
    public MQReceiver(IRedisClusterService iRedisClusterService, ISeckillService iSeckillService, SuccessKilledMapper successKilledMapper, SeckillMapper seckillMapper) {
        this.iRedisClusterService = iRedisClusterService;
        this.iSeckillService = iSeckillService;
        this.successKilledMapper = successKilledMapper;
        this.seckillMapper = seckillMapper;
    }


    @RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
    public void receive(String message) {
        log.info("receive message:"+message);
        SeckillMessage mm  = iRedisClusterService.stringToBean(message, SeckillMessage.class);
        SeckillUser user = mm.getUser();
        Long secKillGoodsId = mm.getSecSkillGoodsId();
        String md5 = mm.getMd5();
        Long userPhone = mm.getUserPhone();

        SeckillGoods goods = seckillMapper.queryById(secKillGoodsId);
        int stock = goods.getNumber();
        if(stock < 0) {
            return;
        }
        //判断是否已经秒杀到了
        //秒杀ID + 手机号
        SecKillOrder order = iSeckillService.getSecKillOrderByUserPhoneIdSeckillGoodsId(user.getId(), secKillGoodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        iSeckillService.executeSeckill(secKillGoodsId,userPhone,md5);
    }


//	    //用于测试
//		@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
//		public void receive(String message) {
//			log.info("receive message:"+message);
//
//
//			SeckillGoods goods = seckillMapper.queryById(1002L);
//	    	int stock = goods.getNumber();
//	    	if(stock <= 0) {
//	    		return;
//	    	}
//
//				Date nowTime = new Date();
//	    	//减库存 下订单 写入秒杀订单
//			seckillMapper.reduceNumber(1002L,nowTime);
//		}

}
