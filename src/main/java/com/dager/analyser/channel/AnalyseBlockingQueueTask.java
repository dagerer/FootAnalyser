package com.dager.analyser.channel;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.context.Configuration;
import com.dager.analyser.context.dto.AnalyseQueueDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author dager
 */
@Slf4j
public class AnalyseBlockingQueueTask<T> {

    private final AnalyseBlockingQueue<T> blockingQueue;

    private final AnalyseContext<T> context;

    private final Semaphore semaphore;

    public AnalyseBlockingQueueTask(AnalyseContext<T> context,
                                    AnalyseBlockingQueue<T> blockingQueue) {
        this.context = context;
        this.blockingQueue = blockingQueue;
        semaphore = new Semaphore(new Configuration().getMaxAvailableNum());
        this.init();
    }

    public void init() {
        //获取队列
        context.getTaskService().getThreadPool().submit(this::consume);
        log.info("AnalyseBlockingQueueTask init poll end!");
    }

    private void consume() {
        while (true) {
            AnalyseQueueDTO<T> bean = null;
            try {
                bean = blockingQueue.poll(100L, TimeUnit.SECONDS);
                if(semaphore.tryAcquire()){
                    Thread.sleep(100L);
                    log.info("AnalyseBlockingQueueTask The bean is: {} ", JSON.toJSONString(bean));
                    AnalyseQueueDTO<T> finalBean = bean;
                    context.getTaskService().getThreadPool().submit(() -> {
                        T data = finalBean.getData();
                        AnalyseDataAnalyser<T> analyser = context.getAnalyser();
                        // 如果没开启规则引擎，则走重写分析数据
                        Object result = analyser.analyse(context, data);
                        analyser.afterAnalyse(data, result);
                        // TODO 如果开启，走规则引擎逻辑
                    });
                }else{
                    log.info("no permit and do retry, bean:{}, current Thread:{}",JSON.toJSONString(bean),Thread.currentThread().getName());
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
