package com.dager.analyser.thread.impl;


import com.alibaba.fastjson.JSON;
import com.dager.analyser.common.dto.ThreadTaskDTO;
import com.dager.analyser.thread.ThreadTaskService;
import com.dager.analyser.util.ThreadPoolExecutorUtil;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class ThreadTaskServiceImpl implements ThreadTaskService {

    private static final Integer MAX_THREAD_COUNT = 40;
    private static final Integer MAX_TASK_COUNT = 200000;


    private final LinkedBlockingQueue<Runnable> QUEUE;
    private final ThreadPoolExecutor executor;

    public ThreadTaskServiceImpl() {
        QUEUE = new LinkedBlockingQueue<>(MAX_TASK_COUNT);
        executor = ThreadPoolExecutorUtil.createThreadPoolExecutorAbortPolicy(MAX_THREAD_COUNT, QUEUE, "ThreadTaskService-Pool");
    }

    public ThreadTaskServiceImpl(int maxNum) {
        QUEUE = new LinkedBlockingQueue<>(MAX_TASK_COUNT);
        executor = ThreadPoolExecutorUtil.createThreadPoolExecutorAbortPolicy(maxNum, QUEUE, "ThreadTaskService-Pool");
    }

    public ThreadTaskServiceImpl(int maxNum, int maxTaskCount) {
        QUEUE = new LinkedBlockingQueue<>(maxTaskCount);
        executor = ThreadPoolExecutorUtil.createThreadPoolExecutorAbortPolicy(maxNum, QUEUE, "ThreadTaskService-Pool");
    }

    public ThreadTaskServiceImpl(int maxNum, String threadName) {
        QUEUE = new LinkedBlockingQueue<>(MAX_TASK_COUNT);
        executor = ThreadPoolExecutorUtil.createThreadPoolExecutorAbortPolicy(maxNum, QUEUE, threadName);
    }

    @Override
    public ThreadPoolExecutor getThreadPool() {
        if (!executor.isShutdown()) {
            return executor;
        }
        return ThreadPoolExecutorUtil.createThreadPoolExecutorAbortPolicy(MAX_THREAD_COUNT, QUEUE, "ThreadTaskService-Pool");
    }

    @Override
    public void batchHandle(BatchExecuteService service, ThreadTaskDTO threadTaskDTO) {
        Preconditions.checkArgument(threadTaskDTO != null && threadTaskDTO.getData() != null);
        Preconditions.checkArgument(threadTaskDTO.getThreadNum() != null && threadTaskDTO.getThreadNum() > 0);
        log.info("ThreadTaskServiceImpl batchHandle info:{}", JSON.toJSONString(threadTaskDTO));
        Integer threadNum = threadTaskDTO.getThreadNum();
        List<?> data = threadTaskDTO.getData();
        int dataSize = data.size();
        // 定义任务集合
        List<Callable<Object>> tasks = new ArrayList<>();
        Callable<Object> task;
        List<?> cutList;
        // 分割集合数据，放入对应的线程中
        for (int i = 0; i < threadNum; i++) {
            int start = i * dataSize / threadNum;
            int end = (i + 1) * dataSize / threadNum;
            cutList = data.subList(start, end);
            log.info("ThreadTaskServiceImpl handle subList start :{}, end:{}", start, end);
            final List<?> tempList = cutList;
            // 创建任务
            task = () -> {
                ThreadTaskDTO current = new ThreadTaskDTO(threadNum, tempList, null);
                return service.execute(current);
            };
            tasks.add(task);
        }
        try {
            getThreadPool().invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("ThreadTaskServiceImpl handle task error", e);
            Thread.currentThread().interrupt();
        }
        log.info("ThreadTaskServiceImpl handle task start all.");
    }

}
