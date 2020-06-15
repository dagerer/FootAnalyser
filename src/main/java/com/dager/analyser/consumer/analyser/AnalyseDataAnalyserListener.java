package com.dager.analyser.consumer.analyser;


import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.context.AnalyseContext;

/**
 * @author v_zmzmwang
 */
public interface AnalyseDataAnalyserListener<T> {

    void beforeHandle(T bean, AnalyseContext<T> context, RuleHandler ruleHandler);

    void afterHandle(T bean, RuleHandler ruleHandler);
}

