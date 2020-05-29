package com.dager.analyser.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class ThreadPoolExecutorUtil {


    public static ThreadPoolExecutor createThreadPoolExecutor(int threadCount, int queueSize, String threadName) {
        return new ThreadPoolExecutor(threadCount, threadCount, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize)
                , new ThreadFactoryBuilder().setNameFormat(threadName).build(), new RejectPolicy(threadName));
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(int threadCount, LinkedBlockingQueue queue, String threadName) {
        return new ThreadPoolExecutor(threadCount, threadCount, 120, TimeUnit.SECONDS, queue
                , new ThreadFactoryBuilder().setNameFormat(threadName).build(), new RejectPolicy(threadName));
    }

    public static ThreadPoolExecutor createThreadPoolExecutorAbortPolicy(int threadCount, LinkedBlockingQueue queue, String threadName) {
        return new ThreadPoolExecutor(threadCount, threadCount, 120, TimeUnit.SECONDS, queue
                , new ThreadFactoryBuilder().setNameFormat(threadName).build(), new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadPoolExecutor createThreadPoolExecutorDiscardPolicy(int threadCount, int queueSize, String threadName) {
        return new ThreadPoolExecutor(threadCount, threadCount, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize)
                , new ThreadFactoryBuilder().setNameFormat(threadName).build(), new ThreadPoolExecutor.DiscardPolicy());
    }

    public static ScheduledExecutorService createScheduledExecutor(int threadCount, String threadName) {
        return Executors.newScheduledThreadPool(threadCount, new ThreadFactoryBuilder().setNameFormat(threadName).build());
    }


    public static class RejectPolicy implements RejectedExecutionHandler {

        private final String threadName;

        public RejectPolicy(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.warn(threadName + " reject task:" + r.getClass().toString());
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " +
                    e.toString());
        }
    }

}
