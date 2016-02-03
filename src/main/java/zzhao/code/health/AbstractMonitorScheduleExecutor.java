/*
 * @(#) ScheduleExecutorWithWarning.java 2015年12月3日
 * 
 * Copyright 2015 NetEase.com, Inc. All rights reserved.
 */
package zzhao.code.health;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * 带状态检查和提醒的定时任务执行器
 * @author zzhao
 * @version 2015年12月3日
 */
public abstract class AbstractMonitorScheduleExecutor {

    private static final Logger logger = Logger.getLogger(AbstractMonitorScheduleExecutor.class);

    private ScheduledExecutorService executor;

    private static ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    };

    public AbstractMonitorScheduleExecutor() {
        this(Executors.newSingleThreadScheduledExecutor(threadFactory));
    }

    public AbstractMonitorScheduleExecutor(int threadCount) {
        this(Executors.newScheduledThreadPool(threadCount, threadFactory));
    }
    
    public AbstractMonitorScheduleExecutor(ScheduledExecutorService executor) {
        if (executor == null) {
            throw new RuntimeException("executor can't be null");
        }
        this.executor = executor;
    }
    
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public ScheduledFuture<?> schedule(String name, Runnable command, long delay, TimeUnit unit) {
        return executor.schedule(command, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(String name, Callable<V> callable, long delay, TimeUnit unit) {
        return executor.schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(String name, Runnable command, long initialDelay, long period,
                    TimeUnit unit) {
        return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(String name, Runnable command, long initialDelay, long delay,
                    TimeUnit unit) {
        return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

}
