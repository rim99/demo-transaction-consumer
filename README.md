# README

这个小Demo是一个简单的微服务。其业务背景源自实践，并进行了脱敏和简化。本质上是对消息队列里的消息进行消费：根据消息里的某些字段，计算一定时间段内的数值累加和计数，并将消息持久化在数据库里。

# 如何运行

执行这个Bash脚本就可以了

```
./run.sh
```

## 目录结构

```
├── LICENSE
├── README.md
├── env                       开发环境的配置
├── docker-compose.yml        开发环境
├── run.sh                    启动开发环境
├── documentation             项目文档                       
├── transaction-consumer      主项目
│   └── src/main/java/org/example/transaction/consumer
│       ├── Main.java         主程序入口
│       ├── adapter           与第三方服务交互
│       │   └── ... 
│       ├── config            依赖注入的配置
│       │   └── ...
│       ├── entity            与第三方服务交互使用的数据类
│       │   └── ...
│       ├── package-info.java
│       ├── port              定义adapter与service交互的接口与数据类
│       │   └── ...
│       └── service           业务逻辑
│           └── ...
└── transaction-producer      消息生成工具，开发调试、性能测试都可以用

```


## 组件选型

微服务框架选择了Oracle的云原生框架Helidon-SE。这个框架是一个轻量级的HTTP微服务框架，可以用GraalVM编译成Native可执行文件，能够做到快速启动，快速响应。但是根据一些评测，运行速度比充分热身后的HotSpot应用要慢一些。

Json的序列化库，一开始选择的是JVM圈里最快的DSL-Json。这个工具可以在编译期生成一些辅助类，在很多评测对比中表现很好。反而以前号称很快的Jackson反而跟这个差了很远。

对消息的聚合选择使用Redis来做。因为聚合都是操作最近的数据；过期的数据不再变更，可以在持久化到关系型数据库里。活跃的数据放在Redis里，就是因为快。原子操作丰富，事务也是非交互式的。

历史数据持久化使用了Postgres。因为这个数据库的数据存储方式很适合追加写入。

## 性能测试

本来尝试用GraalVM编译native镜像，但是因为和Jedis、Lettuce有兼容性问题，暂时放弃。

选择了Hotspot和OpenJ9两个JRE环境，分别简单的测试了一下运行时的表现。

[Test Result](https://gitee.com/rim99/demo-transaction-consumer/blob/master/documentation/test-outcome/message-consume-test/20201029-guice-dsljson-parallel.md)
