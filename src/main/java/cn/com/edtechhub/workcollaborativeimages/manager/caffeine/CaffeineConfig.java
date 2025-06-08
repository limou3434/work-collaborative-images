package cn.com.edtechhub.workcollaborativeimages.manager.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 线程缓存管理器
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "caffeine")
public class CaffeineConfig {

    private Integer initialCapacity;
    /**
     * 构造一个 Caffeine 缓存器
     */
    private final Cache<String, String> cache = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10000L)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

}
