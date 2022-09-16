package com.doudou.redis.lock.anno;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 窦建新
 * @description
 * @date 2022/9/16 9:40
 */

public class BasicThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup threadGroup;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    private Boolean daemon;

    BasicThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        daemon = false;
    }

    BasicThreadFactory (Builder builder) {
        SecurityManager s = System.getSecurityManager();
        threadGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        namePrefix = builder.namePrefix;
        daemon = builder.daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(threadGroup, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    public static final class Builder {

        private String namePrefix = null;
        private Boolean daemon = null;
        public Builder namingPattern(String name) {
            this.namePrefix = name +  "-thread-";
            return this;
        }

        public Builder daemon(Boolean daemon)  {
            this.daemon = daemon;
            return this;
        }

        public BasicThreadFactory build() {
            return new BasicThreadFactory(this);
        }
    }

}
