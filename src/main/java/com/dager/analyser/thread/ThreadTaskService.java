package com.dager.analyser.thread;


import com.dager.analyser.context.dto.ThreadTaskDTO;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多线程服务
 */
public interface ThreadTaskService {

    /**
     * 获取公共线程池
     * @return ThreadPoolExecutor
     */
    ThreadPoolExecutor getThreadPool();

    /**
     * 自动分批处理数据(根据线程数)
     *
     * @param service 自定义操作服务
     * @param threadTaskDTO 多线程操作DTO
     */
    void batchHandle(BatchExecuteService service, ThreadTaskDTO threadTaskDTO);


    interface BatchExecuteService{
        /**
         * 自定义执行逻辑
         * @param threadTaskDTO 执行参数
         * @return 返回值
         */
        Object execute(ThreadTaskDTO threadTaskDTO);

    }
}
