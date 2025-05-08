package cn.com.edtechhub.workcollaborativeimages.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.interceptor.SaInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-token 配置
 */
@Configuration
@Slf4j
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器, 打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
        log.debug("读取 Sa-token 配置查验是否正确: {}", SaManager.getConfig());
        log.debug("读取 Sa-token 切面类查验是否被替换为自己的: {}", SaManager.getStpInterface());
    }

}