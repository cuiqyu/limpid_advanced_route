# 一. 服务注册与发现
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
**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/eureka_client
