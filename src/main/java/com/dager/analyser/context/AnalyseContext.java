package com.dager.analyser.context;

import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.thread.ThreadTaskService;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dager
 */
@Data
public class AnalyseContext<T> {

    private ThreadTaskService taskService;

    private AnalyseDataAnalyser<T> analyser;

    private RuleHandler ruleHandler;

    private RequestMetadata metadata = new RequestMetadata();

    private Configuration config;

    public void setInformation(String information) {
        metadata.setInformation(information);
    }

    @Data
    public static class RequestMetadata implements Serializable {

        /**
         * 请求信息
         */
        private String information;
        /**
         * 记录日期 yyyy-MM-dd
         */
        private String recordDate;
    }
}
