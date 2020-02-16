# 八. 消息总线（Spring Cloud Bus）

> Spring cloud bus通过轻量消息代理连接各个分布的节点。这会用在广播状态的变化（例如配置变化）或者其他的消息指令。Spring bus的一个核心思想是通过分布式的启动器对spring boot应用进行扩展，也可以用来建立一个多个应用之间的通信频道。目前唯一实现的方式是用AMQP消息代理作为通道，同样特性的设置（有些取决于通道的设置）在更多通道的文档中。

> 大家可以将它理解为管理和传播所有分布式项目中的消息既可，其实本质是利用了MQ的广播机制在分布式的系统中传播消息，目前常用的有Kafka和RabbitMQ。利用bus的机制可以做很多的事情，其中配置中心客户端刷新就是典型的应用场景之一，我们用一张图来描述bus在配置中心使用的机制。

![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/48C36BE524FA4318B33D58C2BE535CC0/18609)

根据此图我们可以看出利用Spring Cloud Bus做配置更新的步骤:

1. 提交代码触发post给客户端A发送bus/refresh
2. 客户端A接收到请求从Server端更新配置并且发送给Spring Cloud Bus
3. Spring Cloud bus接到消息并通知给其它客户端
4. 其它客户端接收到通知，请求Server端获取最新配置
5. 全部客户端均获取到最新的配置

**项目实例**

**1. 改造config-client项目**

新增`spring-cloud-starter-bus-amqp`和`spring-boot-starter-actuator`项目依赖
```
<groupId>com.limpid</groupId>
<artifactId>config_client</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>

<name>config_client</name>
<description>从spring cloud config配置中心中读取配置</description>

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
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bus-amqp</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

**修改配置文件：**

添加RabbitMq的地址、端口，用户名、密码。并需要加上spring.cloud.bus的三个配置：

application.yml
```
spring:
  # 配置rabbitmq
  rabbitmq:
    host: ******
    port: 5672
    # username: ***
    # password: ***
  cloud:
    # 配置spring_cloud_bus
    bus:
      enabled: true
      trace:
        enabled: true
management:
  endpoints:
    web:
      exposure:
        include: bus-refresh
```

**启动配置：**

添加@RefreshScope注解。
```
@SpringBootApplication
@RestController
@EnableDiscoveryClient
@EnableEurekaClient
@RefreshScope
/**
 * @Author cuiqiongyu
 * @Description 通过 http://localhost:8881/actuator/bus-refresh 来刷新config_client重新读取配置的消息
 * @Date 11:29 2020-02-16
 * @Param
 * @return
 **/
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    @Value("${foo}")
    String foo;

    @RequestMapping(value = "/hi")
    public String hi(){
        return foo;
    }

}
```

**开始测试：**

启动`eureka_server`,`eureka_server`,`eureka_client:8881`,`eureka_client:8882`

分别访问：`http://localhost:8881/hi`和`http://localhost:8882/hi`
> foo version 5

修改远程配置文件，将`5`修改成`6`，再次分别访问`http://localhost:8881/hi`和`http://localhost:8882/hi`，发现结果没有变化，因为此时`config_client`并不会重新读取远程的配置文件。

通过post请求：`http://localhost:8881/actuator/bus-refresh`或者`http://localhost:8882/actuator/bus-refresh`中的任意一个。此时会发现 `config_client`会收到从spring_clount_bus发出来的消息重新读取远程配置文件。分别访问：`http://localhost:8881/hi`和`http://localhost:8882/hi`
> foo version 6

_另外需要注意的是：_ /actuator/bus-refresh接口可以指定服务，即使用"destination"参数，比如 “/actuator/bus-refresh?destination=customers:**” 即刷新服务名为customers的所有服务。

**分析：**
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/1B9A23EA67E94FAC9910E51A99B1D453/18611)

当git文件更改的时候，通过pc端用post 向端口为8882的config-client发送请求/bus/refresh／；此时8882端口会发送一个消息，由消息总线向其他服务传递，从而使整个微服务集群都达到更新配置文件。

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/config_client