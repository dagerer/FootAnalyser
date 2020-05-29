package com.dager.analyser.context;

import com.dager.analyser.rule.RuleFactory;
import lombok.Data;

/**
 * @author dager
 */
@Data
public class AnalyseContext {

    private String information;
    
    private int batchNum;

    private int maxAvailableNum;

    private RuleFactory ruleFactory;

    private String recordDate;
}
