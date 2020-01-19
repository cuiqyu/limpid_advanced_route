> # 前言：
> **springcloud微服务架构中，包括几个基础的服务组件：**
>
> **`1.服务注册与发现`**
>
> **`2.服务消费`**
>
> **`3.负载均衡`**
>
> **`4.断路由`**
> 
> **`5.智能路由`**
> 
> **`6.配置管理`**

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

# 一. 服务注册与发现
详见详情：https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/eureka_server