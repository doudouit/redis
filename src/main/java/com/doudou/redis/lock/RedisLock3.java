package com.doudou.redis.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @decription: 阶段三
 * @author: 180449
 * @date 2021/8/9 16:45
 *
 *
 * 问题：删除锁直接删除？？？如果由于业务时间很长，锁自己过期了，我们直接删除，有可能把别人正在持有的锁删除了
 *
 * 解决：占锁的时候，值指定为uuid，每个人匹配是自己的锁才删除。
 */
@Component
public class RedisLock3 {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Map<String, List<Catalog2Vo>> getCatalogJsonDbWithRedisLock() {
        //加锁的同时设置过期时间，二者是原子性操作
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "1111",5, TimeUnit.SECONDS);
        if (lock) {
            Map<String, List<Catalog2Vo>> categoriesDb = getCategoryMap();
            //模拟超长的业务执行时间
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private Map<String,List<Catalog2Vo>> getCategoryMap() {
        return null;
    }
}
