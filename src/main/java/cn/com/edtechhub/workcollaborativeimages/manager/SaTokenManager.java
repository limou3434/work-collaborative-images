package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
     * 注入用户服务实例
     */
    @Resource
    private UserService userService;

    /**
     * 返回一个账号所拥有的权限码值集合(暂时没有用到)
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        // 直接从会话缓存中获取用户的所有信息
        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE);
        UserRoleEnums userRole = UserRoleEnums.getUserDescription(user.getRole()); // 由于在本数据库中为了拓展性使用数字来标识身份, 因此需要做一层转化
        log.debug("检测一次当前用户的身份名称: {}", userRole.getDescription());
        List<String> list = new ArrayList<>();
        list.add(userRole.getDescription());
        return list;
    }

}
