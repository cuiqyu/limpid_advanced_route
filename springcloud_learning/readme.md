> # 前言：
> **springcloud微服务架构中，包括几个基础的服务组件：**
> 
> **`1.服务注册与发现`**
>
> **`2.服务消费`**
> 
> **`3.断路由`**
> 
> **`4.路由网关`**
> 
> **`5.分布式配置中心`**
> 
> **`6.消息总线`**
> 
> **`7.服务链路追踪`**
> 
> **`8.断路器监控`**

# springcloud_learning父类项目
> 创建springcloud_learning父类项目，用来管理项目系列子项目的版本号，结构搭建如下：

**pom.xml配置如下**：注意此处使用的springcloud的版本号为`Finchley.RELEASE`，springboot的版本号为`2.0.3.RELEASE`，如需更换版本请注意springcloud和springboot的版本对应关系。
```
<groupId>com.limpid</groupId>
<artifactId>springcloud_learning</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>pom</packaging>

<name>springcloud_learning</name>
<description>springcloud的相关知识入门学习</description>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.3.RELEASE</version>
</parent>

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <spring-cloud.version>Finchley.RELEASE</spring-cloud.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```
**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning

# 一. 服务注册与发现（Eureka）
## [EurekaClient 服务提供者](https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/eureka_client)
## [EurekaServer 服务注册中心](https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/eureka_server)

# 二. [服务消费（rest+ribbon）](https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_ribbon)
# 三. [服务消费（Feign）](https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_feign)
# 四. [断路由（Hystrix）](https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/md/Hystrix.md)
# 五. [路由网关（zuul）](https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_zuul)