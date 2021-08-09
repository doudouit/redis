package com.doudou.redis;

import com.doudou.redis.spring.Svc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimpleTest {

    /**
     * 1.按照type在上下文中查找匹配的bean,查找type为Svc的bean
     *
     * 2. 如果有多个bean，则按照name进行匹配
     *  a.如果有@Qualifier注解，则按照@Qualifier指定的name进行匹配,查找name为svcA的bean
     *  b.如果没有，则按照变量名进行匹配,查找name为svc的bean
     *
     * 4. 匹配不到，则报错。（@Autowired(required=false)，如果设置required为false(默认为true)，则注入失败时不会抛出异常）
     */

    @Autowired
    // @Qualifier("svcA")
    Svc svcA;

    @Test
    void rc() {
        Assertions.assertNotNull(svcA);
        svcA.sayHello();
    }

}