package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceUserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sa-token 管理器
 * 需要保证此类被 SpringBoot 扫描, 完成 Sa-Token 的自定义权限验证扩展
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Component
@Slf4j
public class SaTokenManager implements StpInterface {

    /**
     * 注入 SpaceUserAuth 管理依赖
     */
    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    /**
     * 注入用户服务依赖
     */
    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        List<String> list = new ArrayList<>();

        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE); // 直接从会话缓存中获取用户的所有信息
        UserRoleEnums userRole = UserRoleEnums.getEnums(user.getRole()); // 由于在本数据库中为了拓展性使用数字来标识身份, 因此需要做一层转化
        if (userRole != null) {
            list.add(userRole.getDescription());
            log.debug("本次调用用户携带的角色标识集合为: {}", list);
        }

        return list;
    }

    /**
     * 返回一个账号所拥有的权限码值集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        List<String> list = new ArrayList<>();

        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE); // 直接从会话缓存中获取用户的所有信息
        List<SpaceUser> spaceUserList = spaceUserService.spaceUserSearch(new SpaceUserSearchRequest().setUserId(user.getId())).getRecords();
        if (spaceUserList != null) {
            Integer spaceRole = spaceUserList.get(0).getSpaceRole();
            list = spaceUserAuthManager.getPermissionsByRole(Objects.requireNonNull(SpaceUserRoleEnums.getEnums(spaceRole)));
            log.debug("本次调用用户携带的的权限码值集合为 {}", list);
        }

        return list;
    }

}
