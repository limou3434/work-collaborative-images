package cn.com.edtechhub.workcollaborativeimages.interceptor;

import cn.com.edtechhub.workcollaborativeimages.manager.SpaceUserAuthManager;
import cn.com.edtechhub.workcollaborativeimages.service.PictureService;
import cn.com.edtechhub.workcollaborativeimages.service.SpaceService;
import cn.com.edtechhub.workcollaborativeimages.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceUserAuthManager spaceUserAuthManager;

    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) {
//        if (request instanceof ServletServerHttpRequest) {
//            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//            // 获取请求参数
//            String pictureId = servletRequest.getParameter("pictureId");
//            if (StrUtil.isBlank(pictureId)) {
//                log.error("缺少图片参数，拒绝握手");
//                return false;
//            }
//            User loginUser = userService.getLoginUser(servletRequest);
//            if (ObjUtil.isEmpty(loginUser)) {
//                log.error("用户未登录，拒绝握手");
//                return false;
//            }
//            // 校验用户是否有该图片的权限
//            Picture picture = pictureService.getById(pictureId);
//            if (picture == null) {
//                log.error("图片不存在，拒绝握手");
//                return false;
//            }
//            Long spaceId = picture.getSpaceId();
//            Space space = null;
//            if (spaceId != null) {
//                space = spaceService.getById(spaceId);
//                if (space == null) {
//                    log.error("空间不存在，拒绝握手");
//                    return false;
//                }
//                if (space.getSpaceType() != SpaceTypeEnum.TEAM.getValue()) {
//                    log.info("不是团队空间，拒绝握手");
//                    return false;
//                }
//            }
//            List<String> permissionList = spaceUserAuthManager.getPermissionList(space, loginUser);
//            if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
//                log.error("没有图片编辑权限，拒绝握手");
//                return false;
//            }
//            // 设置 attributes
//            attributes.put("user", loginUser);
//            attributes.put("userId", loginUser.getId());
//            attributes.put("pictureId", Long.valueOf(pictureId)); // 记得转换为 Long 类型
//        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {
    }

}
