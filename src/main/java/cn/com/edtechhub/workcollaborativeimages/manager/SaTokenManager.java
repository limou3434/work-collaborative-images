package cn.com.edtechhub.workcollaborativeimages.manager;

import cn.com.edtechhub.workcollaborativeimages.auth.SpaceUserAuthContext;
import cn.com.edtechhub.workcollaborativeimages.constant.UserConstant;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceUserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.enums.UserRoleEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.model.entity.User;
import cn.com.edtechhub.workcollaborativeimages.model.request.spaceUserService.SpaceUserSearchRequest;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
        // 制作空的角色标识集合
        List<String> list = new ArrayList<>();

        // 返回角色标识集合
        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE); // 直接从会话缓存中获取用户的所有信息
        UserRoleEnums userRole = UserRoleEnums.getEnums(user.getRole()); // 由于在本数据库中为了拓展性使用数字来标识身份, 因此需要做一层转化
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
    public List<String> getPermissionList(Object loginId, String loginType) { // loginType 可以用来区分不同客户端
        // 制作空的权限码值集合
        List<String> list = new ArrayList<>();

        // 返回权限码值集合
        SpaceUserAuthContext authContext = this.getSpaceUserAuthContextByRequest(); // 利用上下文来获取重要的 id 值以支持某些接口可以在某些情况下绕过权限码值集合的判断
        if (isAllFieldsNull(authContext)) { // 如果上下文字段全为 NULL 则直接允许该接口使用, 在本项目中就是指允许公共图库中的接口操作
            return spaceUserAuthManager.getPermissionsByRole(SpaceUserRoleEnums.MANGER_ROLE);
        } else { // 否则说明就是协作空间中使用了接口
            User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE); // 直接从会话缓存中获取用户的所有信息
            List<SpaceUser> spaceUserList = spaceUserService.spaceUserSearch(new SpaceUserSearchRequest().setUserId(user.getId())).getRecords();
            if (spaceUserList != null) {
                Integer spaceRole = spaceUserList.get(0).getSpaceRole();
                list = spaceUserAuthManager.getPermissionsByRole(Objects.requireNonNull(SpaceUserRoleEnums.getEnums(spaceRole)));
            }
        }
        log.debug("本次调用用户携带的的权限码值集合为 {}", list);
        return list;
    }

    /**
     * 从本次请求中获取上下文对象
     */
    private SpaceUserAuthContext getSpaceUserAuthContextByRequest() {
        // 获取当前线程绑定的请求上下文(HttpServletRequest 的 body 值是个流只支持读取一次, 为了重复读取还需要在 config 包下自定义 RequestWrapperConfig 和 HttpRequestWrapperFilterConfig )
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 获取请求报头中的 Content-Type 参数值
        String contentType = request.getHeader(Header.CONTENT_TYPE.getValue());

        // 填充空间用户关联的上下文(这里兼容 get 和 post 操作)
        SpaceUserAuthContext spaceUserAuthContext; // 存储请求中参数来得到上下文
        if (ContentType.JSON.getValue().equals(contentType)) {
            log.debug("这是一个 POST 请求, 正在尝试获取 SpaceUserAuthContext");
            String body = ServletUtil.getBody(request);
            spaceUserAuthContext = JSONUtil.toBean(body, SpaceUserAuthContext.class);
        } else {
            log.debug("这是一个 GET 请求, 正在尝试获取 SpaceUserAuthContext");
            Map<String, String> paramMap = ServletUtil.getParamMap(request);
            spaceUserAuthContext = BeanUtil.toBean(paramMap, SpaceUserAuthContext.class);
        }
        log.debug("最终得到的上下文请求 SpaceUserAuthContext 为 {}", spaceUserAuthContext);
        return spaceUserAuthContext;
    }

    /**
     * 判断上下文中的字段是否全为 NULL
     */
    private boolean isAllFieldsNull(Object object) {
        if (object == null) {
            return true; // 对象本身为空
        }
        // 利用反射来获取所有字段并判断是否所有字段都为空
        return Arrays
                .stream(ReflectUtil.getFields(object.getClass()))
                .map(field -> ReflectUtil.getFieldValue(object, field)) // 获取字段值
                .allMatch(ObjectUtil::isEmpty); // 检查是否所有字段都为空
    }

}
