package cn.com.edtechhub.workcollaborativeimages.annotation;

import java.lang.annotation.*;

/**
 * 查询接口远端缓存优化注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheSearchOptimization {
    long ttl() default 5; // 默认缓存时间单位秒
}
