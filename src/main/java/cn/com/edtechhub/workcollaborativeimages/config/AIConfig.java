package cn.com.edtechhub.workcollaborativeimages.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * AI 配置
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Configuration
@ConfigurationProperties(prefix = "aliyunai")
@Data
@Slf4j
public class AIConfig {

    /**
     * 密钥的值
     */
    private String apiKey;

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("当前项目 Aliyun AI 密钥已加载为 {}", apiKey);
    }

}