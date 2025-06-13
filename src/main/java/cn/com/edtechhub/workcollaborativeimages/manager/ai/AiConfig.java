package cn.com.edtechhub.workcollaborativeimages.manager.ai;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * AI 配置类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Configuration
@ConfigurationProperties(prefix = "aliyunai")
@Data
@Slf4j
public class AiConfig {

    /**
     * 密钥
     */
    private String apiKey;

    /**
     * 创建扩图任务地址
     */
    private String createOutPaintingTaskUrl = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    /**
     * 查询扩图任务地址
     */
    private String getOutPaintingTaskUrl = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[AiConfig] 当前项目 AI 密钥已加载为 {}", apiKey);
    }

}