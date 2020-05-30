package com.dager.analyser.channel;

import lombok.Data;

/**
 * @author dager
 */
@Data
public class AnalyseDataChannel<T> {

    private AnalyseBlockingQueueTask<T> blockingQueueTask;

    private AnalyseBlockingQueue<T> blockingQueue;


    public AnalyseDataChannel() {

    }



}
