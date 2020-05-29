package com.dager.analyser.channel;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.common.AnalyseDataCommon;
import com.dager.analyser.common.dto.AnalyseQueueDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author dager
 */
@Slf4j
public class AnalyseBlockingQueueTask<T> {

    private final AnalyseBlockingQueue<T> blockingQueue;

    private final AnalyseDataCommon<T> common;

    private final Semaphore semaphore;

    public AnalyseBlockingQueueTask(AnalyseDataCommon<T> common,
                                    AnalyseBlockingQueue<T> blockingQueue) {
        this.common = common;
        this.blockingQueue = blockingQueue;
        semaphore = new Semaphore(common.getContext().getMaxAvailableNum());
        this.init();
    }

    public void init() {
        //获取队列
        common.getTaskService().getThreadPool().submit(this::consume);
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
                    common.getTaskService().getThreadPool().submit(() -> {
                        T data = finalBean.getData();
                        Object result = common.getAnalyser().analyse(common.getContext(), data);
                        common.getAnalyser().afterAnalyse(data, result);
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
