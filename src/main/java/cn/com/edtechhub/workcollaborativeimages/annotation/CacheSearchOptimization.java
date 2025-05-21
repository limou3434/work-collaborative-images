package cn.com.edtechhub.workcollaborativeimages.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询接口远端缓存优化注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheSearchOptimization {
    long ttl() default 5; // 默认缓存时间单位秒
}
