package com.dager.analyser.consumer.analyser;


import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.context.constant.RuleConstants;

/**
 * @author dager
 */
public interface AnalyseDataAnalyser<T> {

    default void handle(T bean, RuleHandler ruleHandler) {
        ruleHandler.pushParam(RuleConstants.RULE_COMPARE_DTO, bean);
        ruleHandler.start();
    }

    void afterHandle(T bean, RuleHandler ruleHandler);

}
