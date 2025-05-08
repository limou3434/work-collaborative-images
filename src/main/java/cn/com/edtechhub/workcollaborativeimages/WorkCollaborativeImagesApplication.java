package cn.com.edtechhub.workcollaborativeimages;

import cn.com.edtechhub.workcollaborativeimages.constant.OpenApiConstant;
import cn.com.edtechhub.workcollaborativeimages.constant.ServerConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 * 如果使用 IDEA 需要在启动前配置 .env 后才能启动
 * 如果使用 jdk 运行环境需要书写对应的环境变量才能启动
 * 如果使用 Docker 容器需要书写对应的环境变量才能启动
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@SpringBootApplication
@Slf4j
public class WorkCollaborativeImagesApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(WorkCollaborativeImagesApplication.class, args);
        ServerConstant serverConstant = context.getBean(ServerConstant.class);
        OpenApiConstant springdocConfig = context.getBean(OpenApiConstant.class);
        String baseUrl = "http://" + serverConstant.getAddress() + ":" + serverConstant.getPort() + serverConstant.getContextPath();
        log.debug("访问 {} 或 {} 即可得到在线文档, 访问 {} 即可得到文档配置", baseUrl + springdocConfig.getKnife4jUi(), baseUrl + springdocConfig.getSwaggerUi(), baseUrl + springdocConfig.getApiDocsJson());
        log.debug("Spring Boot running...");
    }

}
