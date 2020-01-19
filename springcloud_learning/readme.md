[toc]

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
**以上代码详见：**

# 一. 服务注册与发现
## EurekaServer 服务注册中心
**添加依赖**
```
<groupId>com.limpid</groupId>
<artifactId>eureka_server</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>

<name>eureka_server</name>
<description>给springcloud提供的服务注册中心</description>

<parent>
    <groupId>com.limpid</groupId>
    <artifactId>springcloud_learning</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>

<properties>
    <java.version>1.8</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```
**启动配置**

只需要一个注解`@EnableEurekaServer`，这个注解需要在springboot工程的启动application类上加。
```
/**
 * 通过@EnableEurekaServer启动一个服务注册中心
 * eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成）；
 * 在默认情况下erureka server也是一个eureka client ,必须要指定一个 server；
 */
@EnableEurekaServer
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
**配置文件**

eureka server的配置文件appication.yml：
```
server:
  port: 8761
# eureka 的相关配置
eureka:
  instance:
    hostname: localhost
  client:
    ## 以下两个配置的false表示自己是一个eureka server端
    registerWithEureka: false
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
spring:
  application:
    name: eureka_server
```
**以上代码详见：** 

## EurekaClient 服务提供者
> 当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。EurekaServer 从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。

**添加依赖**
```
<groupId>com.limpid</groupId>
<artifactId>eureka_client</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>

<name>eureka_client</name>
<description>springcloud服务提供者</description>

<parent>
    <groupId>com.limpid</groupId>
    <artifactId>springcloud_learning</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies> 
```
**启动配置**

通过注解@EnableEurekaClient 表明自己是一个eurekaclient.
```
@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

    @Value("${server.port}")
    String port;

    @RequestMapping("/hi")
    public String home(@RequestParam(value = "name", defaultValue = "forezp") String name) {
        return "hi " + name + " ,i am from port:" + port;
    }

}
```
**配置文件**

仅仅@EnableEurekaClient是不够的，还需要在配置文件中注明自己的服务注册中心的地址，application.yml配置文件如下：(==需要指明spring.application.name这个很重要，这在以后的服务与服务之间相互调用一般都是根据这个name==)

```
server:
  port: 8762
spring:
  application:
    name: service_client
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

# 二. 服务消费
