package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.auth.SpaceUserRole;
import cn.com.edtechhub.workcollaborativeimages.config.SpaceUserAuthConfig;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceUserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SpaceUserAuthManager {

    /**
     * 注入 SpaceAuth 配置
     */
    @Resource
    private SpaceUserAuthConfig spaceUserAuthConfig;

    /**
     * 注入用户服务依赖
     */
    @Resource
    private UserService userService;

    /**
     * 注入空间用户关联服务依赖
     */
    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 根据角色获取对应的权限列表
     */
    public List<String> getPermissionsByRole(SpaceUserRoleEnums spaceUserRoleEnums) {
        ThrowUtils.throwIf(spaceUserRoleEnums == null, CodeBindMessageEnums.PARAMS_ERROR, "空间角色枚举参数不能为空");
        String spaceUserRole = spaceUserRoleEnums.getDescription();
        ThrowUtils.throwIf(StrUtil.isBlank(spaceUserRole), CodeBindMessageEnums.SYSTEM_ERROR, "空间角色非法");
        // 找到匹配的角色
        SpaceUserRole role = spaceUserAuthConfig
                .getConfig()
                .getRoles()
                .stream()
                .filter(r -> spaceUserRole.equals(r.getKey()))
                .findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

}
