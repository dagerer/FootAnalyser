package com.dager.analyser.consumer.analyser;


import com.dager.analyser.context.AnalyseContext;

/**
 * @author dager
 */
public interface AnalyseDataAnalyser<T> {

    Object analyse(AnalyseContext<T> context, T bean);

    void afterAnalyse(T bean, Object result);

}
