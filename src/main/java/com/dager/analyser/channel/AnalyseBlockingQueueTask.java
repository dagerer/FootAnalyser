package com.dager.analyser.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.context.Configuration;
import com.dager.analyser.context.dto.AnalyseQueueDTO;
import com.dager.analyser.thread.ThreadTaskService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author dager
 */
@Slf4j
public class AnalyseBlockingQueueTask<T> {

    private final AnalyseBlockingQueue<T> blockingQueue;

    private final ThreadTaskService taskService;

    private final AnalyseDataAnalyser<T> analyser;

    private final RuleHandler ruleHandler;

    private final Configuration config;

    private final Semaphore semaphore;

    public AnalyseBlockingQueueTask(AnalyseContext<T> context,
                                    AnalyseBlockingQueue<T> blockingQueue) {
        this.config = context.getConfig();
        this.blockingQueue = blockingQueue;
        this.analyser = context.getAnalyser();
        this.ruleHandler = context.getRuleHandler();
        this.taskService = context.getTaskService();
        this.semaphore = new Semaphore(config.getMaxAvailableNum());
        this.init();
    }

    public void init() {
        //获取队列
        taskService.getThreadPool().submit(this::consume);
        log.info("AnalyseBlockingQueueTask init poll end!");
    }

    private void consume() {
        while (true) {
            AnalyseQueueDTO<T> bean = null;
            try {
                bean = blockingQueue.poll(100L, TimeUnit.SECONDS);
                if (semaphore.tryAcquire()) {
                    Thread.sleep(100L);
                    log.info("AnalyseBlockingQueueTask The bean is: {} ", JSON.toJSONString(bean));
                    AnalyseQueueDTO<T> finalBean = bean;
                    taskService.getThreadPool().submit(() -> {
                        T data = finalBean.getData();
                        analyser.handle(data, ruleHandler);
                        if(config.isOverride()){
                            analyser.afterHandle(data, ruleHandler);
                        }
                    });
                } else {
                    log.info("AnalyseBlockingQueueTask no permit and do retry, bean:{}, current Thread:{}", JSON.toJSONString(bean), Thread.currentThread().getName());
                    blockingQueue.offer(bean);
                }
            } catch (Exception e) {
                log.error("AnalyseBlockingQueueTask exception while consume", e);
                if (bean != null) {
                    blockingQueue.offer(bean);
                }
            } finally {
                semaphore.release();
            }
        }
    }

}
