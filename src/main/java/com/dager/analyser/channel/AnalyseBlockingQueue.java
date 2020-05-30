package com.dager.analyser.channel;

import com.dager.analyser.context.dto.AnalyseQueueDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * @author dager
 */
@Slf4j
public class AnalyseBlockingQueue<T> {

    private static final int DEFAULT_CAPACITY = 200_000;

    private final BlockingQueue<AnalyseQueueDTO<T>> queue;

    public AnalyseBlockingQueue() {
        this(DEFAULT_CAPACITY);
    }

    public AnalyseBlockingQueue(int capacity) {
        queue = new LinkedBlockingQueue<>(capacity);
    }

    public void offer(AnalyseQueueDTO<T> dto) {
        queue.offer(dto);
    }

    AnalyseQueueDTO<T> poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    public int size() {
        return queue.size();
    }
}
