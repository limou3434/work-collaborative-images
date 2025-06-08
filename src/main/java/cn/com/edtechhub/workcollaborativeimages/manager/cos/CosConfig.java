package cn.com.edtechhub.workcollaborativeimages.manager.cos;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 对象存储配置
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
@Slf4j
public class CosConfig {

    /**
     * 访问域名
     */
    private String host;

    /**
     * 密钥标识
     */
    private String secretId;

    /**
     * 密钥的值
     */
    private String secretKey;

    /**
     * 地域简称
     */
    private String region;

    /**
     * 存储桶名
     */
    private String bucket;

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[CosConfig] 当前项目 COS 访问域名 {}", host);
        log.debug("[CosConfig] 当前项目 COS 地域简称 {}", region);
        log.debug("[CosConfig] 当前项目 COS 存储桶名 {}", bucket);
    }

}