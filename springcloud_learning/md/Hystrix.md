# 四. 断路由（Hystrix）
**前言：** 在微服务架构中，可以按照业务来将项目拆分成一个个的服务，服务与服务之间可以相互调用（RPC）。而在Spring Cloud可以用RestTemplate+Ribbon和Feign来调用。
但是为了保证其高可用，单个服务通常会集群部署。由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。

针对以上问题：从而出现了断路器这一概念

> **断路器：** 

Netflix开源了Hystrix组件，实现了断路器模式，SpringCloud对这一组件进行了整合。 在微服务架构中，一个请求需要调用多个服务是非常常见的，如下图：
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/86EC18F48BC548A68B364E90FF5131A7/18603)
较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开。
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/B041601F548E470EB7FE007401D1D5E0/18602)

**改造之前项目**

**1. 改造service_ribbon项目：**

pom.xml中添加hystrix依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

在项目启动类`ServiceRibbonApplication`中添加`@EnableHystrix`表示开启Hystrix支持
```
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableHystrix
public class ServiceRibbonApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceRibbonApplication.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
```

改造HelloService服务，在接口方法上添加`@HystrixCommand(fallbackMethod = "hiError")`注解声明该方法的熔断功能，并设置注解属性`fallbackMethod = "hiError"`为一个`hiError`的熔断方法。
```
@Service
public class HelloService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "hiError")
    public String hiService(String name) {
        return restTemplate.getForObject("http://service-client/hi?name="+name, String.class);
    }

    public String hiError(String name) {
        return "hi,"+name+",sorry,error!";
    }

}
```

**开始测试：** 启动`service-client`工程和`service-ribbon`工程，此时访问`service-ribbon`服务可以正常访问`hi zhangsan ,i am from port:8763`，关闭`service-client`服务后，继续访问`service-ribbon`会提示：`hi,zhangsan,sorry,error!`

**综上结果：** 这就说明当`service-client`工程不可用的时候，`service-ribbon`调用`service-client`的API接口时，会执行快速失败，直接返回一组字符串，而不是等待响应超时，这很好的控制了容器的线程阻塞。

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_ribbon

**2. 改造service-feign项目：**

Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开。需要在配置文件中配置打开它，在配置文件加以下代码：
> feign.hystrix.enabled=true

对service-feign项目进行改造，只需要将service中的`@FeignClient`注解中添加fallback属性即可，代码如下：
```
@FeignClient(value = "service-client", fallback = SchedualHelloHystrixService.class)
public interface SchedualHelloService {

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);

}
```

其中SchedualHelloHystrixService.class需要实现SchedualHelloService接口，并注入到ioc容器中。
```
@Component
public class SchedualHelloHystrixService implements SchedualHelloService {

    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry, " + name;
    }

}
```

**开始测试：** 启动`service-client`工程和`service-feign`工程，此时访问`service-feign`服务可以正常访问`hi zhangsan ,i am from port:8763`，关闭`service-client`服务后，继续访问`service-feign`会提示：`sorry, zhangsan`

**综上结果：** 熔断功能实现。

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_feign