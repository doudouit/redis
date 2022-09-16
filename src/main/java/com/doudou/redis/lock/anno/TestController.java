package com.doudou.redis.lock.anno;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;

/**
 * @author 窦建新
 * @description
 * @date 2022/9/15 17:25
 */
@Slf4j
@RestController
public class TestController {

    @GetMapping("/testRedisLock")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.ONE, lockTime = 3)
    public Book testRedisLock(@RequestParam("userId") Long userId) {
        try {
            log.info("睡眠执行前");
            Thread.sleep(100000);
            log.info("睡眠执行后");
        } catch (Exception e) {
            // log error
            log.info("has some error", e);
        }
        return null;
    }


    @RequestMapping("/test")
    public Object test() {
        return "hello world";
    }
}
