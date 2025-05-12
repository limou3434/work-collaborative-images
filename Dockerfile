# 基础镜像
FROM openjdk:11-jdk-slim

# 编译代码(请在本地直接使用 ./mvnw clean package -DskipTests 进行编译避免容器过大, 可以提前使用 mvn wrapper:wrapper 安装包装器来跨环境编译)
COPY ./target/*.jar ./app.jar

# 运行端口
EXPOSE 8000

# 启动命令
CMD ["java", "-jar", "app.jar"]