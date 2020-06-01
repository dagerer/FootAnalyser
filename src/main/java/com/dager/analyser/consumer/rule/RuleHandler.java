package com.dager.analyser.consumer.rule;

import com.dager.analyser.context.RuleContext;

public interface RuleHandler {

    RuleFactory createCommonRuleFactory();

    void pushRules(Object... rule);

    void pushParam(String key, Object value);

    RuleContext getContext();

    void start();
}
