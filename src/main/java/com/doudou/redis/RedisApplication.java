package com.doudou.redis;

import com.doudou.redis.lock.RedisLock5;
import com.doudou.redis.redistest.TestRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisApplication.class, args);
        // TestRedis testRedis = context.getBean(TestRedis.class);
        // testRedis.testRedis();

        RedisLock5 redisLock5 = context.getBean(RedisLock5.class);
        redisLock5.getCatalogJsonDbWithRedisLock();
    }


}
