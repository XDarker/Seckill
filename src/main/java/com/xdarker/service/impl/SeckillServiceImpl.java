package com.xdarker.service.impl;

import com.xdarker.common.Const;
import com.xdarker.dao.SeckillMapper;
import com.xdarker.dao.SuccessKilledMapper;
import com.xdarker.dto.Exposer;
import com.xdarker.dto.SeckillExecution;
import com.xdarker.exception.RepeatKillException;
import com.xdarker.exception.SeckillCloseException;
import com.xdarker.exception.SeckillException;
import com.xdarker.pojo.SecKillOrder;
import com.xdarker.pojo.SeckillGoods;
import com.xdarker.service.IRedisClusterService;
import com.xdarker.service.ISeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**秒杀业务实现类
 * Created by XDarker
 * 2018/8/24 14:43
 */
@Service("iSeckillService")
@Slf4j
public class SeckillServiceImpl implements ISeckillService {


    //颜值 用于混淆MD5;
    private final String salt = "ABCDDCBA";

    private final SeckillMapper seckillMapper;
    private final SuccessKilledMapper successKilledMapper;
    private final IRedisClusterService iRedisClusterService;
    
    @Autowired
    public SeckillServiceImpl(SeckillMapper seckillMapper, SuccessKilledMapper successKilledMapper, IRedisClusterService iRedisClusterService) {
        this.seckillMapper = seckillMapper;
        this.successKilledMapper = successKilledMapper;
        this.iRedisClusterService = iRedisClusterService;
    }

    public List<SeckillGoods> getSeckillList() {
            return seckillMapper.queryAll(0,4);
        }

        public SeckillGoods getById(Long seckillId) {
            //1:访问redis
            String seckillGoodsString = iRedisClusterService.get(seckillId+"");
            //转换成对象
            SeckillGoods seckillGoods = iRedisClusterService.stringToBean(seckillGoodsString,SeckillGoods.class);

            if (seckillGoods == null) {
                //2: 访问数据库
                seckillGoods = seckillMapper.queryById(seckillId);
                if (seckillGoods != null) {
                    //3: 放入redis
                    seckillGoodsString = iRedisClusterService.beanToString(seckillGoods);
                    iRedisClusterService.set("seckillGoods:"+ seckillGoods.getSeckillId(),seckillGoodsString);
                }
            }
            return seckillMapper.queryById(seckillId);
        }

        public Exposer exportSeckillUrl(Long seckillId) {

            //优化点: 缓存优化 : 超时维护一致性
            /**
             * get from cache
             *if null
             *  get db
             *  else
             *      put cache
             *      Login
             */
            //1:访问redis
            String seckillGoodsString = iRedisClusterService.get(seckillId+"");
            //转换成对象
            SeckillGoods seckillGoods = iRedisClusterService.stringToBean(seckillGoodsString,SeckillGoods.class);

            if (seckillGoods == null){
                //2: 访问数据库
                seckillGoods = seckillMapper.queryById(seckillId);
                if (seckillGoods == null) {
                    return new Exposer(false, seckillId);
                }else {
                    //3: 放入redis
                    seckillGoodsString = iRedisClusterService.beanToString(seckillGoods);
                    iRedisClusterService.set("seckillGoods:"+ seckillGoods.getSeckillId(),seckillGoodsString);
                }
            }

            Date startTime = seckillGoods.getStartTime();
            Date endTime = seckillGoods.getEndTime();
            //系统当前时间
            Date nowTime = new Date();

            if(nowTime.getTime() < startTime.getTime()
                    || nowTime.getTime() > endTime.getTime()){
                return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
            }

            //转化特定字符串的过程， 不可逆
            String md5 = getMD5(seckillId);

            return new Exposer(true,md5,seckillId);
        }


        /**
         *使用注解控制事务方法的优点
         * 1: 开发团队达成一致约定， 明确标注事务方法的编程风格。
         * 2: 保证事务方法的执行时间尽可能短，不要穿插其他的网络操作。RPC/Http请求或者剥离到事务方法外部。
         * 3: 不是所有的方法都需要事务， 如只有一条修改操作，只读操作不需要控制事物。
         */
        @Transactional
        public SeckillExecution executeSeckill(Long seckilled, Long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
            if(md5 == null || !md5.equals(getMD5(seckilled))){
                throw new SeckillException("SeckillGoods data rewrite");
            }
            //执行秒杀逻辑 减库存+记录购买行为
            Date nowTime = new Date();

            try {
                //记录购买行为
                int insertCount = successKilledMapper.insertSuccessKilled(seckilled, userPhone);
                //唯一: seckilled + userPhone;
                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("SeckillGoods repeated");
                } else {
                    //减库存、热点商品竞争
                    int updateCount = seckillMapper.reduceNumber(seckilled, nowTime);
                    if (updateCount <= 0) {
                        //没有更新到记录 秒杀结束 rollback
                        throw new SeckillCloseException("SecKill is closed");
                    } else {
                        //秒杀成功 commit
                        SecKillOrder successKilled = successKilledMapper.queryByIdWithSeckill(seckilled, userPhone);
                        //订单信息放入缓存
                        String secKillOrderString = iRedisClusterService.beanToString(successKilled);
                        iRedisClusterService.set(seckilled+"_"+userPhone,secKillOrderString);
                        return new SeckillExecution(seckilled, Const.SeckillStateEnum.SUCCESS, successKilled);
                    }
                }
            }catch (SeckillCloseException e1){
                throw e1;
            }catch (RepeatKillException e2){
                throw e2;
            }catch(Exception e){
                log.error(e.getMessage());
                //所有编译器异常 转化为运行期异常
                throw  new SeckillException("seckill inner error:"+e.getMessage());
            }

        }

        public SecKillOrder getSecKillOrderByUserPhoneIdSeckillGoodsId(Long seckillGoodsId,Long userPhoneId) {
            //return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);

            //秒杀商品id + 手机号
            String secKillOrderString = iRedisClusterService.get(seckillGoodsId+"_"+userPhoneId);
            //将字符串 转换成 对象
            return iRedisClusterService.stringToBean(secKillOrderString,SecKillOrder.class);
        }

        public int getStockById(Long secKillId){

            return seckillMapper.getStockById(secKillId);
        }

        private String getMD5(Long seckillId){
            String base = seckillId+"/"+salt;
            String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
            return md5;
        }


}
