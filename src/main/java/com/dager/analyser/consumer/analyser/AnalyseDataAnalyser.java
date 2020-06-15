package com.dager.analyser.consumer.analyser;


import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.context.constant.RuleConstants;

/**
 * @author dager
 */
public class AnalyseDataAnalyser<T> {

    AnalyseDataAnalyserListener<T> listener;

    public AnalyseDataAnalyser(){
    }

    public final void registerListener(AnalyseDataAnalyserListener<T> listener) {
        this.listener = listener;
    }

    public void handle(T bean, AnalyseContext<T> context, RuleHandler ruleHandler) {
        beforeHandle(bean, context, ruleHandler);
        ruleHandler.pushParam(RuleConstants.RULE_COMPARE_DTO, bean);
        ruleHandler.start();
        afterHandle(bean, ruleHandler);
    }

    private void beforeHandle(T bean, AnalyseContext<T> context, RuleHandler ruleHandler) {
        this.listener.beforeHandle(bean, context, ruleHandler);
    }

    private void afterHandle(T bean, RuleHandler ruleHandler) {
        this.listener.afterHandle(bean, ruleHandler);
    }
}

