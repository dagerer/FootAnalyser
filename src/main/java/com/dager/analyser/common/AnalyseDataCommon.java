package com.dager.analyser.common;

import com.dager.analyser.analyser.AnalyseDataAnalyser;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.thread.ThreadTaskService;
import lombok.Data;

/**
 * @author dager
 */
@Data
public class AnalyseDataCommon<T> {

    private ThreadTaskService taskService;

    private AnalyseDataAnalyser<T> analyser;

    private AnalyseContext context;


}
