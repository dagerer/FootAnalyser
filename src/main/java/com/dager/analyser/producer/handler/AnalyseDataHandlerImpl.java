package com.dager.analyser.producer.handler;


import cn.hutool.core.collection.CollectionUtil;
import com.dager.analyser.producer.base.PageDTO;
import com.dager.analyser.producer.base.PageRequest;
import com.dager.analyser.channel.AnalyseBlockingQueue;
import com.dager.analyser.channel.AnalyseDataChannel;
import com.dager.analyser.context.dto.AnalyseQueueDTO;
import com.dager.analyser.context.dto.ThreadTaskDTO;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.producer.loader.DefaultDataLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AnalyseDataHandlerImpl<R extends PageRequest, T> implements AnalyseDataHandler<T> {

    private final AnalyseDataChannel<T> channel;

    private final DefaultDataLoader<R, T> reader;

    private final AnalyseContext<T> context;

    public AnalyseDataHandlerImpl(AnalyseDataChannel<T> channel,
                                  DefaultDataLoader<R, T> reader,
                                  AnalyseContext<T> context) {
        this.channel = channel;
        this.reader = reader;
        this.context = context;
    }

    @Override
    public void handle() {
        long start = System.currentTimeMillis();
        if (context.getCommonInfo() != null) {
            String information = context.getCommonInfo().getInformation();
            log.info("AnalyseDataHandlerImpl handleData request info:{}", information);
        }
        PageDTO<T> page = reader.load();
        if (page == null || page.getTotalCount() == null || page.getTotalCount() == 0) {
            return;
        }
        pushInAnalyseQueue(page.getContent());
        int totalPage = page.getTotalPage();
        log.info("com.dager.analyser.producer.handler.AnalyseDataHandlerImpl handleData，总页数：{}，当前页：{}，处理到现在总耗时：{}",
                page.getTotalPage(), page.getPageNo(), (System.currentTimeMillis() - start));
        if (totalPage > 1) {
            ThreadTaskDTO taskDTO = new ThreadTaskDTO();
            for (int i = 2; i <= totalPage; i++) {
                reader.getRequest().setPageNo(i);
                page = reader.load();
                taskDTO.setData(page.getContent());
                taskDTO.setThreadNum(context.getConfig().getBatchNum());
                context.getTaskService().batchHandle(param -> {
                    List<?> data = param.getData();
                    pushInAnalyseQueue((List<T>) data);
                    return null;
                }, taskDTO);
                log.info("com.dager.analyser.producer.handler.AnalyseDataHandlerImpl handleData，总页数：{}，当前页：{}，处理到现在总耗时：{}",
                        page.getTotalPage(), page.getPageNo(), (System.currentTimeMillis() - start));
            }
        }
    }

    @Override
    public void pushInAnalyseQueue(List<T> data) {
        AnalyseBlockingQueue<T> blockingQueue = channel.getBlockingQueue();
        if (CollectionUtil.isNotEmpty(data)) {
            data.forEach(d -> {
                AnalyseQueueDTO<T> analyseQueueDTO = new AnalyseQueueDTO<>();
                analyseQueueDTO.setData(d);
                blockingQueue.offer(analyseQueueDTO);
            });
        }
    }
}
