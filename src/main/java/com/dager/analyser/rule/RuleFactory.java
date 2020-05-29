package com.dager.analyser.rule;


import com.dager.analyser.common.constant.RuleConstants;
import com.dager.analyser.common.dto.RuleBaseCompareDTO;
import com.dager.analyser.context.RuleContext;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;


/**
 * 规则引擎工厂类
 *
 * @author dager
 */
@Getter
@NoArgsConstructor
public class RuleFactory {

    private Facts facts;

    private Rules rules;

    private RulesEngine engine;

    private RuleFactory(Facts facts, Rules rules, RulesEngine engine) {
        this.facts = facts;
        this.rules = rules;
        this.engine = engine;
    }

    /**
     * 开始
     */
    public void start() {
        Preconditions.checkArgument(engine != null && facts != null && rules != null && !rules.isEmpty());
        this.engine.fire(rules, facts);
    }

    /**
     * 批量加入规则
     * @param rule 规则
     */
    public void pushRules(Object... rule) {
        Preconditions.checkArgument(rule != null && rule.length > 0);
        for (Object o : rule) {
            rules.register(o);
        }
    }

    /**
     * 清除工厂所有规则
     */
    public void clearRules() {
        if (rules != null && !rules.isEmpty()) {
            this.rules.clear();
        }
    }

    public void pushFact(String key, Object fact) {
        Preconditions.checkArgument(key != null);
        facts.put(key,fact);
    }

    public Object pollFact(String key) {
        Preconditions.checkArgument(key != null);
        return facts.get(key);
    }

    public RuleContext getContext() {
        if(facts.asMap().containsKey("context")){
            Object context = facts.get("context");
            if(context instanceof RuleContext){
                return (RuleContext) context;
            }
        }
        return null;
    }

    public <T extends RuleBaseCompareDTO> T getRuleDTO() {
        if (getFacts().asMap().containsKey(RuleConstants.RULE_COMPARE_DTO)) {
            return getFacts().get(RuleConstants.RULE_COMPARE_DTO);
        }
        return null;
    }

    public static RuleFactory createCommonRuleFactory() {
        return RuleFactory.builder()
                .createFact()
                .initContext()
                .createRules()
                .createEngine()
                .selectSkipOnFirstNonTriggeredRuleMode()
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Facts facts;

        private Rules rules;

        private RulesEngine engine;

        private RulesEngineParameters parameters;

        private RuleContext ruleContext;

        private Builder() {
        }

        /**
         * 创建参数map
         */
        public Builder createFact() {
            if (this.facts == null) {
                this.facts = new Facts();
            }
            return this;
        }

        /**
         * 创建规则集合
         */
        public Builder createRules() {
            if (this.rules == null) {
                this.rules = new Rules();
            }
            return this;
        }

        /**
         * 添加参数
         */
        public Builder pushFact(String key, Object fact) {
            Preconditions.checkArgument(facts != null);
            this.facts.put(key, fact);
            return this;
        }

        /**
         * 初始化规则上下文
         */
        public Builder initContext() {
            Preconditions.checkArgument(facts != null);
            if (ruleContext == null) {
                ruleContext = new RuleContext();
            }
            this.facts.put("context", ruleContext);
            return this;
        }

        /**
         * 添加规则
         */
        public Builder pushRule(Object rule) {
            Preconditions.checkArgument(rules != null);
            this.rules.register(rule);
            return this;
        }

        /**
         * 创建规则引擎
         */
        public Builder createEngine() {
            if (this.engine == null) {
                this.parameters = new RulesEngineParameters();
                this.engine = new DefaultRulesEngine(parameters);
            }
            return this;
        }

        /**
         * 切换规则模式1
         * 如果不通过，则直接跳过后面规则
         */
        public Builder selectSkipOnFirstNonTriggeredRuleMode() {
            if (this.parameters != null) {
                this.parameters.skipOnFirstNonTriggeredRule(true);
            }
            return this;
        }

        /**
         * 切换规则模式2
         */
        public Builder selectSkipOnFirstFailedRuleMode() {
            if (this.parameters != null) {
                this.parameters.skipOnFirstFailedRule(true);
            }
            return this;
        }

        /**
         * 切换规则模式3
         */
        public Builder selectSkipOnFirstAppliedRuleMode() {
            if (this.parameters != null) {
                this.parameters.skipOnFirstAppliedRule(true);
            }
            return this;
        }

        public RuleFactory build() {
            if (this.engine == null) {
                this.engine = new DefaultRulesEngine();
            }
            return new RuleFactory(facts, rules, this.engine);
        }
    }


}
