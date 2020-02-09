# 六. 分布式配置中心（Spring Cloud Config）

> **前言：** 在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。在Spring Cloud中，有分布式配置中心组件spring cloud config ，它支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。在spring cloud config 组件中，分两个角色，一是config server，二是config client。

**1. 新建Config_Server项目：**

在`springcloud_learning`父项目下新建子模块`config_server`项目，添加`web依赖`、`config-server依赖`。

**添加依赖：**
```
<groupId>com.limpid</groupId>
<artifactId>config_server</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>

<name>config_server</name>
<description>使用spring cloud config来管理配置中心</description>

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
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

**启动配置**
```
/**
 * 使用@EnableConfigServer开启配置服务器的功能
 **/
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
```

**配置文件**
```
spring:
  application:
    name: config_server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/cuiqyu/limpid_advanced_route
          searchPaths: springcloud_learning/config_server/config_repo
          # 如果连接的仓库是公开的，则此处不需要输入用户名与密码
          # username: *****
          # password: *****
      label: master
server:
  port: 8888
```

**1. spring.cloud.config.server.git.uri：** 配置git仓库地址

**2. spring.cloud.config.server.git.searchPaths：** 配置仓库路径

**3. spring.cloud.config.label：** 配置仓库的分支

**4. spring.cloud.config.server.git.username：** 访问git仓库的用户名

**5. spring.cloud.config.server.git.password：** 访问git仓库的用户密码

**结果验证：**
启动config_server项目，浏览器地址栏输入`http://localhost:8888/foo/dev`
> {"name":"foo","profiles":["dev"],"label":null,"version":"0e86f901359d89758bbbc03302d29135daa2584d","state":null,"propertySources":[]}

从结果可以看出，证明配置服务中心可以从远程程序获取配置信息。

http请求地址和资源文件映射如下:

* /{application}/{profile}[/{label}]
* /{application}-{profile}.yml
* /{label}/{application}-{profile}.yml
* /{application}-{profile}.properties
* /{label}/{application}-{profile}.properties

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/config_server

**2. 新建config_client项目：**
新建一个config_client项目，用来从config_server中读取远程的配置文件
依旧是在父项目`springcloud_learning`下新建子项目`config_client`，项目需要添加web依赖和`start-config`依赖。

**添加依赖**
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
</dependencies>
```

**启动配置：**
无需特定的注解匹配支持，直接通过默认的springboot启动方式启动服务即可
```
@SpringBootApplication
@RestController
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

**配置文件**
新建`bootstrap.yml`配置文件，并配置以下内容:
```
spring:
  application:
    name: config-client
  cloud:
    config:
      # 指明远程仓库的分支
      label: master
      # 指定配置文件的环境（dev：开发环境，test：测试环境，pro正式环境）
      profile: dev
      # 指明配置服务中心的网址
      uri: http://localhost:8888/
server:
  port: 8881
```
**问1：** 关于为什么要在`bootstrap.yml`配置文件中配置，而不是直接在`application.yml`配置，通过查阅资料解答如下：

**答1：**
> 当使用 Spring Cloud Config Server 的时候，你应该在 bootstrap.yml 里面指定 spring.application.name 和 spring.cloud.config.server.git.uri和一些加密/解密的信息
> **加载过程：**在Spring Cloud Config 项目 中configclient 服务启动后，默认会先访问bootstrap.yml，然后绑定configserver，然后获取application.yml 配置。如果仅仅在application.yml 配置了url:http://127.0.0.1:8080 这样默认会使用8888端口（配置无效）。 所以， 我们将绑定configserver的配置属性应该放在bootstrap.yml文件里。

**问2：** 为何需要把 config server 的信息放在 bootstrap.yml 里？

**答2：**
> 1. 当使用 Spring Cloud 的时候，配置信息一般是从 config server 加载的，为了取得配置信息（比如密码等），你需要一些提早的或引导配置。因此，把 config server 信息放在 bootstrap.yml，用来加载真正需要的配置信息。
> 2. 这是由spring boot的加载属性文件的优先级决定的，你想要在加载属性之前去spring cloud config server上取配置文件，那spring cloud config相关配置就是需要最先加载的，而bootstrap.properties的加载是先于application.properties的，所以config client要配置config的相关配置就只能写到bootstrap.properties里了。

**问3：** bootstrap.yml（bootstrap.properties）与application.yml（application.properties）执行顺序

**答3：**
> 1. bootstrap.yml（bootstrap.properties）用来程序引导时执行，应用于更加早期配置信息读取，如可以使用来配置application.yml中使用到参数等
> 2. application.yml（application.properties) 应用程序特有配置信息，可以用来配置后续各个模块中需使用的公共参数等。
> 3. bootstrap.yml 先于 application.yml 加载.
> 4. 技术上，bootstrap.yml 是被一个父级的 Spring ApplicationContext 加载的。这个父级的 Spring ApplicationContext是先加载的，在加载application.yml 的 ApplicationContext之前。

**结果验证：**
浏览器输入config_client提供的接口地址 `http://localhost:8881/hi`,直接从config_server中读取了远程的配置内容。配置内容读取图实例：**`git` -> `configService` <- `configClient`**

> foo version 3

**以上代码详见：** https://github.com/cuiqyu/limpid_advanced_route/tree/master/springcloud_learning/config_client