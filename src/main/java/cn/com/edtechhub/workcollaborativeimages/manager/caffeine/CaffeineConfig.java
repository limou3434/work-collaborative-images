package cn.com.edtechhub.workcollaborativeimages.manager.caffeine;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine 配置类
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Data
@Slf4j
@ConfigurationProperties(prefix = "caffeine")
public class CaffeineConfig {

    /**
     * 初始大小
     */
    private Integer initialCapacity = 1024;

    /**
     * 最大缓存
     */
    private Long maximumSize = 10000L;

    /**
     * 过期时间
     */
    private Integer expireAfterWrite = 30;

}
