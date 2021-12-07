package com.doudou.redis.delayTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 思路二实现
 */
@Component
public class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void init() {
        new Thread(() -> {
            RedisConnection connection = stringRedisTemplate.getConnectionFactory().getConnection();
            connection.subscribe(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    System.out.println(System.currentTimeMillis() + "ms:" + message + "订单取消");
                }
            }, "__keyevent@0__:expired".getBytes());
        }).start();
    }

    public void start() {
        init();
        for (int i = 0; i < 10; i++) {
            String orderId = "OID000000" + i;
            stringRedisTemplate.opsForValue().set(orderId, orderId, Duration.ofSeconds(3));
            System.out.println(System.currentTimeMillis() + "ms:" + orderId + "订单生成");
        }
    }

}