package com.doudou.redis.delayTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Set;

/**
 * @decription: 测试
 * @author: 180449
 * @date 2021/12/7 9:36
 */
@Component
public class AppTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //生产者,生成5个订单放进去
    public void productionDelayMessage() {
        for (int i = 0; i < 5; i++) {
            //延迟3秒
            Calendar cal1 = Calendar.getInstance();
            cal1.add(Calendar.SECOND, 3);
            ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();
            int second3later = (int) (cal1.getTimeInMillis() / 1000);
            zSet.add("OrderId", "OID0000001" + i, second3later);
            System.out.println(System.currentTimeMillis() + "ms:redis生成了一个订单任务：订单ID为" + "OID0000001" + i);
        }
    }


    //消费者，取订单
    public void consumerDelayMessage() {
        ZSetOperations<String, String> zSet = stringRedisTemplate.opsForZSet();
        while (true) {
            Set<ZSetOperations.TypedTuple<String>> items = zSet.rangeWithScores("OrderId", 0, 1);
            if (items == null || items.isEmpty()) {
                System.out.println("当前没有等待的任务");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            }
            double score = ((ZSetOperations.TypedTuple) items.toArray()[0]).getScore();
            Calendar cal = Calendar.getInstance();
            int nowSecond = (int) (cal.getTimeInMillis() / 1000);
            //System.out.println("currentTiem: " + nowSecond + " socre: " + score);
            if (nowSecond >= score) {
                String orderId = (String) ((ZSetOperations.TypedTuple) items.toArray()[0]).getValue();
                Long num = zSet.remove("OrderId", orderId);
                if (num != null && num > 0) {
                    // 防止多个consumer消费多次
                    System.out.println(System.currentTimeMillis() + "ms:redis消费了一个任务：消费的订单OrderId为" + orderId);
                }
            }
        }

    }

    public static void main(String[] args) {
        AppTest appTest = new AppTest();
        appTest.productionDelayMessage();
        appTest.consumerDelayMessage();
    }

}
