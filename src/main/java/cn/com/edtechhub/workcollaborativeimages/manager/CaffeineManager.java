package cn.com.edtechhub.workcollaborativeimages.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
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
public class CaffeineManager {

    /**
     * 构造一个 Caffeine 缓存器
     */
    private final Cache<String, String> cache = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10000L)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    /**
     * 插入键值对
     */
    public void put(String key, String value) {
        this.cache.put(key, value);
    }

    /**
     * 获得键值堆
     */
    public String get(String key) {
        return this.cache.getIfPresent(key);
    }

    /**
     * 删除键值对
     */
    public void remove(String key) {
        this.cache.invalidate(key);
    }

    /**
     * 清理键值对
     */
    public void clearAll() {
        this.cache.invalidateAll();
    }

    /**
     * 查看键值对
     */
    public Map<String, String> dumpCache() {
        return this.cache.asMap();
    }

}
