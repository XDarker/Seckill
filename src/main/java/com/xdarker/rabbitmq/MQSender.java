package com.xdarker.rabbitmq;

import com.xdarker.service.IRedisClusterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by XDarker
 * 2018/8/26 15:21
 */
@Service("sender")
@Slf4j
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate ;
    @Autowired
    IRedisClusterService iRedisClusterService;

    public void sendSeckillMessage(SeckillMessage mm) {
        String msg = iRedisClusterService.beanToString(mm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }


}
