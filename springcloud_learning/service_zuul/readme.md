# 五. 路由网关（zuul）
> 在springCloud的微服务系统架构中，在Spring Cloud微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（zuul、Ngnix），再到达服务网关（zuul集群），然后再到具体的服。，服务统一注册到高可用的服务注册中心集群，服务的所有的配置文件由配置服务管理，配置服务的配置文件放在git仓库，方便开发人员随时改配置。

微服务架构系统如下图：
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/WEBRESOURCEff2a0345c0abceaa7ad5d82d0e6fe38c/18605)

**Zuul简介**
> Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。
> 
> zuul具有以下这些功能：
> 
> 1. Authentication
> 
> 2. Insights
> 
> 3. Stress Testing
> 
> 4. Canary Testing
> 
> 5. Dynamic Routing
> 
> 6. Service Migration
> 
> 7. Load Shedding
> 
> 8. Security
> 
> 9. Static Response handling
> 
> 10. Active/Active traffic management

**新建项目**

在`springcloud_learning`下创建一个子模块`service_zuul`, 并且引入`eureka-client`、`web`，`zuul`依赖，如下：

**添加依赖**
```
<groupId>com.limpid</groupId>
    <artifactId>service_zuul</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>service_zuul</name>
    <description>使用zuul实现服务路由功能和服务过滤功能</description>

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
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
    </dependencies>
```

**启动配置**
添加@EnableZuulProxy开启zuul支持
```
@EnableEurekaClient
@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication
public class ServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceZuulApplication.class, args);
    }

}
```

**配置文件**
```
server:
  port: 8769

spring:
  application:
    name: service-zuul

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

# 配置zuul路由选择功能
zuul:
  routes:
    api-a:
      path: /api-a/**
      serviceId: service-ribbon
    api-b:
      path: /api-b/**
      serviceId: service-feign
```

**功能测试**

1.输入：http://localhost:8769/api-a/hi?name=zhangsan

> hi zhangsan ,i am from port:8763

2.输入：http://localhost:8769/api-b/hi?name=zhangsan

> hi zhangsan ,i am from port:8763

结果与我们预期的效果一致，api-a被路由到了service-ribbon服务，api—b被路由熬了service-feign服务。

**服务过滤功能**

zuul不仅拥有服务路由功能，还具有服务过滤的功能

如下通过集成ZuulFilter来实现安全的过滤功能
```
@Component
public class MyFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(MyFilter.class);

    /**
     * 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
     * pre：路由之前
     * routing：路由之时
     * post： 路由之后
     * error：发送错误调用
     **/
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 过滤的顺序
     **/
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 这里可以写逻辑判断，是否要过滤，此处返回true,表示永远过滤。
     **/
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
     **/
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        logger.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
        Object accessToken = request.getParameter("token");
        if (accessToken == null) {
            logger.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                ctx.getResponse().getWriter().write("token is empty");
            } catch (Exception e) {
            }

            return null;
        }
        logger.info("ok");
        return null;
    }

}
```

**功能测试**

1.输入：http://localhost:8769/api-a/hi?name=zhangsan

> token is empty

2.输入：http://localhost:8769/api-b/hi?name=zhangsan&token=1

> hi zhangsan ,i am from port:8763

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/service_zuul

**参考博客：**
https://blog.csdn.net/forezp/article/details/81041012
