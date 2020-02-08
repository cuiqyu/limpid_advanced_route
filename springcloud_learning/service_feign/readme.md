# 服务消费（Feign）
> **Fegin的简介：** Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
> 
> 简而言之：
> 1. Feign 采用的是基于接口的注解
> 2. Feign 整合了ribbon，具有负载均衡的能力
> 3. 整合了Hystrix，具有熔断的能力

依旧在父项目`springcloud_learning`下新建子模块`service_feign`，pom文件引入Feign的起步依赖spring-cloud-starter-feign、Eureka的起步依赖spring-cloud-starter-netflix-eureka-client、Web的起步依赖spring-boot-starter-web，如下：

**添加依赖**
```
    <groupId>com.limpid</groupId>
    <artifactId>service_feign</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>service_feign</name>

    <description>使用feign去消费服务</description>
    <packaging>jar</packaging>

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
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
```

**启动配置**

```
/**
 * 添加@EnableFeignClients注解开启Feign的功能
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableFeignClients
public class ServiceFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceFeignApplication.class, args);
    }

}
```

**配置文件**

```
server:
  port: 8765

spring:
  application:
    name: service-feign

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**新增service**
定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。比如在代码中调用了service-client服务的“/hi”接口，代码如下：
```
/**
 * @auther cuiqiongyu
 * @create 2020-02-08 16:30
 */
@FeignClient(value = "service-client")
public interface SchedualHelloService {

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);

}
```

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_feign