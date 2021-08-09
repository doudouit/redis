package com.doudou.redis.redistest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @decription:
 * @author: 180449
 * @date 2021/7/26 16:28
 */
@Component
public class TestRedis {

    @Autowired
    RedisTemplate redisTemplate;

    // springboot 的api
    @Autowired
    @Qualifier("ooxx")
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public void testRedis() {
        System.out.println("我就是一个注释");

        // 会用二进制数组插入redis
        redisTemplate.opsForValue().set("hello", "gogogo");
        System.out.println(redisTemplate.opsForValue().get("hello"));

        stringRedisTemplate.opsForValue().set("hello01", "china");
        System.out.println(stringRedisTemplate.opsForValue().get("hello01"));

        // 低阶api, 二进制安全的
        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();

        connection.set("hello02".getBytes(), "banana".getBytes());
        System.out.println(new String(connection.get("hello02".getBytes())));

        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
        hash.put("sean","name","zhouzhilei");
        hash.put("sean","age","22");
        System.out.println(hash.entries("sean"));

        Person p = new Person();
        p.setName("zhangsan");
        p.setAge(16);

        //stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));

        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);

        stringRedisTemplate.opsForHash().putAll("sean01",jm.toHash(p));

        Map map = stringRedisTemplate.opsForHash().entries("sean01");
        Person per = objectMapper.convertValue(map, Person.class);
        System.out.println(per.getName());


        // 发布订阅
        stringRedisTemplate.convertAndSend("ooxx","hello");

        RedisConnection cc = stringRedisTemplate.getConnectionFactory().getConnection();
        cc.subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());

        while(true){
            stringRedisTemplate.convertAndSend("ooxx","hello  from wo zi ji ");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
