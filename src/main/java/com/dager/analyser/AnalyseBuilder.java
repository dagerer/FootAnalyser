package com.dager.analyser;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.analyser.AnalyseDataAnalyser;
import com.dager.analyser.base.PageDTO;
import com.dager.analyser.base.PageRequest;
import com.dager.analyser.channel.AnalyseBlockingQueue;
import com.dager.analyser.channel.AnalyseBlockingQueueTask;
import com.dager.analyser.channel.AnalyseDataChannel;
import com.dager.analyser.common.AnalyseDataCommon;
import com.dager.analyser.common.constant.RuleConstants;
import com.dager.analyser.common.dto.RuleBaseCompareDTO;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.handler.AnalyseDataHandler;
import com.dager.analyser.handler.AnalyseDataHandlerImpl;
import com.dager.analyser.loader.DefaultDataLoader;
import com.dager.analyser.rule.RuleFactory;
import com.dager.analyser.thread.ThreadTaskService;
import com.dager.analyser.thread.impl.ThreadTaskServiceImpl;


import java.util.function.Function;


/**
 * 系统对账创建工厂类
 *
 * @author dager
 */
public class AnalyseBuilder<R extends PageRequest, T> {

    private final AnalyseDataHandler<T> handler;

    private final AnalyseDataChannel<T> channel;

    private final DefaultDataLoader<R, T> reader;

    private final AnalyseContext context;

    private final AnalyseDataCommon<T> common;

    public AnalyseBuilder() {
        context = new AnalyseContext();
        common = new AnalyseDataCommon<>();
        reader = new DefaultDataLoader<>();
        channel = new AnalyseDataChannel<>();
        handler = new AnalyseDataHandlerImpl<>(this.channel, reader, common);
    }

    /**
     * 1.创建请求
     * <p>触发对比后，会自动调用请求数据方法，进行数据收集</>
     *
     * @param request 请求体
     * @param service 获取数据方法
     * @param <R> 分页参数
     * @param <T> 数据实体类型
     * @return com.dager.analyser.AnalyseBuilder
     */
    public static <R extends PageRequest, T> AnalyseBuilder<R, T> setRequest(R request, Function<R, PageDTO<T>> service) {
        AnalyseBuilder<R, T> analyseBuilder = new AnalyseBuilder<>();
        analyseBuilder.reader.setRequest(request);
        analyseBuilder.reader.setService(service);
        analyseBuilder.context.setInformation(JSON.toJSONString(request));
        return analyseBuilder;
    }

    /**
     * 设置线程数和分批数
     *
     * @param batchNum
     * @param threadMaxNum
     * @param maxAvailableNum
     * @return
     */
    public AnalyseBuilder<R, T> setTaskInfo(int batchNum, int threadMaxNum,int maxAvailableNum, String threadName) {
        this.context.setBatchNum(batchNum);
        this.context.setMaxAvailableNum(maxAvailableNum);
        ThreadTaskService service = new ThreadTaskServiceImpl(threadMaxNum,threadName);
        this.common.setTaskService(service);
        this.common.setContext(this.context);
        AnalyseBlockingQueue<T> queue = new AnalyseBlockingQueue<>();
        AnalyseBlockingQueueTask<T> task = new AnalyseBlockingQueueTask<>(common, queue);
        this.channel.setBlockingQueue(queue);
        this.channel.setBlockingQueueTask(task);
        return this;
    }

    public <V extends RuleBaseCompareDTO> AnalyseBuilder<R, T> setRuleAndParam(V compareDTO, Object... rules) {
        RuleFactory ruleFactory = RuleFactory.createCommonRuleFactory();
        ruleFactory.pushFact(RuleConstants.RULE_COMPARE_DTO, compareDTO);
        ruleFactory.pushRules(rules);
        context.setRuleFactory(ruleFactory);
        return this;
    }

    public <M extends AnalyseDataAnalyser<T>> AnalyseBuilder<R, T> setAnalyseFunction(M analyser) {
        this.common.setAnalyser(analyser);
        return this;
    }

    public FootAnalyse<R, T> build(){
        return new FootAnalyse<>(handler);
    }

}
