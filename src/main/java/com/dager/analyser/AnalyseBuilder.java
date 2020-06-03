package com.dager.analyser;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.consumer.rule.RuleHandlerImpl;
import com.dager.analyser.context.Configuration;
import com.dager.analyser.context.dto.AnalyseInfoDTO;
import com.dager.analyser.producer.base.PageDTO;
import com.dager.analyser.producer.base.PageRequest;
import com.dager.analyser.channel.AnalyseBlockingQueue;
import com.dager.analyser.channel.AnalyseBlockingQueueTask;
import com.dager.analyser.channel.AnalyseDataChannel;
import com.dager.analyser.context.constant.RuleConstants;
import com.dager.analyser.context.dto.RuleBaseCompareDTO;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.producer.handler.AnalyseDataHandler;
import com.dager.analyser.producer.handler.AnalyseDataHandlerImpl;
import com.dager.analyser.producer.loader.DefaultDataLoader;
import com.dager.analyser.consumer.rule.RuleFactory;
import com.dager.analyser.thread.ThreadTaskService;
import com.dager.analyser.thread.impl.ThreadTaskServiceImpl;


import java.util.function.Function;


/**
 * 系统对账创建工厂类
 *
 * @author dager
 */
class AnalyseBuilder<R extends PageRequest, T> {

    private final AnalyseDataChannel<T> channel;

    private final DefaultDataLoader<R, T> loader;

    private final AnalyseContext<T> context;


    public AnalyseBuilder() {
        loader = new DefaultDataLoader<>();
        context = new AnalyseContext<>();
        channel = new AnalyseDataChannel<>();

    }

    /**
     * 1.创建请求
     * <p>触发对比后，会自动调用请求数据方法，进行数据收集</>
     *
     * @param request 请求体
     * @param service 获取数据方法
     * @return AnalyseBuilder
     */
    public AnalyseBuilder<R, T> setRequest(R request, Function<R, PageDTO<T>> service) {
        loader.setRequest(request);
        loader.setService(service);
        AnalyseInfoDTO commonInfo = new AnalyseInfoDTO();
        commonInfo.setInformation(JSON.toJSONString(request));
        context.setCommonInfo(commonInfo);
        return this;
    }

    /**
     * 设置线程数和分批数
     *
     * @param batchNum
     * @param threadMaxNum
     * @param maxAvailableNum
     * @return
     */
    public AnalyseBuilder<R, T> setConfig(int batchNum, int threadMaxNum,int maxAvailableNum, String threadName) {
        Configuration config = new Configuration();
        config.setBatchNum(batchNum);
        config.setThreadNum(threadMaxNum);
        config.setThreadNum(maxAvailableNum);
        context.setConfig(config);
        ThreadTaskService service = new ThreadTaskServiceImpl(threadMaxNum,threadName);
        context.setTaskService(service);
        AnalyseBlockingQueue<T> queue = new AnalyseBlockingQueue<>();
        AnalyseBlockingQueueTask<T> task = new AnalyseBlockingQueueTask<>(context, queue);
        channel.setBlockingQueue(queue);
        channel.setBlockingQueueTask(task);
        return this;
    }

    public AnalyseBuilder<R, T> setRule(Object... rules) {
        RuleHandler ruleHandler = new RuleHandlerImpl();
        ruleHandler.pushRules(rules);
        context.setRuleHandler(ruleHandler);
        return this;
    }

    public <M extends AnalyseDataAnalyser<T>> AnalyseBuilder<R, T> setAfterOperation(M analyser) {
        context.getConfig().setOverride(true);
        context.setAnalyser(analyser);
        return this;
    }

    public FootAnalyse<T> build(){
        final AnalyseDataHandler<T> handler = new AnalyseDataHandlerImpl<>(channel, loader, context);
        return new FootAnalyse<>(handler);
    }

}
