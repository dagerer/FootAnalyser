package com.dager.analyser.handler;


import cn.hutool.core.collection.CollectionUtil;
import com.dager.analyser.base.PageDTO;
import com.dager.analyser.base.PageRequest;
import com.dager.analyser.channel.AnalyseBlockingQueue;
import com.dager.analyser.channel.AnalyseDataChannel;
import com.dager.analyser.common.AnalyseDataCommon;
import com.dager.analyser.common.dto.AnalyseQueueDTO;
import com.dager.analyser.common.dto.ThreadTaskDTO;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.loader.DefaultDataLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AnalyseDataHandlerImpl<R extends PageRequest, T> implements AnalyseDataHandler<T> {

    private final AnalyseDataChannel<T> channel;

    private final DefaultDataLoader<R, T> reader;

    private final AnalyseDataCommon<T> common;

    public AnalyseDataHandlerImpl(AnalyseDataChannel<T> channel,
                                  DefaultDataLoader<R, T> reader,
                                  AnalyseDataCommon<T> common) {
        this.channel = channel;
        this.reader = reader;
        this.common = common;
    }

    @Override
    public void handle() {
        long start = System.currentTimeMillis();
        AnalyseContext context = common.getContext();
        log.info("AnalyseDataHandlerImpl handleData request info:{}", context.getInformation());
        PageDTO<T> page = reader.load();
        if (page == null || page.getTotalCount() == null || page.getTotalCount() == 0) {
            return;
        }
        pushInAnalyseQueue(page.getContent());
        int totalPage = page.getTotalPage();
        log.info("com.dager.analyser.handler.AnalyseDataHandlerImpl handleData，总页数：{}，当前页：{}，处理到现在总耗时：{}",
                page.getTotalPage(), page.getPageNo(), (System.currentTimeMillis() - start));
        if (totalPage > 1) {
            ThreadTaskDTO taskDTO = new ThreadTaskDTO();
            for (int i = 2; i <= totalPage; i++) {
                reader.getRequest().setPageNo(i);
                page = reader.load();
                taskDTO.setData(page.getContent());
                taskDTO.setThreadNum(context.getBatchNum());
                common.getTaskService().batchHandle(param -> {
                    List<?> data = param.getData();
                    pushInAnalyseQueue((List<T>) data);
                    return null;
                }, taskDTO);
                log.info("com.dager.analyser.handler.AnalyseDataHandlerImpl handleData，总页数：{}，当前页：{}，处理到现在总耗时：{}",
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
