package com.dager.analyser.handler;

import java.util.List;

/**
 * @author dager
 */
public interface AnalyseDataHandler<R, T> {

    void fire();

    void pushInAnalyseQueue(List<T> data);

}
