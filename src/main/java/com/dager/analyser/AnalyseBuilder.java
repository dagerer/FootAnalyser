package com.dager.analyser;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
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

    private DefaultDataLoader<R, T> loader;

    private final AnalyseContext<T> context;


    public AnalyseBuilder() {
        context = new AnalyseContext<>();
        channel = new AnalyseDataChannel<>();

    }

    /**
     * 1.创建请求
     * <p>触发对比后，会自动调用请求数据方法，进行数据收集</>
     *
     * @param request 请求体
     * @param service 获取数据方法
     * @return com.dager.analyser.AnalyseBuilder
     */
    public AnalyseBuilder<R, T> setRequest(R request, Function<R, PageDTO<T>> service) {
        loader = new DefaultDataLoader<>();
        loader.setRequest(request);
        loader.setService(service);
        context.setInformation(JSON.toJSONString(request));
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
    public AnalyseBuilder<R, T> setTaskInfo(int batchNum, int threadMaxNum,int maxAvailableNum, String threadName) {
        context.setBatchNum(batchNum);
        context.setMaxAvailableNum(maxAvailableNum);
        ThreadTaskService service = new ThreadTaskServiceImpl(threadMaxNum,threadName);
        context.setTaskService(service);
        AnalyseBlockingQueue<T> queue = new AnalyseBlockingQueue<>();
        AnalyseBlockingQueueTask<T> task = new AnalyseBlockingQueueTask<>(context, queue);
        channel.setBlockingQueue(queue);
        channel.setBlockingQueueTask(task);
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
        context.setAnalyser(analyser);
        return this;
    }

    public FootAnalyse<T> build(){
        final AnalyseDataHandler<T> handler = new AnalyseDataHandlerImpl<>(channel, loader, context);
        return new FootAnalyse<>(handler);
    }

}
