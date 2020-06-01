package com.dager.analyser.handler.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.dager.analyser.base.PageDTO;
import com.dager.analyser.base.PageRequest;
import com.dager.analyser.channel.AnalyseBlockingQueue;
import com.dager.analyser.channel.AnalyseDataChannel;
import com.dager.analyser.common.AnalyseDataCommon;
import com.dager.analyser.common.dto.AnalyseQueueDTO;
import com.dager.analyser.common.dto.ThreadTaskDTO;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.handler.AnalyseDataHandler;
import com.dager.analyser.loader.DefaultDataLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AnalyseDataHandlerImpl<R extends PageRequest, T> implements AnalyseDataHandler<T> {

    private final AnalyseDataChannel<T> channel;

    private final DefaultDataLoader<R, T> loader;

    private final AnalyseDataCommon<T> common;

    public AnalyseDataHandlerImpl(AnalyseDataChannel<T> channel,
                                  DefaultDataLoader<R, T> loader,
                                  AnalyseDataCommon<T> common) {
        this.channel = channel;
        this.loader = loader;
        this.common = common;
    }

    @Override
    public void handle() {
        long start = System.currentTimeMillis();
        AnalyseContext context = common.getContext();
        log.info("AnalyseDataHandlerImpl handleData request info:{}", context.getInformation());
        PageDTO<T> page = loader.load();
        if (page == null || page.getTotalCount() == null || page.getTotalCount() == 0) {
            return;
        }
        pushInAnalyseQueue(page.getContent());
        int totalPage = page.getTotalPage();
        log.info("AnalyseDataHandlerImpl handleData，总页数：{}，当前页：{}，处理到现在总耗时：{}",
                page.getTotalPage(), page.getPageNo(), (System.currentTimeMillis() - start));
        if (totalPage > 1) {
            ThreadTaskDTO taskDTO = new ThreadTaskDTO();
            for (int i = 2; i <= totalPage; i++) {
                loader.getRequest().setPageNo(i);
                page = loader.load();
                taskDTO.setData(page.getContent());
                taskDTO.setThreadNum(context.getBatchNum());
                common.getTaskService().batchHandle(param -> {
                    pushInAnalyseQueue((List<T>) param.getData());
                    return null;
                }, taskDTO);
                log.info("AnalyseDataHandlerImpl handleData，总页数：{}，当前页：{}，处理到现在总耗时：{}",
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
