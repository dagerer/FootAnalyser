package com.dager.analyser.channel;

import com.dager.analyser.common.AnalyseDataCommon;
import lombok.Data;

/**
 * @author dager
 */
@Data
public class AnalyseDataChannel<T> {

    private AnalyseBlockingQueueTask<T> blockingQueueTask;

    private AnalyseBlockingQueue<T> blockingQueue;

    private AnalyseDataCommon<T> common;

    public AnalyseDataChannel() {

    }



}
