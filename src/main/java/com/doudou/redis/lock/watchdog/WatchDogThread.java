package com.doudou.redis.lock.watchdog;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WatchDogThread implements Runnable {

    private String productId;
    private String clientId;
    private StringRedisTemplate stringRedisTemplate;
    private ConcurrentHashMap<String, ScheduledFuture> cacheMap;
    //获取到线程池的引用
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * lua脚本，目的原子操作，获取到商品锁如果等于当前客户端ID，执行锁续命
     */
    private static final String SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then" +
            " local ttl = tonumber(redis.call('ttl',KEYS[1]));" +
            " redis.call('expire',KEYS[1],ttl+ARGV[2]) return redis.call('ttl',KEYS[1]) end";

    public WatchDogThread(String productId, String clientId, StringRedisTemplate stringRedisTemplate,
                          ConcurrentHashMap<String, ScheduledFuture> concurrentHashMap, ScheduledExecutorService scheduledExecutorService) {
        this.clientId = clientId;
        this.productId = productId;
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheMap = concurrentHashMap;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    /**
     * 这里还有点小问题，查询操作与续期操作不是原子的
     */
    @Override
    public void run() {
        String lock = stringRedisTemplate.opsForValue().get(productId);
        try {
            //如果获取到锁为空，或者获取到的锁不等于当前客户端ID，那么就直接停止看门狗
            if (StringUtils.isEmpty(lock) || !clientId.equals(lock)) {
                ScheduledFuture scheduledFuture = cacheMap.get(clientId);
                if (scheduledFuture != null) {
                    System.out.println("库存扣减完成，关闭开门狗。。。");
                    scheduledFuture.cancel(true);
                    cacheMap.remove(clientId);
                }
                return;
            }
            System.out.println("执行续命任务ID:" + lock);
            //执行lua脚本，用来原子性执行锁续命
            stringRedisTemplate.execute(new DefaultRedisScript(SCRIPT, Long.class), Collections.singletonList(productId), clientId, "10");
            Long expire = stringRedisTemplate.getExpire(productId, TimeUnit.SECONDS);
            System.out.println("续命后时间；" + expire);
        } catch (Exception e) {
            System.out.println("watchdog执行失败" + e.getMessage());
            /**
             * 如果watchDog执行续命任务出现异常，直接设置30秒过期时间，防止key值失效，导致误删
             */
            this.stringRedisTemplate.expire(productId, 30, TimeUnit.SECONDS);
            /*WatchDogThread watchDogThread = new WatchDogThread(productId,clientId,stringRedisTemplate,this.cacheMap,this.scheduledExecutorService);
            this.scheduledExecutorService.scheduleAtFixedRate(watchDogThread, 1, 5, TimeUnit.SECONDS);*/
        }
    }
}
