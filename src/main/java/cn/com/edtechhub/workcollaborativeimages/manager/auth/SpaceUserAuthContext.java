package cn.com.edtechhub.workcollaborativeimages.manager.auth;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述用户在特定空间内的授权上下文信息, 用于后续进行权限判断
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Slf4j
public class SpaceUserAuthContext {

    /**
     * 临时标识(不同请求的 id 可能不同, 但是这个 id 最终的目的是为了填充后面的某个 id)
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 图片 ID
     */
    private Long pictureId;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 空间用户关联 ID
     */
    private Long spaceUserId;

    /**
     * 当前请求的控制器所属模块
     */
    private String controlModule;

}
