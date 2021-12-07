参考文档：https://mp.weixin.qq.com/s/6di4KLUn5IZAepaHueK5cw

## 思路一
利用redis的zset,zset是一个有序集合，每一个元素(member)都关联了一个score,通过score排序来取集合中的值

添加元素:ZADD key score member [[score member] [score member] …]    
按顺序查询元素:ZRANGE key start stop [WITHSCORES]  
查询元素：score:ZSCORE key member    
移除元素:ZREM key member [member …]     
测试如下
```shell
# 添加单个元素
redis> ZADD page_rank 10 google.com

(integer) 1


# 添加多个元素
redis> ZADD page_rank 9 baidu.com 8 bing.com

(integer) 2

redis> ZRANGE page_rank 0 -1 WITHSCORES

1) "bing.com"
2) "8"
3) "baidu.com"
4) "9"
5) "google.com"
6) "10"

# 查询元素的score值

redis> ZSCORE page_rank bing.com
"8"

# 移除单个元素
 
redis> ZREM page_rank google.com

(integer) 1

redis> ZRANGE page_rank 0 -1 WITHSCORES

1) "bing.com"
2) "8"
3) "baidu.com"
4) "9"
```

## 思路二 
利用redis的过期通知机制（基于消息队列）
redis的消息队列是不可靠的，断开连接期间的消息，不能再消费，无法保证at last once语义

redis-conf 中加入配置
```shell
notify-keyspace-events Ex
```

