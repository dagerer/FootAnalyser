package com.dager.analyser;

import com.dager.analyser.producer.base.PageRequest;
import com.dager.analyser.producer.handler.AnalyseDataHandler;

/**
 * FootAnalyse {@link AnalyseBuilder}
 * 具体使用模板:
 * <p>
 * <blockquote><pre>
 *     QueryRequest request = new QueryRequest();
 *     request.setSome();
 *     FootAnalyse&lt;Object> foot = FootAnalyse.&lt;Object>builder().
 *            .setRequest(request, dataSourceService::queryDataByPage)
 *            .setConfig(...)
 *            .setRule(...)
 *            .setAfterOperation(...)
 *            .build();
 *     foot.analyse();
 * </pre></blockquote>
 * </p>
 *
 * @author dager
 * @author G. Seinfeld
 */
public class FootAnalyse<T> {

    private final AnalyseDataHandler<T> handler;

    public FootAnalyse(AnalyseDataHandler<T> handler) {
        this.handler = handler;
    }

    public void analyse() {
        handler.handle();
    }

    /**
     * @param <R> 分页信息类型（每次处理的条数以及当前页数）
     * @param <T> 数据类型
     * @return
     */
    public static <R extends PageRequest, T> AnalyseBuilder<R, T> builder() {
        return new AnalyseBuilder<>();
    }

}
