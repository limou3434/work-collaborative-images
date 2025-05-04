package cn.com.edtechhub.workcollaborativeimages;

import cn.com.edtechhub.workcollaborativeimages.constant.OpenApiConstant;
import cn.com.edtechhub.workcollaborativeimages.constant.ServerConstant;
import cn.dev33.satoken.SaManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
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

        log.debug("Spring Boot running...");
        log.debug("访问 {} 或 {} 即可得到在线文档, 访问 {} 即可得到文档配置", baseUrl + springdocConfig.getKnife4jUi(), baseUrl + springdocConfig.getSwaggerUi(), baseUrl + springdocConfig.getApiDocsJson());
        log.debug("读取 Sa-token 配置查验是否正确: {}", SaManager.getConfig());
        log.debug("读取 Sa-token 切面类查验是否被替换为自己的: {}", SaManager.getStpInterface());
    }

}
