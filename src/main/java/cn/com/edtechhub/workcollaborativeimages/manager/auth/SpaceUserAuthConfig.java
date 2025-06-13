package cn.com.edtechhub.workcollaborativeimages.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * SpaceAuth 配置
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class SpaceUserAuthConfig {

    /**
     * 加载配置
     */
    public static final SpaceUserAuth SPACE_USER_AUTH_CONFIG;

    // 使用静态代码块在类加载时执行一次初始化静态变量以读取定义的空间角色和空间权限
    static {
        String json = ResourceUtil.readUtf8Str("biz/space-user-auth-config.json"); // 读取配置文件中的 json 配置
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuth.class);
    }

    /**
     * 获取配置
     */
    public SpaceUserAuth getConfig() {
        return SPACE_USER_AUTH_CONFIG;
    }

    /**
     * 打印配置
     */
    @PostConstruct
    public void printConfig() {
        log.debug("[SpaceUserAuthConfig] 当前项目 SpaceUserAuth 读取到的配置文件内容为: {}", SpaceUserAuthConfig.SPACE_USER_AUTH_CONFIG);
    }

}
