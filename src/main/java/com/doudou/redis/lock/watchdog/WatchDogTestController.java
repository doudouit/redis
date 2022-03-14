package com.doudou.redis.lock.watchdog;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @decription:
 * @author: 180449
 * @date 2022/3/14 16:15
 */
@RestController
public class WatchDogTestController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建定时任务线程工厂
     */
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("watchDog-").build();

    /**
     * 创建定时任务线程
     */
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(10, THREAD_FACTORY);

    /**
     * 存放看门狗返回的线程对象
     */
    private static final ConcurrentHashMap<String, ScheduledFuture> CONCURRENT_HASH_MAP = new ConcurrentHashMap<>(16);


    @GetMapping("/normal")
    public String normalRedisLock() throws InterruptedException {
        //给每一个线程都设置对应的UUID
        String productId = "product_huawei_p30";
        String stock = "stock";
        String clientId = UUID.randomUUID().toString();
        try {
            //如果线程已经被加锁，死循环等待释放锁
            while (true) {
                Integer stockNum = Integer.parseInt(stringRedisTemplate.opsForValue().get(stock));
                if (stockNum <= 0) {
                    return "商品已经卖完";
                }
                //线程加锁，为10秒钟，设置为对应的客户端ID
                Boolean setIfAbsent = stringRedisTemplate.opsForValue().setIfAbsent(productId, clientId, 10, TimeUnit.SECONDS);
                if (Objects.nonNull(setIfAbsent) && setIfAbsent) {
                    break;
                }
            }
            System.out.println("----------------------------------开始扣减库存----------------------------------");
            /**
             * 看门狗机制，目的是在线程业务处理时间过长时，导致锁被提前释放，导致处理完成时错误的释放掉另外线程的锁
             */
            WatchDogThread watchDogThread = new WatchDogThread(productId, clientId, stringRedisTemplate, CONCURRENT_HASH_MAP, SCHEDULED_EXECUTOR_SERVICE);
            ScheduledFuture<?> scheduledFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(watchDogThread, 1, 5, TimeUnit.SECONDS);
            /**
             * 采用ConcurrentHaspMap用来存储，watchDog任务，并且停止指定的watchDog任务
             */
            CONCURRENT_HASH_MAP.put(clientId, scheduledFuture);
            //执行业务逻辑
            int stockNum = Integer.parseInt(stringRedisTemplate.opsForValue().get(stock));
            if (stockNum > 0) {
                /*System.out.println("模拟业务处理时间过长，看门狗续命机制.....");
                Thread.sleep(20000);*/
                stringRedisTemplate.opsForValue().set(stock, String.valueOf(stockNum - 1));
                System.out.println("扣减库存成功库存数量为：" + stringRedisTemplate.opsForValue().get(stock));
            } else {
                System.out.println("库存扣减失败。。。。");
            }
        } catch (Exception e) {
            /**
             * 抛出异常时，获取到对应客户端ID的看门狗线程，并且停止看门狗机制
             */
            ScheduledFuture scheduledFuture = CONCURRENT_HASH_MAP.get(clientId);
            if (scheduledFuture != null) {
                System.out.println("异常信息，移除看门狗线程。。。");
                scheduledFuture.cancel(true);
                CONCURRENT_HASH_MAP.remove(clientId);
            }
        } finally {
            // 释放锁
            stringRedisTemplate.delete(productId);
            System.out.println("----------------------------------业务执行完成----------------------------------");
        }
        return "";

    }

}
