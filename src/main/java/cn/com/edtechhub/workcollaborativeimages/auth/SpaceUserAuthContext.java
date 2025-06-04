package cn.com.edtechhub.workcollaborativeimages.auth;

import cn.com.edtechhub.workcollaborativeimages.model.entity.Picture;
import cn.com.edtechhub.workcollaborativeimages.model.entity.Space;
import cn.com.edtechhub.workcollaborativeimages.model.entity.SpaceUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于表示用户在特定空间内的授权上下文信息
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
@Data
@Slf4j
public class SpaceUserAuthContext {

    /**
     * 临时标识, 不同请求的 id 可能不同
     */
    private Long id;

    /**
     * 图片 ID
     */
    private Long pictureId;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 空间用户 ID
     */
    private Long spaceUserId;

}
