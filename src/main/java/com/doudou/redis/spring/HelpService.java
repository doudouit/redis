package com.doudou.redis.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class HelpService {
    @Autowired
    @Qualifier("svcB")
    private Svc svc;

    public void sayHello() {
        svc.sayHello();
    }
}