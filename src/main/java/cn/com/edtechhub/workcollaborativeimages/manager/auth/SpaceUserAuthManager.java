package cn.com.edtechhub.workcollaborativeimages.manager.auth;

import cn.com.edtechhub.workcollaborativeimages.enums.SpaceTypeEnum;
import cn.com.edtechhub.workcollaborativeimages.enums.SpaceUserRoleEnum;
import cn.com.edtechhub.workcollaborativeimages.exception.CodeBindMessageEnums;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceUserService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import cn.com.edtechhub.workcollaborativeimages.utils.ThrowUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 空间用户关联认证管理器
 * 当请求设计到协作空间时就需要引入改管理器判断用户是否具有某些接口的权限
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
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
     * 注入图片服务依赖
     */
    @Resource
    PictureService pictureService;

    /**
     * 注入空间服务依赖
     */
    @Resource
    private SpaceService spaceService;

    /**
     * 注入空间用户关联服务依赖
     */
    @Resource
    private SpaceUserService spaceUserService;

    /**
     * 从本次请求中获取上下文对象(主要存储某些 id 值用于后续权限码值集合校验)
     */
    public SpaceUserAuthContext getSpaceUserAuthContextByRequest() {
        // 获取当前线程绑定的请求上下文(HttpServletRequest 的 body 值是个流只支持读取一次, 为了重复读取还需要在 config 包下自定义 RequestWrapperConfig 和 HttpRequestWrapperFilter )
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

        // 填充模块
        String requestUri = request.getRequestURI(); // 获取当前 HTTP 请求的 URI
        String partUri = StrUtil.removePrefix(requestUri, "/"); // 去除 URL 最前面的 "/"
        String moduleName = StrUtil.subBefore(partUri, "/", false);
        log.debug("本次请求的 URL 提取过程为 {} -> {} -> {}", requestUri, partUri, moduleName);
        log.debug("本次上下中的临时 id 是 {} 接口中的请求 id", moduleName);
        spaceUserAuthContext.setControlModule(moduleName);

        // 解析临时 id 来进行填充
        Long id = spaceUserAuthContext.getId();
        if (ObjUtil.isNotNull(id)) { // 根据请求路径区分 id 字段的含义
            log.debug("尝试填充临时的 id 值");
            switch (moduleName) {
                case "space_user":
                    spaceUserAuthContext.setSpaceUserId(id);
                    break;
                case "picture":
                    spaceUserAuthContext.setPictureId(id);
                    break;
                case "space":
                    spaceUserAuthContext.setSpaceId(id);
                    break;
                default:
            }
        }
        log.debug("最终得到的上下文请求 SpaceUserAuthContext 为 {}", spaceUserAuthContext);
        return spaceUserAuthContext;
    }

    /**
     * 从本次请求获取的上下文对象中提取权限码值集合
     */
    public List<String> getPermissionListById(SpaceUserAuthContext authContext, Long currentLonginUserId) {
        Long spaceId = authContext.getSpaceId();
        Long pictureId = authContext.getPictureId();
        Long userId = authContext.getUserId();
        String controlModule = authContext.getControlModule();
        List<String> pass = this.getPermissionsByRole(SpaceUserRoleEnum.MANGER_ROLE);

        // 如果上下文什么字段都没有则直接放开所有权限
        if (this.isAllFieldsNull(authContext)) {
            return pass;
        }

        // 如果是和空间用户关联控制器相关的请求(例如 移入用户、移出用户、编辑用户 接口)
        // 如果是和空间控制器相关的请求(例如 团队成员查询查询协作空间 接口)
        if (controlModule.equals("space_user")) {
            // 无论是哪一个请求都必定会要求携带 spaceId
            Space space = spaceService.spaceSearchById(spaceId);
            ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该空间不存在");
            ThrowUtils.throwIf(SpaceTypeEnum.getEnums(space.getType()) != SpaceTypeEnum.COLLABORATIVE, CodeBindMessageEnums.PARAMS_ERROR, "该空间不是协作空间无法操作");
            SpaceUser spaceUser = spaceUserService.spaceUserSearchById(spaceId, currentLonginUserId);
            ThrowUtils.throwIf(spaceUser == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该协作空间的相关成员无法进行相关操作");
            return this.getPermissionsByRole(SpaceUserRoleEnum.getEnums(spaceUser.getSpaceRole()));
        }

        // 如果是和图片相关的请求(例如 上传图片、删除图片、查看图片 接口)
        if (controlModule.equals("picture") || controlModule.equals("space")) {
            // 上传图片可能携带 spaceId, 如果有则有可能是要上传到协作空间, 有权限才可以上传
            if (spaceId != null) {
                Space space = spaceService.spaceSearchById(spaceId);
                ThrowUtils.throwIf(space == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该空间不存在");
                if (SpaceTypeEnum.getEnums(space.getType()) != SpaceTypeEnum.COLLABORATIVE) {
                    return pass;
                }
                SpaceUser spaceUser = spaceUserService.spaceUserSearchById(spaceId, currentLonginUserId);
                ThrowUtils.throwIf(spaceUser == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该协作空间的相关成员无法进行相关操作");
                return this.getPermissionsByRole(SpaceUserRoleEnum.getEnums(spaceUser.getSpaceRole()));
            }

            // 销毁图片只需要携带 pictureId, 查验这张图片有没有所属空间
            if (pictureId != null) {
                Picture picture = pictureService.pictureSearchById(pictureId);
                ThrowUtils.throwIf(picture == null, CodeBindMessageEnums.NOT_FOUND_ERROR, "该图片不存在");
                Long spaceIdOfPicture = picture.getSpaceId();
                if (spaceIdOfPicture == null) { // 没有所属空间就放行所有权限
                    return pass;
                }
                Space space = spaceService.spaceSearchById(spaceIdOfPicture);
                if (SpaceTypeEnum.getEnums(space.getType()) != SpaceTypeEnum.COLLABORATIVE) { // 所属空间不为协作空间就放行所有权限
                    return pass;
                }
                SpaceUser spaceUser = spaceUserService.spaceUserSearchById(space.getId(), currentLonginUserId);
                ThrowUtils.throwIf(spaceUser == null, CodeBindMessageEnums.ILLEGAL_OPERATION_ERROR, "您不是该协作空间的相关成员无法进行相关操作");
                return this.getPermissionsByRole(SpaceUserRoleEnum.getEnums(spaceUser.getSpaceRole()));
            }

            // 查看图片需要获取图片记录
            // (1)如果有携带 pictureId 则需要检查图片是否有所属空间, 所属空间为协作空间则需要检查权限
            // (2)如果没携带 pictureId 一般都是获取多个图片的接口, 因此就需要看是否有携带 spaceId, 那么又可以复用前面上传图片的代码...妙吖...这我竟然也写得出来

            // TODO: 不过这里有个点权限校验之前就查询了一次数据库, 后续请求还需要查询数据库, 可以使用线程缓存来解决这个问题, 线程缓存管理器我已经做好, 有时间可以过来优化
        }

        return pass;
    }

    /**
     * 根据角色获取对应的权限列表(必须要求记录不为 null, 提高权限的安全性)
     */
    private List<String> getPermissionsByRole(SpaceUserRoleEnum spaceUserRoleEnum) {
        ThrowUtils.throwIf(spaceUserRoleEnum == null, CodeBindMessageEnums.PARAMS_ERROR, "空间角色枚举参数不能为空");
        String spaceUserRole = spaceUserRoleEnum.getDescription();
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
