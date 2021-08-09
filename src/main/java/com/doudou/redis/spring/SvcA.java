package com.doudou.redis.spring;

import org.springframework.stereotype.Service;

@Service
public class SvcA implements Svc {

    @Override
    public void sayHello() {
        System.out.println("hello, this is service A");
    }

}