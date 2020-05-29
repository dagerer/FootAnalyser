package com.dager.analyser.analyser;


import com.dager.analyser.context.AnalyseContext;

/**
 * @author dager
 */
public interface AnalyseDataAnalyser<T> {

    Object analyse(AnalyseContext context, T bean);

    void afterAnalyse(T bean, Object result);

}
