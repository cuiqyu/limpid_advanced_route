# 九. 高可用的服务注册中心

> **声明：**本文转载自方志朋的博客 [原文链接：] http://blog.csdn.net/forezp/article/details/81041101

**前言：** 在spring_cloud入门第一篇，就是介绍使用Eureka_Server来担任分布式服务注册中心的角色，而启用的`eureka_server`注册中心服务是一个实例。但是当如果一个项目有成千上万的服务实例的时候，很明显这个注册中心服务的负载是不够的。那么此时就需要考虑将Eureka_Server也来实现集群化管理。

> **官网介绍：** Eureka can be made even more resilient and available by running multiple instances and asking them to register with each other. In fact, this is the default behaviour, so all you need to do to make it work is add a valid serviceUrl to a peer, e.g.
> 
>> 通过运行多个实例并要求它们彼此注册，Eureka可以变得更具弹性和可用性。事实上，这是默认行为，因此要使其正常工作，您只需向对等方添加一个有效的serviceUrl，例如，通过运行多个实例并要求它们彼此注册，Eureka可以变得更具弹性和可用性。事实上，这是默认行为，因此要使其正常工作，只需向对等方添加一个有效的serviceUrl，例如。

**改造项目：**

改造`eureka_server`项目

在resources下新建`application-peer1.yml`
```
spring:
  profiles: peer1
  application:
    name: eureka-server
server:
  port: 8761
eureka:
  instance:
    hostname: peer1
  client:
    serviceUrl:
      defaultZone: http://peer2:8769/eureka/
```

在resources下新建`application-peer2.yml`
```
spring:
  profiles: peer2
  application:
    name: eureka-server
server:
  port: 8769
eureka:
  instance:
    hostname: peer2
  client:
    serviceUrl:
      defaultZone: http://peer1:8761/eureka/
```

这时候项目改造完毕了。
> ou could use this configuration to test the peer awareness on a single host (there’s not much value in doing that in production) by manipulating /etc/hosts to resolve the host names.
> -摘自官网

按照官方文档的指示，需要改变etc/hosts，linux系统通过vim /etc/hosts ,加上：
```
127.0.0.1 peer1
127.0.0.1 peer2
```

这时候为了测试eureka_server是否改造成功，需要修改`eureka_client`项目的配置文件
```
server:
  port: 8762

spring:
  application:
    name: service-client

eureka:
  client:
    serviceUrl:
      defaultZone: http://peer1:8761/eureka/
```

**开始启动项目测试：**

1. 分别使用`applicaiton-peer1.yml`和`application-peer2.yml`启动两个`eureka_server`项目。
```
java -jar eureka_server-0.0.1-SNAPSHOT.jar - -spring.profiles.active=peer1
java -jar eureka_server-0.0.1-SNAPSHOT.jar - -spring.profiles.active=peer2
```

2. 启动`eureka_client`项目。

浏览器访问：http://localhost:8761/ 界面效果如下：
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/368B1BF4E46249C7AA9E7C123037BE5A/18615)

浏览器访问：http://localhost:8769/ 界面效果如下：
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/B14626DD3E7A4A3B9D00E0BC1325B084/18616)

你会发现注册了service-client，并且有个peer2节点，同理访问localhost:8769你会发现有个peer1节点。

client只向8761注册，但是你打开8769，你也会发现，8769也有 client的注册信息。

**如上发现一个问题：** 每日需要手动修改hostname，相对来说比较麻烦。
> In some cases, it is preferable for Eureka to advertise the IP Adresses of services rather than the hostname. Set eureka.instance.preferIpAddress to true and when the application registers with eureka, it will use its IP Address rather than its hostname.
-- 摘自官网
>> 在某些情况下，Eureka最好公布服务的IP地址，而不是主机名。将eureka.instance.preferIpAddress设置为true，当应用程序向eureka注册时，它将使用其IP地址而不是主机名。

此时的架构图：
![](https://note.youdao.com/yws/public/resource/8a3bdadc14ca85b7eddc14be9dc18bf5/xmlnote/C307535CFB414C9AB51C806A1F6F5AB1/18618)

Eureka-server peer1 8761,Eureka-eserver peer2 8769相互感应，当有服务注册时，两个Eureka-server是对等的，它们都存有相同的信息，这就是通过服务器的冗余来增加可靠性，当有一台服务器宕机了，服务并不会终止，因为另一台服务存有相同的数据。

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/eureka_server

