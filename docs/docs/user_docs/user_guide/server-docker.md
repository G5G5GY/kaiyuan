### Hippo4J Server Docker镜像构建
可以通过以下命令快速构建Hippo4J Server：
方式一：
```
//进入到hippo4j-server工程路径下
mvn clean package
//默认打包是打包的tag是latest
docker build -t hippo4j-server ../hippo4j-server
```
方式二：
通过maven docker plugin
```
//进入到hippo4j-server工程路径下
mvn clean package -DskipTests docker:build
```
#### Docker 镜像方式搭建Hippo4J Server：

- 下载镜像

```
// Docker地址：https://hub.docker.com/r/xxxx/hippo4j-server/     (建议指定版本号)
docker pull hippo4j-server
```

- 创建容器并运行

```
docker run -p 6691:6691 --name hippo4j-server -d hippo4j-server:{指定版本}

/**
* 如需自定义 mysql 等配置，可通过 "-e PARAMS" 指定，参数格式 PARAMS="--key=value  --key2=value2" ；
* 配置项参考文件：/hippo4j-server/src/main/resources/application.properties
* 如需自定义 JVM内存参数 等配置，可通过 "-e JAVA_OPTS" 指定，参数格式 JAVA_OPTS="-Xmx512m" ；
*/
docker run -e PARAMS="--spring.datasource.url=jdbc:mysql://localhost:3306/hippo4j_manager?characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=GMT%2B8" -p 6691:6691  --name hippo4j-server  -d hippo4j-server:{指定版本}
```
