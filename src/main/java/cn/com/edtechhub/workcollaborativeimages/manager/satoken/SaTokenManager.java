package cn.com.edtechhub.workcollaborativeimages.manager.satoken;

import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnum;
import cn.com.edtechhub.workcollaborativeimages.manager.auth.SpaceUserAuthContext;
import cn.com.edtechhub.workcollaborativeimages.manager.auth.SpaceUserAuthManager;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Sa-token 管理器
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class SaTokenManager implements StpInterface {

    /**
     * 注入 spaceUserAuthManager 管理依赖
     */
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 制作空的角色标识集合
        List<String> list = new ArrayList<>();

        // 获取当前登录用户信息
        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE); // 直接从会话缓存中获取用户的所有信息
        UserRoleEnum userRole = UserRoleEnum.getEnums(user.getRole()); // 由于在本数据库中为了拓展性使用数字来标识身份, 因此需要做一层转化

        // 返回角色标识集合
        if (userRole != null) {
            list.add(userRole.getDescription());
        }
        log.debug("本次调用用户携带的角色标识集合为: {}", list);
        return list;
    }

    /**
     * 返回一个账号所拥有的权限码值集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 制作空的权限码值集合
        List<String> list = new ArrayList<>();

        // 获取当前登录用户信息
        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE); // 直接从会话缓存中获取用户的所有信息
        SpaceUserAuthContext authContext = spaceUserAuthManager.getSpaceUserAuthContextByRequest(); // 利用上下文来获取重要的 id 值以支持某些接口可以在某些情况下绕过权限码值集合的判断

        // 返回权限码值集合
        if (authContext != null) {
            list = spaceUserAuthManager.getPermissionListByLoginUserId(authContext, user.getId());
        }
        log.debug("本次调用用户携带的的权限码值集合为 {}", list);
        return list;
    }

}
