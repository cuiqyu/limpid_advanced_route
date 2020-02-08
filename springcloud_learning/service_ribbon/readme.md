# 服务消费（rest+ribbon）
新建项目`service_ribbon`，作为`spring_cloud`项目下的子模块，代码如下：

**添加依赖**
```
    <groupId>com.limpid</groupId>
    <artifactId>service_ribbon</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>service_ribbon</name>
    <description>使用ribbon来消费注册中心的服务</description>

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
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </dependency>
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

```
/**
 * 通过@EnableDiscoveryClient向服务中心注册
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
public class ServiceRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRibbonApplication.class, args);
    }

    /**
     * 使用@LoadBalance表示声明该restTemplate的bean使用了负载均衡
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
```

**配置文件**

```
server:
  port: 8764

spring:
  application:
    name: service-ribbon

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

**着重注意**：此处的spring.application.name中不能使用下划线，不然ribbon在实现负载均衡时将不能找到服务

**ribbon的负载均衡使用**

```
@Service
public class HelloService {

    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * 此处url中使用的是注册中心注册的服务名称，不区分大小写
     */
    public String hiService(String name) {
        return restTemplate.getForObject("http://service-client/hi?name="+name, String.class);
    }

}
```

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_ribbon