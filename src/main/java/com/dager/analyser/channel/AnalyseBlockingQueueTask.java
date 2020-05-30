package com.dager.analyser.channel;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.context.AnalyseContext;
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
        semaphore = new Semaphore(context.getMaxAvailableNum());
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
                    log.info("AnalyseBlockingQueueTask The bean is : {} ", JSON.toJSONString(bean));
                    AnalyseQueueDTO<T> finalBean = bean;
                    context.getTaskService().getThreadPool().submit(() -> {
                        T data = finalBean.getData();
                        Object result = context.getAnalyser().analyse(context, data);
                        context.getAnalyser().afterAnalyse(data, result);
                    });
                }else{
                    log.info("no permit do retry, bean:" + JSON.toJSONString(bean) + "Thread:" + Thread.currentThread().getName());
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
