# EurekaServer 服务注册中心
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
**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/eureka_server