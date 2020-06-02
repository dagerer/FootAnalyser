package com.dager.analyser.consumer.rule;

import com.dager.analyser.context.RuleContext;
import com.dager.analyser.context.constant.RuleConstants;
import com.dager.analyser.context.dto.RuleBaseCompareDTO;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.RulesEngine;

@Slf4j
public class RuleHandlerImpl implements RuleHandler {

    private final RuleFactory factory;

    public RuleHandlerImpl() {
        this.factory = createCommonRuleFactory();
    }

    @Override
    public RuleFactory createCommonRuleFactory() {
        return RuleFactory.builder()
                .createFact()
                .initContext()
                .createRules()
                .createEngine()
                .selectSkipOnFirstNonTriggeredRuleMode()
                .build();
    }

    @Override
    public void pushRules(Object... rule) {
        Preconditions.checkArgument(rule != null && rule.length > 0);
        for (Object o : rule) {
            factory.getRules().register(o);
        }
    }

    @Override
    public void pushParam(String key, Object value) {
        Preconditions.checkArgument(key != null);
        factory.getFacts().put(key, value);
    }

    @Override
    public RuleContext getContext() {
        Facts facts = factory.getFacts();
        if (facts.asMap().containsKey("context")) {
            Object context = facts.get("context");
            if (context instanceof RuleContext) {
                return (RuleContext) context;
            }
        }
        return null;
    }

    @Override
    public void start() {
        RulesEngine engine = factory.getEngine();
        engine.fire(factory.getRules(), factory.getFacts());
    }
}
