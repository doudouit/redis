package com.doudou.redis.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @decription: 阶段二
 * @author: 180449
 * @date 2021/8/9 16:42
 *
 * 问题：setnx设置好，正要去设置过期时间，宕机。又死锁了。
 *
 * 解决方法：设置过期时间和占位必须是原子的。redis支持使用setnx ex命令
 *
 */
@Component
public class RedisLock2 {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Map<String, List<Catalog2Vo>> getCatalogJsonDbWithRedisLock() {
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "111");
        if (lock) {
            //设置过期时间
            stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catalog2Vo>> categoriesDb = getCategoryMap();
            stringRedisTemplate.delete("lock");
            return categoriesDb;
        }else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonDbWithRedisLock();
        }
    }

    private Map<String, List<Catalog2Vo>> getCategoryMap() {
        return null;
    }
}
