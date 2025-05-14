package cn.com.edtechhub.workcollaborativeimages.config;

import cn.com.edtechhub.workcollaborativeimages.aop.RequestLogAOP;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 跨域共享配置
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Configuration
public class CrossDomainConfig implements WebMvcConfigurer {

    /**
     * 注入请求日志拦截切面依赖
     */
    @Resource
    private RequestLogAOP requestLogAOP;

    /**
     * 配合切面拦截所有接口调用以提供详细的访问日志打印
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogAOP).addPathPatterns("/**");
    }
    /**
     * 配置跨域共享
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOriginPatterns(
                        "http://127.0.0.1:3000",
                        "http://10.10.174.232",
                        "https://wci.edtechhub.com.cn"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
