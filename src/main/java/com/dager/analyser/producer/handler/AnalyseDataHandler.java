package com.dager.analyser.producer.handler;

import java.util.List;

/**
 * @author dager
 */
public interface AnalyseDataHandler<T> {

    void handle();

    void pushInAnalyseQueue(List<T> data);

}
