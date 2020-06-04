# FootAnalyser

## 工作流程
### 流程图
### 流程描述
1. 设置数据请求体，和对应请求数据方法
2. 设置任务配置
3. 设置数据分析规则
4. 开始分析

## 内部实现细节
### 生产者
1. 通过reader读取数据
2. 将数据推入阻塞队列

### 消费者
1. 从阻塞队列中取出数据
2. 使用规则引擎对数据进行处理
3. 将处理完的数据进行后处理。

### 通道
1. 通过阻塞队列建立消费者与生产者的连接

### 线程
1. 内部实现根据配置线程数进行分批操作数据

## 组件
- EasyRule
- lombok
- fastjson
- hutool
- guava
- junit