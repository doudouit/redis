package com.doudou.redis;

import com.doudou.redis.redistest.TestRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RedisApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RedisApplication.class, args);
        TestRedis testRedis = context.getBean(TestRedis.class);
        testRedis.testRedis();
    }

}
