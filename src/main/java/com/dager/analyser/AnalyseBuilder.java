package com.dager.analyser;

import com.alibaba.fastjson.JSON;
import com.dager.analyser.channel.AnalyseBlockingQueue;
import com.dager.analyser.channel.AnalyseBlockingQueueTask;
import com.dager.analyser.channel.AnalyseDataChannel;
import com.dager.analyser.consumer.analyser.AnalyseDataAnalyser;
import com.dager.analyser.consumer.analyser.AnalyseDataAnalyserListener;
import com.dager.analyser.consumer.rule.RuleHandler;
import com.dager.analyser.consumer.rule.RuleHandlerImpl;
import com.dager.analyser.context.AnalyseContext;
import com.dager.analyser.context.Configuration;
import com.dager.analyser.producer.base.PageDTO;
import com.dager.analyser.producer.base.PageRequest;
import com.dager.analyser.producer.handler.AnalyseDataHandler;
import com.dager.analyser.producer.handler.AnalyseDataHandlerImpl;
import com.dager.analyser.producer.loader.DefaultDataLoader;
import com.dager.analyser.thread.ThreadTaskService;
import com.dager.analyser.thread.impl.ThreadTaskServiceImpl;

import java.util.List;
import java.util.function.Function;


/**
 * 系统对账创建工厂类
 *
 * @author dager
 */
public class AnalyseBuilder<R extends PageRequest, T> {

    private final AnalyseDataChannel<T> channel;

    private final DefaultDataLoader<R, T> loader;

    private final AnalyseContext<T> context;

    private final Configuration config;


    public AnalyseBuilder() {
        loader = new DefaultDataLoader<>();
        context = new AnalyseContext<>();
        config = new Configuration();
        context.setConfig(config);

        channel = new AnalyseDataChannel<>();
        channel.setBlockingQueue(new AnalyseBlockingQueue<>());
        channel.setBlockingQueueTask(new AnalyseBlockingQueueTask<>(context, new AnalyseBlockingQueue<>()));
    }

    /**
     * 1.创建请求
     * <p>触发对比后，会自动调用请求数据方法，进行数据收集</>
     *
     * @param request 请求体
     */
    public AnalyseBuilder<R, T> request(R request) {
        loader.setRequest(request);
        context.setInformation(JSON.toJSONString(request));
        return this;
    }

    /**
     * @param service 获取数据方法
     */
    public AnalyseBuilder<R, T> service(Function<R, PageDTO<T>> service) {
        loader.setService(service);
        return this;
    }


    /**
     * 设置线程数和分批数
     *
     * @param batchNum
     * @param threadMaxNum
     * @param maxAvailableNum
     */
    public AnalyseBuilder<R, T> taskInfo(int batchNum, int threadMaxNum, int maxAvailableNum, String threadName) {
        config.setBatchNum(batchNum);
        config.setMaxAvailableNum(maxAvailableNum);

        ThreadTaskService service = new ThreadTaskServiceImpl(threadMaxNum, threadName);
        context.setTaskService(service);

        return this;
    }

    public AnalyseBuilder<R, T> rules(Object... rules) {
        RuleHandler ruleHandler = new RuleHandlerImpl();
        ruleHandler.pushRules(rules);
        context.setRuleHandler(ruleHandler);
        return this;
    }

    public AnalyseBuilder<R, T> rules(List<Object> rules) {
        RuleHandler ruleHandler = new RuleHandlerImpl();
        for (Object rule : rules) {
            ruleHandler.pushRules(rule);
        }
        context.setRuleHandler(ruleHandler);
        return this;
    }

    /**
     * 默认对账模式，走规则引擎，可自定义监听器
     *
     * @param listener 监听器
     * @param <M> 监听器类型
     * @return
     */
    public <M extends AnalyseDataAnalyserListener<T>> AnalyseBuilder<R, T> listener(M listener) {
        AnalyseDataAnalyser<T> dataAnalyser = new AnalyseDataAnalyser<>();
        dataAnalyser.registerListener(listener);
        context.setAnalyser(dataAnalyser);
        return this;
    }

    /**
     * 自定义处理数据模式（无法注册监听器）
     *
     * @param analyser 自定义分析类
     * @param <M> 分析类类型
     * @return
     */
    public <M extends AnalyseDataAnalyser<T>> AnalyseBuilder<R, T> analyser(M analyser) {
        context.setAnalyser(analyser);
        return this;
    }


    public FootAnalyse<R, T> build() {
        final AnalyseDataHandler<T> handler = new AnalyseDataHandlerImpl<>(channel, loader, context);
        return new FootAnalyse<>(handler);
    }

}
