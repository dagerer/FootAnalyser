package com.dager.analyser.context;

import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
import com.dager.analyser.consumer.rule.RuleFactory;
import com.dager.analyser.thread.ThreadTaskService;
import lombok.Data;

/**
 * @author dager
 */
@Data
public class AnalyseContext<T> {

    private ThreadTaskService taskService;

    private AnalyseDataAnalyser<T> analyser;
    private String information;
    
    private int batchNum;

    private int maxAvailableNum;

    private RuleFactory ruleFactory;

    private String recordDate;
}
