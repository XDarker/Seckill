package com.xdarker.controller;

import com.xdarker.cache.AccessKey;
import com.xdarker.cache.GoodsKey;
import com.xdarker.common.Const;
import com.xdarker.common.ResponseCode;
import com.xdarker.common.ServerResponse;
import com.xdarker.dao.SeckillMapper;
import com.xdarker.dto.Exposer;
import com.xdarker.dto.SeckillExecution;
import com.xdarker.dto.SeckillResult;
import com.xdarker.pojo.SecKillOrder;
import com.xdarker.pojo.SeckillGoods;
import com.xdarker.pojo.SeckillUser;
import com.xdarker.rabbitmq.MQSender;
import com.xdarker.rabbitmq.SeckillMessage;
import com.xdarker.service.IRedisClusterService;
import com.xdarker.service.ISeckillService;
import com.xdarker.service.ISeckillUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by XDarker
 * 2018/8/25 13:47
 */
@Controller
@RequestMapping("/seckill") // url: /模块/资源/{id}/细分
@Slf4j
public class SeckillController implements InitializingBean {

    private final ISeckillService iSeckillService;
    private IRedisClusterService iRedisClusterService;
    private final ISeckillUserService iSeckillUserService;
    private final SeckillMapper seckillMapper;
    private final MQSender sender;

    @Autowired
    public SeckillController(ISeckillService iSeckillService, IRedisClusterService iRedisClusterService, ISeckillUserService iSeckillUserService, SeckillMapper seckillMapper, MQSender sender) {
        this.iSeckillService = iSeckillService;
        this.iRedisClusterService = iRedisClusterService;
        this.iSeckillUserService = iSeckillUserService;
        this.seckillMapper = seckillMapper;
        this.sender = sender;
    }

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();
    /**
     * 系统初始化
     */
    public void afterPropertiesSet() throws Exception {
        List<SeckillGoods> goodsList = iSeckillService.getSeckillList();
        if (goodsList == null) {
            return;
        }
        for (SeckillGoods goods : goodsList) {
            iRedisClusterService.set(GoodsKey.SECKILLGOODSSTOCK + goods.getSeckillId(), goods.getNumber() + "");
            localOverMap.put(goods.getSeckillId(), false);
        }
    }

    /**
     * 获取秒杀商品列表页
     * @param model
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //获取列表页
        List<SeckillGoods> list = iSeckillService.getSeckillList();
        model.addAttribute("list", list);
        //list.jsp + model = MpdelAndView
        return "list";
    }

    /**
     * 获取秒杀商品详情页
     * @param seckillId
     * @param model
     * @return
     */
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        SeckillGoods seckillGoods = iSeckillService.getById(seckillId);
        if (seckillGoods == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckillGoods);
        return "detail";
    }

    /**
     * 暴露 秒杀接口地址
     *
     * @param seckillId
     * @return
     */
    //ajax json
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId, HttpServletRequest request) {
        SeckillResult<Exposer> result;

        //查询访问的次数
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String key = ip +"_"+ uri + "_" + seckillId;


        String timeString = iRedisClusterService.get(AccessKey.ACCESS + ":" + key);
        Integer count = iRedisClusterService.stringToBean(timeString, Integer.class);
        if (count == null) {//第一次访问
            iRedisClusterService.set(AccessKey.ACCESS + ":" + key, 1 + "");
            //设置过期时间
            iRedisClusterService.expire(AccessKey.ACCESS + ":" + key);
        } else if (count < 5) {
            iRedisClusterService.incr(AccessKey.ACCESS + ":" + key);
        } else {
            return new SeckillResult<Exposer>(false, ResponseCode.ACCESS_LIMIT.getDesc());
        }

        try {
            Exposer exposer = iSeckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            log.error(e.getMessage());
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    /**
     * 执行秒杀
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone", required = false) Long userPhone,
                                                   @PathVariable("md5") String md5) {
        if (userPhone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<Exposer> result;

        //内存标记，减少redis访问
        boolean over = localOverMap.get(seckillId);
        if (over) {
            SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
        //预减库存
        Long stock = iRedisClusterService.decr(GoodsKey.SECKILLGOODSSTOCK + seckillId);//10
        if (stock < 0) {
            localOverMap.put(seckillId, true);

            SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
        //说明有库存
        //判断是否已经秒杀到了
        SecKillOrder order = iSeckillService.getSecKillOrderByUserPhoneIdSeckillGoodsId(seckillId, userPhone);
        if (order != null) {
            //重复秒杀
            SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        }


        ServerResponse<SeckillUser> response = iSeckillUserService.getById(userPhone);
        SeckillUser user = response.getData();

        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setSecSkillGoodsId(seckillId);
        mm.setMd5(md5);//md5秒杀地址
        mm.setUserPhone(userPhone);
        sender.sendSeckillMessage(mm);

        SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.SUCCESS);
        return new SeckillResult<SeckillExecution>(true, execution);

  /*      try {
            SeckillExecution execution = iSeckillService.executeSeckill(seckillId,userPhone,md5);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (RepeatKillException e){
            // logger.error(e.getMessage());
            SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (SeckillCloseException e){
            //logger.error(e.getMessage());
            SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true,execution);
        }catch (Exception e){
            log.error(e.getMessage());
            SeckillExecution execution = new SeckillExecution(seckillId, Const.SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true,execution);
        }
    */

    }
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();

        long nowTime = now.getTime();


        return new SeckillResult(true, nowTime);
    }
}
