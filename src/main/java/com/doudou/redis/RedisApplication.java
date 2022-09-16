package com.doudou.redis;

import com.doudou.redis.delayTask.AppTest;
import com.doudou.redis.delayTask.RedisTest;
import com.doudou.redis.lock.RedisLock5;
import com.doudou.redis.redistest.TestRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
        // TestRedis testRedis = context.getBean(TestRedis.class);
        // testRedis.testRedis();

        /*RedisLock5 redisLock5 = context.getBean(RedisLock5.class);
        redisLock5.getCatalogJsonDbWithRedisLock();*/

        /*AppTest apptest = context.getBean(AppTest.class);
        apptest.productionDelayMessage();
        apptest.consumerDelayMessage();*/


        // 测试订阅过期消息
        /*RedisTest redisTest = context.getBean(RedisTest.class);
        redisTest.start();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


}
