package cn.com.edtechhub.workcollaborativeimages.aop;

import cn.com.edtechhub.workcollaborativeimages.annotation.CacheSearchOptimization;
import cn.com.edtechhub.workcollaborativeimages.constant.RedisConstant;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.manager.caffeine.CaffeineManager;
import cn.com.edtechhub.workcollaborativeimages.model.vo.PictureVO;
import cn.com.edtechhub.workcollaborativeimages.response.BaseResponse;
import cn.com.edtechhub.workcollaborativeimages.response.TheResult;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 查询接口远端缓存优化注解切面
 */
@Aspect
@Component
@Slf4j
public class CacheSearchOptimizationAOP { // TODO: 需要预防缓存击穿、缓存穿透、缓存雪崩

    /**
     * 引入 Caffeine 缓存管理依赖
     */
    @Resource
    private CaffeineManager caffeineManager;

    /**
     * 注入 Redis 缓存依赖
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate; // TODO: 可以考虑使用热点探测

    @Around("@annotation(cacheSearchOptimization)")
    public Object around(ProceedingJoinPoint joinPoint, CacheSearchOptimization cacheSearchOptimization) throws Throwable {
        log.debug(">>> [CacheSearchOptimizationAOP] 执行前");
        log.debug("检查 {}", cacheSearchOptimization);

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames(); // 参数名
        Object[] args = joinPoint.getArgs(); // 参数值

        // 遍历参数名和对应值
        for (int i = 0; i < parameterNames.length; i++) {
            log.debug("参数名: {}, 参数值: {}", parameterNames[i], args[i]);
        }

        // 构造 key 前缀
        String queryCondition = JSONUtil.toJsonStr(args[0]);
        log.debug("查询参数转化为 JSON 字符串 {}", queryCondition);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        log.debug("JSON 字符串经过加密后 {}", hashKey);
        String redisKey = RedisConstant.SEARCH_KEY_PREFIX + hashKey;
        log.debug("最终得到 Redis key 名 {}", redisKey);
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // 尝试获取本地缓存
        String localCachedValue = caffeineManager.get(redisKey);
        if (localCachedValue != null) {
            log.debug("本地缓存已命中 {}", localCachedValue);
            Page<PictureVO> cachedPage = JSONUtil.toBean(
                    localCachedValue,
                    new TypeReference<>() {
                    },
                    false
            ); // 使用 TypeReference 保留泛型
            log.debug("<<< [CacheSearchOptimizationAOP] 执行后");
            return TheResult.success(CodeBindMessageEnums.SUCCESS, cachedPage);
        } else {
            log.debug("本地缓存未命中");
        }

        // 尝试获取远端缓存, 并且更新本地缓存
        String remoteCachedValue = valueOps.get(redisKey);
        if (remoteCachedValue != null) {
            log.debug("远端缓存已命中 {}", remoteCachedValue);
            Page<PictureVO> cachedPage = JSONUtil.toBean(
                    remoteCachedValue,
                    new TypeReference<>() {
                    }
                    , false
            ); // 使用 TypeReference 保留泛型

            // 做二级缓存
            log.debug("更新一次本地缓存");
            caffeineManager.put(redisKey, remoteCachedValue); // 把远端缓存中的值更新到本地缓存
            log.debug("本地缓存中的所有结果 {}", caffeineManager.dumpCache());

            log.debug("<<< [CacheSearchOptimizationAOP] 执行后");
            return TheResult.success(CodeBindMessageEnums.SUCCESS, cachedPage);
        } else {
            log.debug("远端缓存未命中");
        }

        // 二级缓存中都没有获取到数据则执行原方法在数据库中查询
        Object result = joinPoint.proceed();

        // 添加一部分远端缓存, 把查询参数和查询结果加入远端缓存中, 暂时不更新缓存的目的是怕占用太多本地缓存空间
        log.debug("本次请求会把查询参数和查询结果加入远端缓存中");
        if (result instanceof BaseResponse) {
            BaseResponse<?> response = (BaseResponse<?>) result;
            Object data = response.getData();
            if (data instanceof Page<?>) {
                long ttlSeconds = cacheSearchOptimization.ttl(); // 获取用户配置的 ttl
                String toCache = JSONUtil.toJsonStr(data);
                valueOps.set(redisKey, toCache, Duration.ofSeconds(ttlSeconds)); // 缓存到远端缓存
                log.debug("将查询结果缓存进远端缓存中 {} -> {}", redisKey, toCache); // TODO: 后续的日志可能需要添加判断
                log.debug("远端缓存中的所有结果 {}", stringRedisTemplate.keys(RedisConstant.SEARCH_KEY_PREFIX + "*"));
            } // TODO: 这里如果 Redis 挂掉了还可以考虑存入本地缓存
            else {
                log.debug("返回值不是分页类型不缓存");
            }
        } else {
            log.warn("返回结果不是 BaseResponse，无法解析分页数据");
        }

        log.debug("<<< [CacheSearchOptimizationAOP] 执行后");
        return result;
    }

}
